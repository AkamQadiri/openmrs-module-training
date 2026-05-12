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

import org.openmrs.User;
import org.openmrs.module.training.model.Course;
import org.openmrs.module.training.model.CourseEnrollment;
import org.openmrs.module.training.model.CourseModule;
import org.openmrs.module.training.model.ModuleProgress;

import java.util.List;

public interface TrainingEnrollmentDao {
	
	CourseEnrollment getCourseEnrollmentByUuid(String uuid);
	
	CourseEnrollment getCourseEnrollmentByUserAndCourse(User user, Course course);
	
	List<CourseEnrollment> getCourseEnrollmentsByUser(User user);
	
	List<CourseEnrollment> getCourseEnrollmentsByCourse(Course course);
	
	CourseEnrollment saveCourseEnrollment(CourseEnrollment enrollment);
	
	// Module Progress
	ModuleProgress getModuleProgress(CourseEnrollment enrollment, CourseModule module);
	
	List<ModuleProgress> getModuleProgressByEnrollment(CourseEnrollment enrollment);
	
	ModuleProgress saveModuleProgress(ModuleProgress progress);
}
