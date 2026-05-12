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
import org.openmrs.module.training.model.CourseFeedback;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Handler(supports = { CourseFeedback.class }, order = 50)
public class CourseFeedbackValidator implements Validator {
	
	@Override
	public boolean supports(Class<?> clazz) {
		return CourseFeedback.class.isAssignableFrom(clazz);
	}
	
	@Override
	public void validate(Object target, Errors errors) {
		CourseFeedback feedback = (CourseFeedback) target;
		
		if (feedback == null) {
			errors.reject("training.error.general");
			return;
		}
		
		// Validate required fields
		ValidationUtils.rejectIfEmpty(errors, "enrollment", "training.feedback.enrollment.required");
		ValidationUtils.rejectIfEmpty(errors, "clarityRating", "training.feedback.clarityRating.required");
		ValidationUtils.rejectIfEmpty(errors, "difficultyRating", "training.feedback.difficultyRating.required");
		ValidationUtils.rejectIfEmpty(errors, "usefulnessRating", "training.feedback.usefulnessRating.required");
		ValidationUtils.rejectIfEmpty(errors, "overallRating", "training.feedback.overallRating.required");
		
		// Validate rating ranges (1-5)
		validateRating(feedback.getClarityRating(), "clarityRating", errors);
		validateRating(feedback.getDifficultyRating(), "difficultyRating", errors);
		validateRating(feedback.getUsefulnessRating(), "usefulnessRating", errors);
		validateRating(feedback.getOverallRating(), "overallRating", errors);
		
		// Validate comment length
		if (feedback.getComment() != null && feedback.getComment().length() > 5000) {
			errors.rejectValue("comment", "training.feedback.comment.tooLong");
		}
	}
	
	private void validateRating(Integer rating, String field, Errors errors) {
		if (rating != null && (rating < CourseFeedback.MIN_RATING || rating > CourseFeedback.MAX_RATING)) {
			errors.rejectValue(field, "training.feedback.rating.outOfRange",
			    new Object[] { CourseFeedback.MIN_RATING, CourseFeedback.MAX_RATING }, "Rating must be between 1 and 5");
		}
	}
}
