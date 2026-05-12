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
import org.openmrs.module.training.model.CourseEnrollment;
import org.openmrs.module.training.model.Exercise;
import org.openmrs.module.training.model.ExerciseAttempt;
import org.openmrs.module.training.model.ExerciseSubmissionResult;

import java.util.List;

public interface TrainingExerciseService extends OpenmrsService {
	
	@Authorized(TrainingPrivilegeConstants.MANAGE)
	Exercise getExerciseById(Integer id);
	
	@Authorized(TrainingPrivilegeConstants.MANAGE)
	Exercise getExerciseByUuid(String uuid);
	
	@Authorized(TrainingPrivilegeConstants.MANAGE)
	List<Exercise> getAllExercises(boolean includeRetired);
	
	@Authorized(TrainingPrivilegeConstants.MANAGE)
	Exercise saveExercise(Exercise exercise) throws ValidationException;
	
	@Authorized(TrainingPrivilegeConstants.MANAGE)
	void retireExercise(Exercise exercise, String reason);
	
	// Exercise Attempt
	@Authorized(TrainingPrivilegeConstants.PARTICIPATE)
	ExerciseAttempt startExerciseAttempt(Exercise exercise, CourseEnrollment enrollment);
	
	@Authorized(TrainingPrivilegeConstants.PARTICIPATE)
	ExerciseSubmissionResult submitExerciseAttempt(Exercise exercise, CourseEnrollment enrollment, String response)
	        throws ValidationException;
}
