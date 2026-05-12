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
import org.openmrs.module.training.model.Course;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Handler(supports = { Course.class }, order = 50)
public class CourseValidator implements Validator {
	
	@Override
	public boolean supports(Class<?> clazz) {
		return Course.class.isAssignableFrom(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		Course course = (Course) target;
		
		if (course == null) {
			errors.reject("training.error.general");
			return;
		}
		
		// At least one name is required
		if (course.getNames() == null || course.getNames().isEmpty()
		        || course.getNames().stream().allMatch(n -> n.getVoided())) {
			errors.rejectValue("names", "training.course.name.required");
		}
		
		// Validate estimated minutes
		if (course.getEstimatedMinutes() != null && course.getEstimatedMinutes() < 0) {
			errors.rejectValue("estimatedMinutes", "training.course.estimatedMinutes.invalid");
		}
		
		// Version must be positive
		if (course.getVersion() != null && course.getVersion() < 1) {
			errors.rejectValue("version", "training.course.version.invalid");
		}
	}
}
