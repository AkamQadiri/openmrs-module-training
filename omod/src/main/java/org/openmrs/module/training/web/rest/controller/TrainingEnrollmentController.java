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

import org.openmrs.api.context.Context;
import org.openmrs.module.training.api.TrainingCourseService;
import org.openmrs.module.training.api.TrainingEnrollmentService;
import org.openmrs.module.training.model.Course;
import org.openmrs.module.training.model.CourseEnrollment;
import org.openmrs.module.training.model.CourseModule;
import org.openmrs.module.training.model.CourseModuleType;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + "/training/enrollment")
public class TrainingEnrollmentController {
	
	@RequestMapping(value = "/course/{uuid}", method = RequestMethod.POST)
	@ResponseBody
	public Object enrollInCourse(@PathVariable("uuid") String courseUuid, HttpServletRequest request) {
		Course course = getCourseService().getCourseByUuid(courseUuid);
		
		if (course == null) {
			throw new ObjectNotFoundException(Context.getMessageSourceService().getMessage("training.course.notfound",
			    new Object[] { courseUuid }, Context.getLocale()));
		}
		
		CourseEnrollment enrollment = getEnrollmentService().enrollCurrentUserInCourse(course);
		SimpleObject courseObj = (SimpleObject) ConversionUtil.convertToRepresentation(enrollment.getCourse(),
		    Representation.REF);
		SimpleObject result = new SimpleObject();
		result.put("uuid", enrollment.getUuid());
		result.put("course", courseObj);
		result.put("enrolledAt", enrollment.getEnrolledAt());
		
		return result;
	}
	
	@RequestMapping(value = "/course/{uuid}", method = RequestMethod.GET)
	@ResponseBody
	public SimpleObject getEnrollmentByCourse(@PathVariable("uuid") String courseUuid, HttpServletRequest request) {
		Course course = getCourseService().getCourseByUuid(courseUuid);
		
		if (course == null) {
			throw new ObjectNotFoundException(Context.getMessageSourceService().getMessage("training.course.notfound",
			    new Object[] { courseUuid }, Context.getLocale()));
		}
		
		CourseEnrollment enrollment = getEnrollmentService().getCurrentUserEnrollmentByCourse(course);
		
		if (enrollment == null) {
			throw new ObjectNotFoundException(
			        Context.getMessageSourceService().getMessage("training.enrollment.user.notfound"));
		}
		
		return serializeEnrollment(enrollment);
	}
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ResponseBody
	public SimpleObject getCurrentUserEnrollments(HttpServletRequest request) {
		List<CourseEnrollment> enrollments = getEnrollmentService().getCurrentUserEnrollments();
		List<SimpleObject> enrollmentObjects = new ArrayList<>();
		
		for (CourseEnrollment enrollment : enrollments) {
			enrollmentObjects.add(serializeEnrollment(enrollment));
		}
		
		SimpleObject result = new SimpleObject();
		result.put("enrollments", enrollmentObjects);
		return result;
	}
	
	@RequestMapping(value = "/module/access", method = RequestMethod.POST)
	@ResponseBody
	public void trackModuleAccess(@RequestBody SimpleObject postBody, HttpServletRequest request) {
		String enrollmentUuid = (String) postBody.get("enrollmentUuid");
		String moduleUuid = (String) postBody.get("moduleUuid");
		
		CourseEnrollment enrollment = getEnrollmentService().getCourseEnrollmentByUuid(enrollmentUuid);
		CourseModule module = getCourseService().getCourseModuleByUuid(moduleUuid);
		
		if (enrollment == null) {
			throw new ObjectNotFoundException(Context.getMessageSourceService().getMessage("training.enrollment.notfound",
			    new Object[] { enrollmentUuid }, Context.getLocale()));
		}
		
		if (module == null) {
			throw new ObjectNotFoundException(Context.getMessageSourceService().getMessage("training.courseModule.notfound",
			    new Object[] { moduleUuid }, Context.getLocale()));
		}
		
		getEnrollmentService().trackModuleAccess(enrollment, module);
	}
	
	@RequestMapping(value = "/module/complete", method = RequestMethod.POST)
	@ResponseBody
	public void completeModule(@RequestBody SimpleObject postBody, HttpServletRequest request) {
		String enrollmentUuid = (String) postBody.get("enrollmentUuid");
		String moduleUuid = (String) postBody.get("moduleUuid");
		
		CourseEnrollment enrollment = getEnrollmentService().getCourseEnrollmentByUuid(enrollmentUuid);
		CourseModule module = getCourseService().getCourseModuleByUuid(moduleUuid);
		
		if (enrollment == null) {
			throw new ObjectNotFoundException(Context.getMessageSourceService().getMessage("training.enrollment.notfound",
			    new Object[] { enrollmentUuid }, Context.getLocale()));
		}
		
		if (module == null) {
			throw new ObjectNotFoundException(Context.getMessageSourceService().getMessage("training.courseModule.notfound",
			    new Object[] { moduleUuid }, Context.getLocale()));
		}
		
		getEnrollmentService().completeModule(enrollment, module);
	}
	
	private SimpleObject serializeEnrollment(CourseEnrollment enrollment) {
		SimpleObject obj = new SimpleObject();
		obj.put("uuid", enrollment.getUuid());
		obj.put("enrolledAt", enrollment.getEnrolledAt());
		obj.put("completedAt", enrollment.getCompletedAt());
		obj.put("lastAccessedAt", enrollment.getLastAccessedAt());
		obj.put("progressPercentage", enrollment.getProgressPercentage());
		
		SimpleObject courseObj = (SimpleObject) ConversionUtil.convertToRepresentation(enrollment.getCourse(),
		    Representation.REF);
		obj.put("course", courseObj);
		
		// Add last accessed module info
		if (enrollment.getLastAccessedModule() != null) {
			obj.put("lastAccessedModule", serializeModule(enrollment.getLastAccessedModule()));
		}
		
		// Get next module to continue
		CourseModule nextModule = getEnrollmentService().getNextIncompleteModule(enrollment);
		if (nextModule != null) {
			obj.put("nextModule", serializeModule(nextModule));
		}
		
		return obj;
	}
	
	private SimpleObject serializeModule(CourseModule module) {
		SimpleObject moduleObj = new SimpleObject();
		moduleObj.put("uuid", module.getUuid());
		moduleObj.put("moduleType", module.getModuleType());
		moduleObj.put("sortWeight", module.getSortWeight());
		
		if (module.getModuleType() == CourseModuleType.LESSON) {
			if (module.getLesson() != null) {
				moduleObj.put("lessonUuid", module.getLesson().getUuid());
			}
		} else if (module.getModuleType() == CourseModuleType.EXERCISE) {
			if (module.getExercise() != null) {
				moduleObj.put("exerciseUuid", module.getExercise().getUuid());
			}
		}
		
		return moduleObj;
	}
	
	private TrainingEnrollmentService getEnrollmentService() {
		return Context.getService(TrainingEnrollmentService.class);
	}
	
	private TrainingCourseService getCourseService() {
		return Context.getService(TrainingCourseService.class);
	}
}
