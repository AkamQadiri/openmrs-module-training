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

@Entity
@Table(name = "training_course_module")
public class CourseModule extends BaseOpenmrsData {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "course_module_id")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "course_id", nullable = false)
	private Course course;
	
	@ManyToOne
	@JoinColumn(name = "lesson_id")
	private Lesson lesson;
	
	@ManyToOne
	@JoinColumn(name = "exercise_id")
	private Exercise exercise;
	
	@Column(name = "module_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private CourseModuleType moduleType;
	
	@Column(name = "sort_weight", nullable = false)
	private Integer sortWeight;
	
	@Column(name = "required", nullable = false)
	private Boolean required = true;
	
	@Override
	public Integer getId() {
		return id;
	}
	
	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Course getCourse() {
		return course;
	}
	
	public void setCourse(Course course) {
		this.course = course;
	}
	
	public CourseModuleType getModuleType() {
		return moduleType;
	}
	
	public void setModuleType(CourseModuleType moduleType) {
		this.moduleType = moduleType;
	}
	
	public Lesson getLesson() {
		return lesson;
	}
	
	public void setLesson(Lesson lesson) {
		this.lesson = lesson;
	}
	
	public Exercise getExercise() {
		return exercise;
	}
	
	public void setExercise(Exercise exercise) {
		this.exercise = exercise;
	}
	
	public Integer getSortWeight() {
		return sortWeight;
	}
	
	public void setSortWeight(Integer sortWeight) {
		this.sortWeight = sortWeight;
	}
	
	public Boolean isRequired() {
		return required;
	}
	
	public Boolean getRequired() {
		return required;
	}
	
	public void setRequired(Boolean required) {
		this.required = required;
	}
}
