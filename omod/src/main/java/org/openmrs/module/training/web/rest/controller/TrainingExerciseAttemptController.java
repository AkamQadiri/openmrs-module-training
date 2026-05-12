/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.training.web.rest.controller;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.api.ValidationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.training.api.TrainingEnrollmentService;
import org.openmrs.module.training.api.TrainingExerciseService;
import org.openmrs.module.training.api.util.TrainingJsonUtil;
import org.openmrs.module.training.model.CourseEnrollment;
import org.openmrs.module.training.model.Exercise;
import org.openmrs.module.training.model.ExerciseAttempt;
import org.openmrs.module.training.model.ExerciseSubmissionResult;
import org.openmrs.module.training.web.rest.util.ValidationErrorUtil;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.response.IllegalRequestException;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Locale;

@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + "/training/attempt")
public class TrainingExerciseAttemptController {
	
	@RequestMapping(value = "/start", method = RequestMethod.POST)
	@ResponseBody
	public SimpleObject startExerciseAttempt(@RequestBody SimpleObject postBody, HttpServletRequest request)
	        throws ResponseException {
		String enrollmentUuid = (String) postBody.get("enrollmentUuid");
		String exerciseUuid = (String) postBody.get("exerciseUuid");
		
		if (enrollmentUuid == null) {
			throw new IllegalRequestException(
			        Context.getMessageSourceService().getMessage("training.enrollment.uuid.required"));
		}
		
		if (exerciseUuid == null) {
			throw new IllegalRequestException(
			        Context.getMessageSourceService().getMessage("training.exercise.uuid.required"));
		}
		
		CourseEnrollment enrollment = getEnrollmentService().getCourseEnrollmentByUuid(enrollmentUuid);
		if (enrollment == null) {
			throw new ObjectNotFoundException(Context.getMessageSourceService().getMessage("training.enrollment.notfound",
			    new Object[] { enrollmentUuid }, Context.getLocale()));
		}
		
		Exercise exercise = getExerciseService().getExerciseByUuid(exerciseUuid);
		if (exercise == null) {
			throw new ObjectNotFoundException(Context.getMessageSourceService().getMessage("training.exercise.notfound",
			    new Object[] { exerciseUuid }, Context.getLocale()));
		}
		
		try {
			ExerciseAttempt attempt = getExerciseService().startExerciseAttempt(exercise, enrollment);
			return serializeAttempt(attempt, exercise, enrollment, false);
		}
		catch (ValidationException e) {
			throw new IllegalRequestException(ValidationErrorUtil.extractValidationMessage(e));
		}
	}
	
	@RequestMapping(value = "/{uuid}/submit", method = RequestMethod.POST)
	@ResponseBody
	public SimpleObject submitExerciseAttempt(@PathVariable("uuid") String exerciseUuid, @RequestBody SimpleObject postBody,
	        HttpServletRequest request) throws ResponseException {
		Exercise exercise = getExerciseService().getExerciseByUuid(exerciseUuid);
		if (exercise == null) {
			throw new ObjectNotFoundException(Context.getMessageSourceService().getMessage("training.exercise.notfound",
			    new Object[] { exerciseUuid }, Context.getLocale()));
		}
		
		String enrollmentUuid = (String) postBody.get("enrollmentUuid");
		Object responseObj = postBody.get("response");
		String response;
		
		if (responseObj instanceof String) {
			response = (String) responseObj;
		} else {
			response = TrainingJsonUtil.toJsonString(responseObj);
		}
		
		if (enrollmentUuid == null || response == null) {
			throw new IllegalRequestException(
			        Context.getMessageSourceService().getMessage("training.attempt.enrollmentresponse.required"));
		}
		
		CourseEnrollment enrollment = getEnrollmentService().getCourseEnrollmentByUuid(enrollmentUuid);
		if (enrollment == null) {
			throw new ObjectNotFoundException(Context.getMessageSourceService().getMessage("training.enrollment.notfound",
			    new Object[] { enrollmentUuid }, Context.getLocale()));
		}
		
		try {
			ExerciseSubmissionResult exerciseSubmissionResult = getExerciseService().submitExerciseAttempt(exercise,
			    enrollment, response);
			return serializeAttempt(exerciseSubmissionResult.getAttempt(), exercise, enrollment, true,
			    exerciseSubmissionResult);
		}
		catch (ValidationException e) {
			throw new IllegalRequestException(ValidationErrorUtil.extractValidationMessage(e));
		}
	}
	
	private String getLocalizedFeedback(JsonNode feedbackNode) {
		if (feedbackNode.isMissingNode() || feedbackNode.isNull())
			return null;
		
		Locale locale = Context.getLocale();
		String localeStr = locale.toString().replace('_', '-');
		
		// Try exact locale match
		if (feedbackNode.has(localeStr)) {
			return feedbackNode.get(localeStr).asText();
		}
		
		// Try language only
		String language = locale.getLanguage();
		if (feedbackNode.has(language)) {
			return feedbackNode.get(language).asText();
		}
		
		// Fallback to English
		if (feedbackNode.has("en")) {
			return feedbackNode.get("en").asText();
		}
		
		return null;
	}
	
	private SimpleObject serializeAttempt(ExerciseAttempt attempt, Exercise exercise, CourseEnrollment enrollment,
	        boolean includeResults) {
		return serializeAttempt(attempt, exercise, enrollment, includeResults, null);
	}
	
	private SimpleObject serializeAttempt(ExerciseAttempt attempt, Exercise exercise, CourseEnrollment enrollment,
	        boolean includeResults, ExerciseSubmissionResult submissionResult) {
		SimpleObject exerciseObj = (SimpleObject) ConversionUtil.convertToRepresentation(exercise, Representation.REF);
		SimpleObject enrollmentObj = new SimpleObject();
		enrollmentObj.put("uuid", enrollment.getUuid());
		
		SimpleObject result = new SimpleObject();
		result.put("uuid", attempt.getUuid());
		result.put("exercise", exerciseObj);
		result.put("enrollment", enrollmentObj);
		result.put("attemptNumber", attempt.getAttemptNumber());
		result.put("startedAt", attempt.getStartedAt());
		result.put("status", attempt.getStatus().toString());
		
		if (includeResults) {
			result.put("correct", attempt.getCorrect());
			result.put("completedAt", attempt.getCompletedAt());
			result.put("timeSpentSeconds", attempt.getTimeSpentSeconds());
			
			// Add feedback
			SimpleObject feedbackObj = generateFeedback(exercise, attempt, submissionResult);
			if (feedbackObj != null) {
				result.put("feedback", feedbackObj);
			}
			
			result.put("allowRetry", exercise.getAllowRetry() && !attempt.getCorrect());
		}
		
		return result;
	}
	
	private SimpleObject generateFeedback(Exercise exercise, ExerciseAttempt attempt,
	        ExerciseSubmissionResult submissionResult) {
		SimpleObject feedbackObj = null;
		
		if (exercise.getFeedback() != null) {
			JsonNode feedbackNode = TrainingJsonUtil.parseJsonNode(exercise.getFeedback());
			
			if (feedbackNode != null) {
				String feedbackType = attempt.getCorrect() ? "correct" : "incorrect";
				JsonNode feedbackMessage = feedbackNode.path(feedbackType);
				String localizedMessage = getLocalizedFeedback(feedbackMessage);
				
				if (localizedMessage != null) {
					feedbackObj = new SimpleObject();
					feedbackObj.put("message", localizedMessage);
				}
			}
		}
		
		if (submissionResult != null && submissionResult.getEvaluationResult().hasErrors()) {
			if (feedbackObj == null) {
				feedbackObj = new SimpleObject();
			}
			feedbackObj.put("error", String.join("\n", submissionResult.getEvaluationResult().getErrors()));
		}
		
		return feedbackObj;
	}
	
	private TrainingExerciseService getExerciseService() {
		return Context.getService(TrainingExerciseService.class);
	}
	
	private TrainingEnrollmentService getEnrollmentService() {
		return Context.getService(TrainingEnrollmentService.class);
	}
}
