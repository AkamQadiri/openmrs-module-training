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

import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.ValidationException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.training.api.TrainingEnrollmentService;
import org.openmrs.module.training.api.TrainingExerciseService;
import org.openmrs.module.training.api.dao.TrainingCourseDao;
import org.openmrs.module.training.api.dao.TrainingExerciseDao;
import org.openmrs.module.training.api.util.ExerciseResponseEvaluator;
import org.openmrs.module.training.api.validator.ExerciseAttemptValidator;
import org.openmrs.module.training.api.validator.ExerciseValidator;
import org.openmrs.module.training.model.CourseEnrollment;
import org.openmrs.module.training.model.CourseModule;
import org.openmrs.module.training.model.CourseModuleType;
import org.openmrs.module.training.model.EvaluationResult;
import org.openmrs.module.training.model.Exercise;
import org.openmrs.module.training.model.ExerciseAttempt;
import org.openmrs.module.training.model.ExerciseAttemptStatus;
import org.openmrs.module.training.model.ExerciseSubmissionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import java.util.Date;
import java.util.List;

@Service("trainingExerciseService")
public class TrainingExerciseServiceImpl extends BaseOpenmrsService implements TrainingExerciseService {
	
	@Autowired
	private TrainingCourseDao courseDAO;
	
	@Autowired
	private TrainingExerciseDao exerciseDAO;
	
	@Autowired
	private ExerciseValidator exerciseValidator;
	
	@Autowired
	private ExerciseAttemptValidator exerciseAttemptValidator;
	
	@Autowired
	private TrainingEnrollmentService enrollmentService;
	
	@Override
	@Transactional(readOnly = true)
	public Exercise getExerciseById(Integer id) {
		return exerciseDAO.getExerciseById(id);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Exercise getExerciseByUuid(String uuid) {
		return exerciseDAO.getExerciseByUuid(uuid);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Exercise> getAllExercises(boolean includeRetired) {
		return exerciseDAO.getAllExercises(includeRetired);
	}
	
	@Override
	public Exercise saveExercise(Exercise exercise) {
		Errors errors = new BindException(exercise, "exercise");
		exerciseValidator.validate(exercise, errors);
		
		if (errors.hasErrors()) {
			throw new ValidationException(errors);
		}
		
		return exerciseDAO.saveExercise(exercise);
	}
	
	@Override
	public void retireExercise(Exercise exercise, String reason) {
		exercise.setRetired(true);
		exercise.setRetireReason(reason);
		exercise.setDateRetired(new Date());
		exerciseDAO.saveExercise(exercise);
	}
	
	// Exercise Attempt
	@Override
	public ExerciseAttempt startExerciseAttempt(Exercise exercise, CourseEnrollment enrollment) {
		User user = Context.getAuthenticatedUser();
		
		List<ExerciseAttempt> attempts = exerciseDAO.getExerciseAttemptsByEnrollmentAndExercise(enrollment, exercise);
		ExerciseAttempt currentAttempt = attempts.stream().filter(a -> a.getStatus() == ExerciseAttemptStatus.IN_PROGRESS)
		        .findFirst().orElse(null);
		
		if (currentAttempt != null) {
			long hourInMillis = 60 * 60 * 1000;
			long timeSinceStart = new Date().getTime() - currentAttempt.getStartedAt().getTime();
			
			if (timeSinceStart >= hourInMillis) {
				currentAttempt.setStartedAt(new Date());
				return exerciseDAO.saveExerciseAttempt(currentAttempt);
			}
			
			return currentAttempt;
		}
		
		// Get attempt number
		List<ExerciseAttempt> previousAttempts = exerciseDAO.getExerciseAttemptsByEnrollmentAndExercise(enrollment,
		    exercise);
		int attemptNumber = previousAttempts.size() + 1;
		
		// Create attempt
		ExerciseAttempt attempt = new ExerciseAttempt();
		attempt.setUser(user);
		attempt.setExercise(exercise);
		attempt.setEnrollment(enrollment);
		attempt.setAttemptNumber(attemptNumber);
		attempt.setStartedAt(new Date());
		attempt.setStatus(ExerciseAttemptStatus.IN_PROGRESS);
		
		Errors errors = new BindException(attempt, "attempt");
		exerciseAttemptValidator.validate(attempt, errors);
		
		if (errors.hasErrors()) {
			throw new ValidationException(errors);
		}
		
		return exerciseDAO.saveExerciseAttempt(attempt);
	}
	
	@Override
	public ExerciseSubmissionResult submitExerciseAttempt(Exercise exercise, CourseEnrollment enrollment, String response) {
		List<ExerciseAttempt> attempts = exerciseDAO.getExerciseAttemptsByEnrollmentAndExercise(enrollment, exercise);
		ExerciseAttempt currentAttempt = attempts.stream().filter(a -> a.getStatus() == ExerciseAttemptStatus.IN_PROGRESS)
		        .findFirst().orElse(null);
		
		if (currentAttempt == null) {
			throw new APIException(Context.getMessageSourceService().getMessage("training.exercise.attempt.notfound"));
		}
		
		if (!exercise.getAllowRetry() && attempts.size() > 1) {
			throw new APIException(Context.getMessageSourceService().getMessage("training.exercise.no.retry.allowed"));
		}
		
		currentAttempt.setResponse(response);
		
		Errors errors = new BindException(currentAttempt, "attempt");
		exerciseAttemptValidator.validate(currentAttempt, errors);
		
		if (errors.hasErrors()) {
			throw new ValidationException(errors);
		}
		
		EvaluationResult evaluationResult = ExerciseResponseEvaluator.evaluateResponse(exercise, response);
		
		currentAttempt.setCorrect(evaluationResult.isCorrect());
		currentAttempt.setCompletedAt(new Date());
		currentAttempt.setStatus(ExerciseAttemptStatus.COMPLETED);
		currentAttempt.setTimeSpentSeconds(
		    (currentAttempt.getCompletedAt().getTime() - currentAttempt.getStartedAt().getTime()) / 1000);
		
		currentAttempt = exerciseDAO.saveExerciseAttempt(currentAttempt);
		
		// Find the module containing this exercise
		List<CourseModule> modules = courseDAO.getCourseModulesByCourse(enrollment.getCourse());
		CourseModule exerciseModule = modules.stream().filter(m -> m.getModuleType() == CourseModuleType.EXERCISE
		        && m.getExercise() != null && m.getExercise().getId().equals(exercise.getId())).findFirst().orElse(null);
		
		if (exerciseModule != null) {
			// Complete module based on rules
			if (evaluationResult.isCorrect() || !exercise.getAllowRetry()) {
				// Complete if correct OR if no retry allowed (first attempt rule)
				enrollmentService.completeModule(enrollment, exerciseModule);
			} else {
				// Always update overall progress even if not completed
				enrollmentService.updateEnrollmentProgress(enrollment);
			}
		}
		
		return new ExerciseSubmissionResult(currentAttempt, evaluationResult);
	}
}
