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
import org.openmrs.module.training.model.ExerciseAttempt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Handler(supports = { ExerciseAttempt.class }, order = 50)
public class ExerciseAttemptValidator implements Validator {
	
	@Autowired
	private TrainingJsonSchemaValidator jsonSchemaValidator;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return ExerciseAttempt.class.isAssignableFrom(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		ExerciseAttempt attempt = (ExerciseAttempt) target;
		
		if (attempt == null) {
			errors.reject("training.error.general");
			return;
		}
		
		// Validate required fields
		ValidationUtils.rejectIfEmpty(errors, "user", "training.attempt.user.required");
		ValidationUtils.rejectIfEmpty(errors, "exercise", "training.attempt.exercise.required");
		ValidationUtils.rejectIfEmpty(errors, "enrollment", "training.attempt.enrollment.required");
		ValidationUtils.rejectIfEmpty(errors, "attemptNumber", "training.attempt.attemptNumber.required");
		ValidationUtils.rejectIfEmpty(errors, "startedAt", "training.attempt.startedAt.required");
		ValidationUtils.rejectIfEmpty(errors, "status", "training.attempt.status.required");
		
		// Validate response JSON schema if present
		if (attempt.getResponse() != null && !attempt.getResponse().trim().isEmpty() && attempt.getExercise() != null) {
			try {
				jsonSchemaValidator.validateExerciseAttemptResponse(attempt.getResponse(),
				    attempt.getExercise().getExerciseType());
			}
			catch (Exception e) {
				errors.rejectValue("response", "training.attempt.response.invalid", new Object[] { e.getMessage() },
				    "Invalid attempt response format");
			}
		}
		
		// Attempt number should be positive
		if (attempt.getAttemptNumber() != null && attempt.getAttemptNumber() < 1) {
			errors.rejectValue("attemptNumber", "training.attempt.attemptNumber.invalid");
		}
		
		// Completed date should be after start date
		if (attempt.getStartedAt() != null && attempt.getCompletedAt() != null) {
			if (attempt.getCompletedAt().before(attempt.getStartedAt())) {
				errors.rejectValue("completedAt", "training.attempt.completedAt.beforeStartedAt");
			}
		}
		
		if (attempt.getTimeSpentSeconds() != null && attempt.getTimeSpentSeconds() < 0) {
			errors.rejectValue("timeSpentSeconds", "training.attempt.timeSpent.invalid");
		}
	}
}
