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
import org.openmrs.module.training.model.Lesson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Handler(supports = { Lesson.class }, order = 50)
public class LessonValidator implements Validator {
	
	@Autowired
	private TrainingJsonSchemaValidator jsonSchemaValidator;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return Lesson.class.isAssignableFrom(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		Lesson lesson = (Lesson) target;
		
		if (lesson == null) {
			errors.reject("training.error.general");
			return;
		}
		
		// Validate required fields
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "content", "training.lesson.content.required");
		
		// Validate JSON schema
		if (lesson.getContent() != null && !lesson.getContent().trim().isEmpty()) {
			try {
				jsonSchemaValidator.validateLessonContent(lesson.getContent());
			}
			catch (Exception e) {
				errors.rejectValue("content", "training.lesson.content.invalid", new Object[] { e.getMessage() },
				    "Invalid lesson content format");
			}
		}
		
		// At least one name is required
		if (lesson.getNames() == null || lesson.getNames().isEmpty()
		        || lesson.getNames().stream().allMatch(n -> n.getVoided())) {
			errors.rejectValue("names", "training.lesson.name.required");
		}
		
		// Validate estimated minutes
		if (lesson.getEstimatedMinutes() != null && lesson.getEstimatedMinutes() < 0) {
			errors.rejectValue("estimatedMinutes", "training.lesson.estimatedMinutes.invalid");
		}
	}
}
