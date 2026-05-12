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

import org.openmrs.module.training.model.CourseEnrollment;
import org.openmrs.module.training.model.Exercise;
import org.openmrs.module.training.model.ExerciseAttempt;

import java.util.List;

public interface TrainingExerciseDao {
	
	Exercise getExerciseById(Integer id);
	
	Exercise getExerciseByUuid(String uuid);
	
	List<Exercise> getAllExercises(boolean includeRetired);
	
	Exercise saveExercise(Exercise exercise);
	
	// Exercise Attempt
	ExerciseAttempt saveExerciseAttempt(ExerciseAttempt attempt);
	
	List<ExerciseAttempt> getExerciseAttemptsByEnrollmentAndExercise(CourseEnrollment enrollment, Exercise exercise);
	
	List<ExerciseAttempt> getExerciseAttemptsByExercise(Exercise exercise);
}
