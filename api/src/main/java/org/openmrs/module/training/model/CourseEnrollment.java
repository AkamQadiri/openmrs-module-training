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
@Table(name = "training_course_enrollment")
public class CourseEnrollment extends BaseOpenmrsData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "enrollment_id")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "course_id", nullable = false)
	private Course course;
	
	@Column(name = "enrolled_at", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date enrolledAt;
	
	@Column(name = "completed_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date completedAt;
	
	@Column(name = "last_accessed_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastAccessedAt;
	
	@ManyToOne
	@JoinColumn(name = "last_accessed_module_id")
	private CourseModule lastAccessedModule;
	
	@Column(name = "progress_percentage", nullable = false)
	private Integer progressPercentage = 0;
	
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
	
	public Course getCourse() {
		return course;
	}
	
	public void setCourse(Course course) {
		this.course = course;
	}
	
	public Date getEnrolledAt() {
		return enrolledAt;
	}
	
	public void setEnrolledAt(Date enrolledAt) {
		this.enrolledAt = enrolledAt;
	}
	
	public Date getCompletedAt() {
		return completedAt;
	}
	
	public void setCompletedAt(Date completedAt) {
		this.completedAt = completedAt;
	}
	
	public Date getLastAccessedAt() {
		return lastAccessedAt;
	}
	
	public void setLastAccessedAt(Date lastAccessedAt) {
		this.lastAccessedAt = lastAccessedAt;
	}
	
	public CourseModule getLastAccessedModule() {
		return lastAccessedModule;
	}
	
	public void setLastAccessedModule(CourseModule lastAccessedModule) {
		this.lastAccessedModule = lastAccessedModule;
	}
	
	public Integer getProgressPercentage() {
		return progressPercentage;
	}
	
	public void setProgressPercentage(Integer progressPercentage) {
		this.progressPercentage = progressPercentage;
	}
}
