/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.training.web.rest.resources;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.training.api.TrainingCourseService;
import org.openmrs.module.training.model.Course;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.properties.BooleanProperty;
import io.swagger.models.properties.DateProperty;
import io.swagger.models.properties.IntegerProperty;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;

import java.util.List;

@Resource(name = RestConstants.VERSION_1 + "/training/course", supportedClass = Course.class, supportedOpenmrsVersions = {
        "1.11.6 - 2.*" })
public class CourseResource extends DelegatingCrudResource<Course> {
	
	@Override
	public Course getByUniqueId(String uniqueId) {
		return getService().getCourseByUuid(uniqueId);
	}
	
	@Override
	public void delete(Course course, String reason, RequestContext context) throws ResponseException {
		getService().retireCourse(course, reason);
	}
	
	@Override
	public Course newDelegate() {
		return new Course();
	}
	
	@Override
	public Course save(Course course) {
		return getService().saveCourse(course);
	}
	
	@Override
	public void purge(Course course, RequestContext context) throws ResponseException {
		throw new UnsupportedOperationException("Purging courses is not supported");
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		
		if (rep instanceof RefRepresentation) {
			description.addProperty("uuid");
			description.addProperty("name");
		} else if (rep instanceof DefaultRepresentation) {
			description.addProperty("uuid");
			description.addProperty("name");
			description.addProperty("description");
			description.addProperty("estimatedMinutes");
			description.addProperty("version");
			description.addProperty("published");
		} else if (rep instanceof FullRepresentation) {
			description.addProperty("uuid");
			description.addProperty("name");
			description.addProperty("description");
			description.addProperty("estimatedMinutes");
			description.addProperty("version");
			description.addProperty("published");
			description.addProperty("creator", Representation.REF);
			description.addProperty("dateCreated");
			description.addProperty("changedBy");
			description.addProperty("dateChanged");
			description.addProperty("retired");
			description.addProperty("retiredBy");
			description.addProperty("dateRetired");
			description.addProperty("retireReason");
		}
		
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("estimatedMinutes");
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getUpdatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("estimatedMinutes");
		description.addProperty("published");
		return description;
	}
	
	@Override
	public Model getGETModel(Representation rep) {
		ModelImpl model = new ModelImpl();
		
		if (rep instanceof RefRepresentation) {
			model.property("uuid", new StringProperty()).property("name", new StringProperty());
		} else if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			model.property("uuid", new StringProperty()).property("name", new StringProperty())
			        .property("description", new StringProperty()).property("estimatedMinutes", new IntegerProperty())
			        .property("version", new IntegerProperty()).property("published", new BooleanProperty());
		}
		
		if (rep instanceof FullRepresentation) {
			model.property("creator", new RefProperty("#/definitions/UserGetRef"))
			        .property("dateCreated", new DateProperty())
			        .property("changedBy", new RefProperty("#/definitions/UserGetRef"))
			        .property("dateChanged", new DateProperty()).property("retired", new BooleanProperty())
			        .property("retiredBy", new RefProperty("#/definitions/UserGetRef"))
			        .property("dateRetired", new DateProperty()).property("retireReason", new StringProperty());
		}
		
		return model;
	}
	
	@Override
	public Model getCREATEModel(Representation rep) {
		ModelImpl model = new ModelImpl();
		
		model.property("estimatedMinutes", new IntegerProperty());
		
		return model;
	}
	
	@Override
	public Model getUPDATEModel(Representation rep) {
		ModelImpl model = new ModelImpl();
		
		model.property("estimatedMinutes", new IntegerProperty()).property("published", new BooleanProperty());
		
		return model;
	}
	
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String query = context.getParameter("q");
		
		boolean includeRetired = context.getIncludeAll();
		String publishedParam = context.getParameter("published");
		boolean publishedOnly = publishedParam != null ? Boolean.parseBoolean(publishedParam) : false;
		
		List<Course> courses;
		
		if (!StringUtils.isBlank(query)) {
			courses = getService().searchCourses(query, includeRetired, publishedOnly);
		} else {
			courses = publishedOnly ? getService().getPublishedCourses() : getService().getAllCourses(includeRetired);
		}
		
		return new NeedsPaging<Course>(courses, context);
	}
	
	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		boolean includeRetired = context.getIncludeAll();
		List<Course> courses = getService().getAllCourses(includeRetired);
		
		return new NeedsPaging<Course>(courses, context);
	}
	
	private TrainingCourseService getService() {
		return Context.getService(TrainingCourseService.class);
	}
}
