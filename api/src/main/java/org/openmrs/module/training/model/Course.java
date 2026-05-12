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

import org.openmrs.Auditable;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.Retireable;
import org.openmrs.User;
import org.openmrs.api.context.Context;

import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Entity
@Table(name = "training_course")
public class Course extends BaseOpenmrsObject implements Auditable, Retireable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "course_id")
	private Integer id;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "course")
	private Set<CourseName> names = new HashSet<>();
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "course")
	private Set<CourseDescription> descriptions = new HashSet<>();
	
	@Column(name = "estimated_minutes")
	private Integer estimatedMinutes;
	
	@Column(name = "version")
	private Integer version = 1;
	
	@Column(name = "published", nullable = false)
	private Boolean published = false;
	
	@ManyToOne
	@JoinColumn(name = "creator")
	private User creator;
	
	@Column(name = "date_created", nullable = false)
	private Date dateCreated;
	
	@ManyToOne
	@JoinColumn(name = "changed_by")
	private User changedBy;
	
	@Column(name = "date_changed")
	private Date dateChanged;
	
	@Column(name = "retired", nullable = false)
	private Boolean retired = Boolean.FALSE;
	
	@ManyToOne
	@JoinColumn(name = "retired_by")
	private User retiredBy;
	
	@Column(name = "date_retired")
	private Date dateRetired;
	
	@Column(name = "retire_reason")
	private String retireReason;
	
	@Override
	public Integer getId() {
		return id;
	}
	
	@Override
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getName() {
		Locale locale = Context.getLocale();
		
		// Try current locale
		CourseName name = names.stream().filter(n -> !n.getVoided() && n.getLocale().equals(locale)).findFirst()
		        .orElse(null);
		
		// Try English if null
		if (name == null) {
			name = names.stream().filter(n -> !n.getVoided() && n.getLocale().equals(Locale.ENGLISH)).findFirst()
			        .orElse(null);
		}
		
		// Fallback to first non-voided name
		if (name == null && !names.isEmpty()) {
			name = names.stream().filter(n -> !n.getVoided()).findFirst().orElse(null);
		}
		
		return name != null ? name.getName() : null;
	}
	
	public Set<CourseName> getNames() {
		return names;
	}
	
	public void setNames(Set<CourseName> names) {
		this.names = names;
	}
	
	public String getDescription() {
		Locale locale = Context.getLocale();
		
		// Try current locale
		CourseDescription description = descriptions.stream().filter(n -> !n.getVoided() && n.getLocale().equals(locale))
		        .findFirst().orElse(null);
		
		// Try English if null
		if (description == null) {
			description = descriptions.stream().filter(n -> !n.getVoided() && n.getLocale().equals(Locale.ENGLISH))
			        .findFirst().orElse(null);
		}
		
		// Fallback to first non-voided description
		if (description == null && !descriptions.isEmpty()) {
			description = descriptions.stream().filter(n -> !n.getVoided()).findFirst().orElse(null);
		}
		
		return description != null ? description.getDescription() : null;
	}
	
	public Set<CourseDescription> getDescriptions() {
		return descriptions;
	}
	
	public void setDescriptions(Set<CourseDescription> descriptions) {
		this.descriptions = descriptions;
	}
	
	public Integer getEstimatedMinutes() {
		return estimatedMinutes;
	}
	
	public void setEstimatedMinutes(Integer estimatedMinutes) {
		this.estimatedMinutes = estimatedMinutes;
	}
	
	public Integer getVersion() {
		return version;
	}
	
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public Boolean getPublished() {
		return published;
	}
	
	public void setPublished(Boolean published) {
		this.published = published;
	}
	
	@Override
	public Date getDateRetired() {
		return this.dateRetired;
	}
	
	@Override
	public String getRetireReason() {
		return this.retireReason;
	}
	
	@Override
	public User getRetiredBy() {
		return this.retiredBy;
	}
	
	@Override
	public Boolean isRetired() {
		return this.retired;
	}
	
	public Boolean getRetired() {
		return this.isRetired();
	}
	
	@Override
	public void setDateRetired(Date dateRetired) {
		this.dateRetired = dateRetired;
	}
	
	@Override
	public void setRetireReason(String retireReason) {
		this.retireReason = retireReason;
	}
	
	@Override
	public void setRetired(Boolean retired) {
		this.retired = retired;
	}
	
	@Override
	public void setRetiredBy(User retiredBy) {
		this.retiredBy = retiredBy;
	}
	
	@Override
	public User getChangedBy() {
		return this.changedBy;
	}
	
	@Override
	public User getCreator() {
		return this.creator;
	}
	
	@Override
	public Date getDateChanged() {
		return this.dateChanged;
	}
	
	@Override
	public Date getDateCreated() {
		return this.dateCreated;
	}
	
	@Override
	public void setChangedBy(User changedBy) {
		this.changedBy = changedBy;
	}
	
	@Override
	public void setCreator(User creator) {
		this.creator = creator;
	}
	
	@Override
	public void setDateChanged(Date dateChanged) {
		this.dateChanged = dateChanged;
	}
	
	@Override
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
}
