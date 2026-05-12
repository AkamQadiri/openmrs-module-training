/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.training.api.validator;

import org.openmrs.annotation.Handler;
import org.openmrs.module.training.model.Exercise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Handler(supports = { Exercise.class }, order = 50)
public class ExerciseValidator implements Validator {
	
	@Autowired
	private TrainingJsonSchemaValidator jsonSchemaValidator;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return Exercise.class.isAssignableFrom(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		Exercise exercise = (Exercise) target;
		
		if (exercise == null) {
			errors.reject("training.error.general");
			return;
		}
		
		// Validate required fields
		ValidationUtils.rejectIfEmpty(errors, "exerciseType", "training.exercise.type.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "content", "training.exercise.content.required");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "validation", "training.exercise.validation.required");
		
		// At least one name is required
		if (exercise.getNames() == null || exercise.getNames().isEmpty()
		        || exercise.getNames().stream().allMatch(n -> n.getVoided())) {
			errors.rejectValue("names", "training.exercise.name.required");
		}
		
		// Validate content JSON schema
		if (exercise.getContent() != null && !exercise.getContent().trim().isEmpty() && exercise.getExerciseType() != null) {
			try {
				jsonSchemaValidator.validateExerciseContent(exercise.getContent(), exercise.getExerciseType());
			}
			catch (Exception e) {
				errors.rejectValue("content", "training.exercise.content.invalid", new Object[] { e.getMessage() },
				    "Invalid exercise content format");
			}
		}
		
		// Validate validation JSON
		if (exercise.getValidation() != null && !exercise.getValidation().trim().isEmpty()) {
			try {
				jsonSchemaValidator.validateExerciseValidation(exercise.getValidation());
			}
			catch (Exception e) {
				errors.rejectValue("validation", "training.exercise.validation.invalid", new Object[] { e.getMessage() },
				    "Invalid exercise validation format");
			}
		}
		
		// Validate feedback JSON
		if (exercise.getFeedback() != null && !exercise.getFeedback().trim().isEmpty()) {
			try {
				jsonSchemaValidator.validateExerciseFeedback(exercise.getFeedback());
			}
			catch (Exception e) {
				errors.rejectValue("feedback", "training.exercise.feedback.invalid", new Object[] { e.getMessage() },
				    "Invalid exercise feedback format");
			}
		}
	}
}
