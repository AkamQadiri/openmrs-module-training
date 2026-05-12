/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.training.api.dao;

import org.openmrs.module.training.model.Course;
import org.openmrs.module.training.model.CourseEnrollment;
import org.openmrs.module.training.model.CourseFeedback;
import org.openmrs.module.training.model.CourseModule;

import java.util.List;

public interface TrainingCourseDao {
	
	Course getCourseById(Integer id);
	
	Course getCourseByUuid(String uuid);
	
	List<Course> getAllCourses(boolean includeRetired);
	
	Course saveCourse(Course course);
	
	List<Course> searchCourses(String query, boolean includeRetired, boolean includePublishedOnly);
	
	// Course Module
	CourseModule getCourseModuleByUuid(String uuid);
	
	List<CourseModule> getCourseModulesByCourse(Course course);
	
	CourseModule saveCourseModule(CourseModule module);
	
	void deleteCourseModule(CourseModule module);
	
	// Feedback
	CourseFeedback getCourseFeedbackByEnrollment(CourseEnrollment enrollment);
	
	List<CourseFeedback> getCourseFeedbacksByCourse(Course course);
	
	CourseFeedback saveCourseFeedback(CourseFeedback feedback);
}
