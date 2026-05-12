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
import org.openmrs.module.training.api.util.TrainingPrivilegeConstants;
import org.openmrs.module.training.model.Course;
import org.openmrs.module.training.model.CourseEnrollment;
import org.openmrs.module.training.model.CourseModule;
import org.openmrs.module.training.model.ModuleProgress;

import java.util.List;

public interface TrainingEnrollmentService extends OpenmrsService {
	
	@Authorized(TrainingPrivilegeConstants.PARTICIPATE)
	CourseEnrollment getCourseEnrollmentByUuid(String uuid);
	
	@Authorized(TrainingPrivilegeConstants.PARTICIPATE)
	CourseEnrollment enrollCurrentUserInCourse(Course course);
	
	@Authorized(TrainingPrivilegeConstants.PARTICIPATE)
	CourseEnrollment getCurrentUserEnrollmentByCourse(Course course);
	
	@Authorized(TrainingPrivilegeConstants.PARTICIPATE)
	List<CourseEnrollment> getCurrentUserEnrollments();
	
	@Authorized(TrainingPrivilegeConstants.VIEW_ANALYTICS)
	List<CourseEnrollment> getCourseEnrollments(Course course);
	
	@Authorized(TrainingPrivilegeConstants.PARTICIPATE)
	void updateEnrollmentProgress(CourseEnrollment enrollment);
	
	// Module
	@Authorized(TrainingPrivilegeConstants.PARTICIPATE)
	void trackModuleAccess(CourseEnrollment enrollment, CourseModule module);
	
	@Authorized(TrainingPrivilegeConstants.PARTICIPATE)
	void completeModule(CourseEnrollment enrollment, CourseModule module);
	
	@Authorized(TrainingPrivilegeConstants.PARTICIPATE)
	CourseModule getNextIncompleteModule(CourseEnrollment enrollment);
	
	@Authorized(TrainingPrivilegeConstants.PARTICIPATE)
	List<ModuleProgress> getModuleProgress(CourseEnrollment enrollment);
}
