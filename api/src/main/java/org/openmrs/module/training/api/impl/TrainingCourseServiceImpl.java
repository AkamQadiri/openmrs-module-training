/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.training.api.impl;

import org.openmrs.api.APIException;
import org.openmrs.api.ValidationException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.training.api.TrainingCourseService;
import org.openmrs.module.training.api.dao.TrainingCourseDao;
import org.openmrs.module.training.api.validator.CourseFeedbackValidator;
import org.openmrs.module.training.api.validator.CourseModuleValidator;
import org.openmrs.module.training.api.validator.CourseValidator;
import org.openmrs.module.training.model.Course;
import org.openmrs.module.training.model.CourseEnrollment;
import org.openmrs.module.training.model.CourseFeedback;
import org.openmrs.module.training.model.CourseModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service("trainingCourseService")
public class TrainingCourseServiceImpl extends BaseOpenmrsService implements TrainingCourseService {
	
	@Autowired
	private TrainingCourseDao courseDAO;
	
	@Autowired
	private CourseValidator courseValidator;
	
	@Autowired
	private CourseModuleValidator courseModuleValidator;
	
	@Autowired
	private CourseFeedbackValidator courseFeedbackValidator;
	
	@Override
	@Transactional(readOnly = true)
	public Course getCourseByUuid(String uuid) {
		return courseDAO.getCourseByUuid(uuid);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Course> getAllCourses(boolean includeRetired) {
		return courseDAO.getAllCourses(includeRetired);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Course> getPublishedCourses() {
		return courseDAO.getAllCourses(false).stream().filter(Course::getPublished).collect(Collectors.toList());
	}
	
	@Override
	public Course saveCourse(Course course) {
		Errors errors = new BindException(course, "course");
		courseValidator.validate(course, errors);
		
		if (errors.hasErrors()) {
			throw new ValidationException(errors);
		}
		
		// Increment version if updating
		if (course.getId() != null) {
			Course existing = courseDAO.getCourseById(course.getId());
			if (existing != null) {
				course.setVersion(existing.getVersion() + 1);
			}
		}
		
		return courseDAO.saveCourse(course);
	}
	
	@Override
	public void retireCourse(Course course, String reason) {
		course.setRetired(true);
		course.setRetireReason(reason);
		course.setDateRetired(new Date());
		courseDAO.saveCourse(course);
	}
	
	@Override
	public void publishCourse(Course course) {
		course.setPublished(true);
		courseDAO.saveCourse(course);
	}
	
	@Override
	public void unpublishCourse(Course course) {
		course.setPublished(false);
		courseDAO.saveCourse(course);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Course> searchCourses(String query, boolean includeRetired, boolean publishedOnly) {
		return courseDAO.searchCourses(query, includeRetired, publishedOnly);
	}
	
	// Course Module
	@Override
	@Transactional(readOnly = true)
	public CourseModule getCourseModuleByUuid(String uuid) {
		return courseDAO.getCourseModuleByUuid(uuid);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CourseModule> getCourseModulesByCourse(Course course) {
		return courseDAO.getCourseModulesByCourse(course);
	}
	
	@Override
	public CourseModule saveCourseModule(CourseModule module) {
		Errors errors = new BindException(module, "module");
		courseModuleValidator.validate(module, errors);
		
		if (errors.hasErrors()) {
			throw new ValidationException(errors);
		}
		
		return courseDAO.saveCourseModule(module);
	}
	
	@Override
	public void removeCourseModule(CourseModule module) {
		module.setVoided(true);
		module.setVoidReason("Removed from course");
		module.setDateVoided(new Date());
		courseDAO.saveCourseModule(module);
	}
	
	// Feedback
	@Override
	public CourseFeedback submitCourseFeedback(CourseEnrollment enrollment, CourseFeedback feedback) {
		// Ensure enrollment is completed
		if (enrollment.getCompletedAt() == null) {
			throw new APIException(
			        Context.getMessageSourceService().getMessage("training.course.must.complete.before.feedback"));
		}
		
		// Check if feedback already exists
		CourseFeedback existing = courseDAO.getCourseFeedbackByEnrollment(enrollment);
		if (existing != null && !existing.getVoided()) {
			throw new APIException(
			        Context.getMessageSourceService().getMessage("training.course.feedback.already.submitted"));
		}
		
		feedback.setEnrollment(enrollment);
		
		Errors errors = new BindException(feedback, "feedback");
		courseFeedbackValidator.validate(feedback, errors);
		
		if (errors.hasErrors()) {
			throw new ValidationException(errors);
		}
		
		return courseDAO.saveCourseFeedback(feedback);
	}
}
