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

import javax.persistence.*;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.User;

import java.util.Date;

@Entity
@Table(name = "training_exercise_attempt")
public class ExerciseAttempt extends BaseOpenmrsData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "exercise_attempt_id")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "exercise_id", nullable = false)
	private Exercise exercise;
	
	@ManyToOne
	@JoinColumn(name = "enrollment_id", nullable = false)
	private CourseEnrollment enrollment;
	
	@Column(name = "attempt_number", nullable = false)
	private Integer attemptNumber;
	
	// schema = "schemas/exercise-attempt-response-*.schema.json"
	@Column(name = "response", columnDefinition = "TEXT")
	private String response;
	
	@Column(name = "correct")
	private Boolean correct;
	
	@Column(name = "started_at", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date startedAt;
	
	@Column(name = "completed_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date completedAt;
	
	@Column(name = "time_spent_seconds")
	private Long timeSpentSeconds;
	
	@Column(name = "status", nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
	private ExerciseAttemptStatus status;
	
	@Override
	public Integer getId() {
		return id;
	}
	
	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public Exercise getExercise() {
		return exercise;
	}
	
	public void setExercise(Exercise exercise) {
		this.exercise = exercise;
	}
	
	public CourseEnrollment getEnrollment() {
		return enrollment;
	}
	
	public void setEnrollment(CourseEnrollment enrollment) {
		this.enrollment = enrollment;
	}
	
	public Integer getAttemptNumber() {
		return attemptNumber;
	}
	
	public void setAttemptNumber(Integer attemptNumber) {
		this.attemptNumber = attemptNumber;
	}
	
	public String getResponse() {
		return response;
	}
	
	public void setResponse(String response) {
		this.response = response;
	}
	
	public Boolean getCorrect() {
		return correct;
	}
	
	public void setCorrect(Boolean correct) {
		this.correct = correct;
	}
	
	public Date getStartedAt() {
		return startedAt;
	}
	
	public void setStartedAt(Date startedAt) {
		this.startedAt = startedAt;
	}
	
	public Date getCompletedAt() {
		return completedAt;
	}
	
	public void setCompletedAt(Date completedAt) {
		this.completedAt = completedAt;
	}
	
	public Long getTimeSpentSeconds() {
		return timeSpentSeconds;
	}
	
	public void setTimeSpentSeconds(Long timeSpentSeconds) {
		this.timeSpentSeconds = timeSpentSeconds;
	}
	
	public ExerciseAttemptStatus getStatus() {
		return status;
	}
	
	public void setStatus(ExerciseAttemptStatus status) {
		this.status = status;
	}
}
