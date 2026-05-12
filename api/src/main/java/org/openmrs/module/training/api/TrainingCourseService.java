/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.training.api;

import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.api.ValidationException;
import org.openmrs.module.training.api.util.TrainingPrivilegeConstants;
import org.openmrs.module.training.model.Course;
import org.openmrs.module.training.model.CourseEnrollment;
import org.openmrs.module.training.model.CourseFeedback;
import org.openmrs.module.training.model.CourseModule;

import java.util.List;

public interface TrainingCourseService extends OpenmrsService {
	
	@Authorized(TrainingPrivilegeConstants.MANAGE)
	Course getCourseByUuid(String uuid);
	
	@Authorized(TrainingPrivilegeConstants.MANAGE)
	List<Course> getAllCourses(boolean includeRetired);
	
	@Authorized(TrainingPrivilegeConstants.PARTICIPATE)
	List<Course> getPublishedCourses();
	
	@Authorized(TrainingPrivilegeConstants.MANAGE)
	Course saveCourse(Course course) throws ValidationException;
	
	@Authorized(TrainingPrivilegeConstants.MANAGE)
	void retireCourse(Course course, String reason);
	
	@Authorized(TrainingPrivilegeConstants.MANAGE)
	void publishCourse(Course course);
	
	@Authorized(TrainingPrivilegeConstants.MANAGE)
	void unpublishCourse(Course course);
	
	@Authorized(TrainingPrivilegeConstants.PARTICIPATE)
	List<Course> searchCourses(String query, boolean includeRetired, boolean publishedOnly);
	
	// Course Module
	@Authorized(TrainingPrivilegeConstants.MANAGE)
	CourseModule getCourseModuleByUuid(String uuid);
	
	@Authorized(TrainingPrivilegeConstants.MANAGE)
	List<CourseModule> getCourseModulesByCourse(Course course);
	
	@Authorized(TrainingPrivilegeConstants.MANAGE)
	CourseModule saveCourseModule(CourseModule module) throws ValidationException;
	
	@Authorized(TrainingPrivilegeConstants.MANAGE)
	void removeCourseModule(CourseModule module);
	
	// Feedback
	@Authorized(TrainingPrivilegeConstants.PARTICIPATE)
	CourseFeedback submitCourseFeedback(CourseEnrollment enrollment, CourseFeedback feedback) throws ValidationException;
}
