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

import org.openmrs.User;
import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.training.api.util.TrainingPrivilegeConstants;
import org.openmrs.module.training.model.Course;
import org.openmrs.module.training.model.Exercise;

import java.util.Map;

public interface TrainingAnalyticsService extends OpenmrsService {
	
	@Authorized(TrainingPrivilegeConstants.VIEW_ANALYTICS)
	Map<String, Object> getCourseAnalytics(Course course);
	
	@Authorized(TrainingPrivilegeConstants.VIEW_ANALYTICS)
	Map<String, Object> getExerciseAnalytics(Exercise exercise);
	
	@Authorized(TrainingPrivilegeConstants.VIEW_ANALYTICS)
	Map<String, Object> getUserAnalytics(User user);
	
	@Authorized(TrainingPrivilegeConstants.PARTICIPATE)
	Map<String, Object> getCurrentUserAnalytics();
}
