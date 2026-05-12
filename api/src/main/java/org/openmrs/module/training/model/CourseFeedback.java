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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.openmrs.BaseOpenmrsData;

@Entity
@Table(name = "training_course_feedback")
public class CourseFeedback extends BaseOpenmrsData {
	
	public static final int MIN_RATING = 1;
	
	public static final int MAX_RATING = 5;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "feedback_id")
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "enrollment_id", nullable = false)
	private CourseEnrollment enrollment;
	
	@Column(name = "clarity_rating", nullable = false)
	@Min(value = MIN_RATING)
	@Max(value = MAX_RATING)
	private Integer clarityRating;
	
	@Column(name = "difficulty_rating", nullable = false)
	@Min(value = MIN_RATING)
	@Max(value = MAX_RATING)
	private Integer difficultyRating;
	
	@Column(name = "usefulness_rating", nullable = false)
	@Min(value = MIN_RATING)
	@Max(value = MAX_RATING)
	private Integer usefulnessRating;
	
	@Column(name = "overall_rating", nullable = false)
	@Min(value = MIN_RATING)
	@Max(value = MAX_RATING)
	private Integer overallRating;
	
	@Column(name = "comment", columnDefinition = "TEXT")
	private String comment;
	
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
	
	public Integer getClarityRating() {
		return clarityRating;
	}
	
	public void setClarityRating(Integer clarityRating) {
		this.clarityRating = clarityRating;
	}
	
	public Integer getDifficultyRating() {
		return difficultyRating;
	}
	
	public void setDifficultyRating(Integer difficultyRating) {
		this.difficultyRating = difficultyRating;
	}
	
	public Integer getUsefulnessRating() {
		return usefulnessRating;
	}
	
	public void setUsefulnessRating(Integer usefulnessRating) {
		this.usefulnessRating = usefulnessRating;
	}
	
	public Integer getOverallRating() {
		return overallRating;
	}
	
	public void setOverallRating(Integer overallRating) {
		this.overallRating = overallRating;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
}
