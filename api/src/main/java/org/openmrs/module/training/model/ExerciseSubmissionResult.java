/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.training.model;

public class ExerciseSubmissionResult {
	
	private final ExerciseAttempt attempt;
	
	private final EvaluationResult evaluationResult;
	
	public ExerciseSubmissionResult(ExerciseAttempt attempt, EvaluationResult evaluationResult) {
		this.attempt = attempt;
		this.evaluationResult = evaluationResult;
	}
	
	public ExerciseAttempt getAttempt() {
		return attempt;
	}
	
	public EvaluationResult getEvaluationResult() {
		return evaluationResult;
	}
}
