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
import org.openmrs.module.training.model.CourseModule;
import org.openmrs.module.training.model.CourseModuleType;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Handler(supports = { CourseModule.class }, order = 50)
public class CourseModuleValidator implements Validator {
	
	@Override
	public boolean supports(Class<?> clazz) {
		return CourseModule.class.isAssignableFrom(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		CourseModule module = (CourseModule) target;
		
		if (module == null) {
			errors.reject("training.error.general");
			return;
		}
		
		// Validate required fields
		ValidationUtils.rejectIfEmpty(errors, "course", "training.courseModule.course.required");
		ValidationUtils.rejectIfEmpty(errors, "moduleType", "training.courseModule.type.required");
		ValidationUtils.rejectIfEmpty(errors, "sortWeight", "training.courseModule.sortWeight.required");
		
		// Validate module content based on type
		if (module.getModuleType() != null) {
			if (module.getModuleType() == CourseModuleType.LESSON) {
				if (module.getLesson() == null) {
					errors.rejectValue("lesson", "training.courseModule.lesson.required");
				}
				if (module.getExercise() != null) {
					errors.rejectValue("exercise", "training.courseModule.exercise.shouldBeNull");
				}
			} else if (module.getModuleType() == CourseModuleType.EXERCISE) {
				if (module.getExercise() == null) {
					errors.rejectValue("exercise", "training.courseModule.exercise.required");
				}
				if (module.getLesson() != null) {
					errors.rejectValue("lesson", "training.courseModule.lesson.shouldBeNull");
				}
			}
		}
		
		// Validate sort weight
		if (module.getSortWeight() != null && module.getSortWeight() < 0) {
			errors.rejectValue("sortWeight", "training.courseModule.sortWeight.invalid");
		}
	}
}
