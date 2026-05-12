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

import java.util.Date;

@Entity
@Table(name = "training_module_progress")
public class ModuleProgress extends BaseOpenmrsData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "module_progress_id")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "enrollment_id", nullable = false)
	private CourseEnrollment enrollment;
	
	@ManyToOne
	@JoinColumn(name = "course_module_id", nullable = false)
	private CourseModule courseModule;
	
	@Column(name = "completed", nullable = false)
	private Boolean completed = false;
	
	@Column(name = "started_at", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date startedAt;
	
	@Column(name = "completed_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date completedAt;
	
	@Column(name = "time_spent_seconds")
	private Long timeSpentSeconds;
	
	@Override
	public Integer getId() {
		return id;
	}
	
	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	public CourseEnrollment getEnrollment() {
		return enrollment;
	}
	
	public void setEnrollment(CourseEnrollment enrollment) {
		this.enrollment = enrollment;
	}
	
	public CourseModule getCourseModule() {
		return courseModule;
	}
	
	public void setCourseModule(CourseModule courseModule) {
		this.courseModule = courseModule;
	}
	
	public Boolean getCompleted() {
		return completed;
	}
	
	public void setCompleted(Boolean completed) {
		this.completed = completed;
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
}
