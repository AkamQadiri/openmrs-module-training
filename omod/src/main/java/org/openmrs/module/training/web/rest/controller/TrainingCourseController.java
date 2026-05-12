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
import org.openmrs.module.training.api.TrainingCourseService;
import org.openmrs.module.training.api.TrainingEnrollmentService;
import org.openmrs.module.training.model.Course;
import org.openmrs.module.training.model.CourseEnrollment;
import org.openmrs.module.training.model.CourseFeedback;
import org.openmrs.module.training.model.CourseModule;
import org.openmrs.module.training.model.CourseModuleType;
import org.openmrs.module.training.model.Exercise;
import org.openmrs.module.training.model.Lesson;
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

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + "/training/course")
public class TrainingCourseController {
	
	@RequestMapping(value = "/{uuid}/publish", method = RequestMethod.POST)
	@ResponseBody
	public SimpleObject publishCourse(@PathVariable("uuid") String courseUuid, HttpServletRequest request)
	        throws ResponseException {
		Course course = getCourseService().getCourseByUuid(courseUuid);
		
		if (course == null) {
			throw new ObjectNotFoundException(Context.getMessageSourceService().getMessage("training.course.notfound",
			    new Object[] { courseUuid }, Context.getLocale()));
		}
		
		getCourseService().publishCourse(course);
		return (SimpleObject) ConversionUtil.convertToRepresentation(course, Representation.DEFAULT);
	}
	
	@RequestMapping(value = "/{uuid}/unpublish", method = RequestMethod.POST)
	@ResponseBody
	public SimpleObject unpublishCourse(@PathVariable("uuid") String courseUuid, HttpServletRequest request)
	        throws ResponseException {
		Course course = getCourseService().getCourseByUuid(courseUuid);
		
		if (course == null) {
			throw new ObjectNotFoundException(Context.getMessageSourceService().getMessage("training.course.notfound",
			    new Object[] { courseUuid }, Context.getLocale()));
		}
		
		getCourseService().unpublishCourse(course);
		return (SimpleObject) ConversionUtil.convertToRepresentation(course, Representation.DEFAULT);
	}
	
	@RequestMapping(value = "/{uuid}/structure", method = RequestMethod.GET)
	@ResponseBody
	public SimpleObject getCourseStructure(@PathVariable("uuid") String courseUuid, HttpServletRequest request)
	        throws ResponseException {
		Course course = getCourseService().getCourseByUuid(courseUuid);
		
		if (course == null) {
			throw new ObjectNotFoundException(Context.getMessageSourceService().getMessage("training.course.notfound",
			    new Object[] { courseUuid }, Context.getLocale()));
		}
		
		return serializeCourseStructure(course);
	}
	
	@RequestMapping(value = "/{uuid}/feedback", method = RequestMethod.POST)
	@ResponseBody
	public void submitFeedback(@PathVariable("uuid") String courseUuid, @RequestBody SimpleObject postBody,
	        HttpServletRequest request) throws ResponseException {
		Course course = getCourseService().getCourseByUuid(courseUuid);
		
		if (course == null) {
			throw new ObjectNotFoundException(Context.getMessageSourceService().getMessage("training.course.notfound",
			    new Object[] { courseUuid }, Context.getLocale()));
		}
		
		Integer clarityRating = (Integer) postBody.get("clarityRating");
		Integer difficultyRating = (Integer) postBody.get("difficultyRating");
		Integer usefulnessRating = (Integer) postBody.get("usefulnessRating");
		Integer overallRating = (Integer) postBody.get("overallRating");
		String comment = (String) postBody.get("comment");
		
		if (clarityRating == null || difficultyRating == null || usefulnessRating == null || overallRating == null) {
			throw new IllegalRequestException(
			        Context.getMessageSourceService().getMessage("training.feedback.allratings.required"));
		}
		
		CourseEnrollment enrollment = getEnrollmentService().getCurrentUserEnrollmentByCourse(course);
		if (enrollment == null) {
			throw new ObjectNotFoundException(
			        Context.getMessageSourceService().getMessage("training.enrollment.user.notfound"));
		}
		
		CourseFeedback feedback = new CourseFeedback();
		feedback.setEnrollment(enrollment);
		feedback.setClarityRating(clarityRating);
		feedback.setDifficultyRating(difficultyRating);
		feedback.setUsefulnessRating(usefulnessRating);
		feedback.setOverallRating(overallRating);
		feedback.setComment(comment);
		
		try {
			getCourseService().submitCourseFeedback(enrollment, feedback);
		}
		catch (ValidationException e) {
			throw new IllegalRequestException(ValidationErrorUtil.extractValidationMessage(e));
		}
	}
	
	private SimpleObject serializeCourseStructure(Course course) {
		SimpleObject result = new SimpleObject();
		result.put("uuid", course.getUuid());
		result.put("name", course.getName());
		result.put("description", course.getDescription());
		result.put("estimatedMinutes", course.getEstimatedMinutes());
		result.put("version", course.getVersion());
		result.put("published", course.getPublished());
		
		List<CourseModule> modules = getCourseService().getCourseModulesByCourse(course);
		List<SimpleObject> moduleObjects = new ArrayList<>();
		
		for (CourseModule module : modules) {
			moduleObjects.add(serializeModuleWithDetails(module));
		}
		
		result.put("modules", moduleObjects);
		return result;
	}
	
	private SimpleObject serializeModuleWithDetails(CourseModule module) {
		SimpleObject moduleObj = new SimpleObject();
		moduleObj.put("uuid", module.getUuid());
		moduleObj.put("moduleType", module.getModuleType());
		moduleObj.put("sortWeight", module.getSortWeight());
		moduleObj.put("required", module.getRequired());
		
		if (module.getModuleType() == CourseModuleType.LESSON && module.getLesson() != null) {
			moduleObj.put("lesson", serializeLesson(module.getLesson()));
		} else if (module.getModuleType() == CourseModuleType.EXERCISE && module.getExercise() != null) {
			moduleObj.put("exercise", serializeExercise(module.getExercise()));
		}
		
		return moduleObj;
	}
	
	private SimpleObject serializeLesson(Lesson lesson) {
		SimpleObject lessonObj = new SimpleObject();
		lessonObj.put("uuid", lesson.getUuid());
		lessonObj.put("name", lesson.getName());
		lessonObj.put("description", lesson.getDescription());
		lessonObj.put("estimatedMinutes", lesson.getEstimatedMinutes());
		return lessonObj;
	}
	
	private SimpleObject serializeExercise(Exercise exercise) {
		SimpleObject exerciseObj = new SimpleObject();
		exerciseObj.put("uuid", exercise.getUuid());
		exerciseObj.put("name", exercise.getName());
		exerciseObj.put("exerciseType", exercise.getExerciseType());
		exerciseObj.put("allowRetry", exercise.getAllowRetry());
		return exerciseObj;
	}
	
	private TrainingCourseService getCourseService() {
		return Context.getService(TrainingCourseService.class);
	}
	
	private TrainingEnrollmentService getEnrollmentService() {
		return Context.getService(TrainingEnrollmentService.class);
	}
}
