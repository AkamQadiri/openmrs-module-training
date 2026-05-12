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

import org.openmrs.api.context.Context;
import org.openmrs.module.training.api.TrainingCourseService;
import org.openmrs.module.training.model.Course;
import org.openmrs.module.training.model.CourseModule;
import org.openmrs.module.training.model.CourseModuleType;
import org.openmrs.module.webservices.docs.swagger.core.property.EnumProperty;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
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

@SubResource(parent = CourseResource.class, path = "module", supportedClass = CourseModule.class, supportedOpenmrsVersions = {
        "1.11.6 - 2.*" })
public class CourseModuleSubResource extends DelegatingSubResource<CourseModule, Course, CourseResource> {
	
	@Override
	public CourseModule getByUniqueId(String uniqueId) {
		throw new UnsupportedOperationException("Uuid lookup for course module is not supported");
	}
	
	@Override
	protected void delete(CourseModule module, String reason, RequestContext context) throws ResponseException {
		getService().removeCourseModule(module);
	}
	
	@Override
	public CourseModule newDelegate() {
		return new CourseModule();
	}
	
	@Override
	public CourseModule save(CourseModule module) {
		return getService().saveCourseModule(module);
	}
	
	@Override
	public void purge(CourseModule module, RequestContext context) throws ResponseException {
		throw new UnsupportedOperationException("Purging course module is not supported");
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		
		if (rep instanceof RefRepresentation) {
			description.addProperty("uuid");
			description.addProperty("course", Representation.REF);
			description.addProperty("moduleType");
			description.addProperty("sortWeight");
			description.addProperty("required");
		} else if (rep instanceof DefaultRepresentation) {
			description.addProperty("uuid");
			description.addProperty("course", Representation.REF);
			description.addProperty("moduleType");
			description.addProperty("lesson", Representation.DEFAULT);
			description.addProperty("exercise", Representation.DEFAULT);
			description.addProperty("sortWeight");
			description.addProperty("required");
		} else if (rep instanceof FullRepresentation) {
			description.addProperty("uuid");
			description.addProperty("course", Representation.REF);
			description.addProperty("moduleType");
			description.addProperty("lesson", Representation.FULL);
			description.addProperty("exercise", Representation.FULL);
			description.addProperty("sortWeight");
			description.addProperty("required");
			description.addProperty("creator", Representation.REF);
			description.addProperty("dateCreated");
			description.addProperty("changedBy");
			description.addProperty("dateChanged");
			description.addProperty("voided");
			description.addProperty("voidedBy");
			description.addProperty("dateVoided");
			description.addProperty("voidReason");
		}
		
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("moduleType");
		description.addProperty("course_id");
		description.addProperty("lesson_id");
		description.addProperty("exercise_id");
		description.addRequiredProperty("sortWeight");
		description.addProperty("required");
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getUpdatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("sortWeight");
		description.addProperty("required");
		return description;
	}
	
	@Override
	public Model getGETModel(Representation rep) {
		ModelImpl model = new ModelImpl();
		
		if (rep instanceof RefRepresentation) {
			model.property("uuid", new StringProperty())
			        .property("course", new RefProperty("#/definitions/TrainingCourseGetRef"))
			        .property("moduleType", new EnumProperty(CourseModuleType.class))
			        .property("sortWeight", new IntegerProperty()).property("required", new BooleanProperty());
		} else if (rep instanceof DefaultRepresentation) {
			model.property("uuid", new StringProperty())
			        .property("course", new RefProperty("#/definitions/TrainingCourseGetRef"))
			        .property("moduleType", new EnumProperty(CourseModuleType.class))
			        .property("lesson", new RefProperty("#/definitions/TrainingLessonGet"))
			        .property("exercise", new RefProperty("#/definitions/TrainingExerciseGet"))
			        .property("sortWeight", new IntegerProperty()).property("required", new BooleanProperty());
		} else if (rep instanceof FullRepresentation) {
			model.property("uuid", new StringProperty())
			        .property("course", new RefProperty("#/definitions/TrainingCourseGetRef"))
			        .property("moduleType", new EnumProperty(CourseModuleType.class))
			        .property("lesson", new RefProperty("#/definitions/TrainingLessonGetFull"))
			        .property("exercise", new RefProperty("#/definitions/TrainingExerciseGetFull"))
			        .property("sortWeight", new IntegerProperty()).property("required", new BooleanProperty())
			        .property("creator", new RefProperty("#/definitions/UserGetRef"))
			        .property("dateCreated", new DateProperty())
			        .property("changedBy", new RefProperty("#/definitions/UserGetRef"))
			        .property("dateChanged", new DateProperty()).property("voided", new BooleanProperty())
			        .property("voidedBy", new RefProperty("#/definitions/UserGetRef"))
			        .property("dateVoided", new DateProperty()).property("voidReason", new StringProperty());
		}
		
		return model;
	}
	
	@Override
	public Model getCREATEModel(Representation rep) {
		ModelImpl model = new ModelImpl();
		
		model.property("course_id", new StringProperty()).property("moduleType", new StringProperty())
		        .property("lesson_id", new StringProperty()).property("exercise_id", new StringProperty())
		        .property("sortWeight", new IntegerProperty()).property("required", new BooleanProperty());
		
		model.required("moduleType").required("sortWeight");
		
		return model;
	}
	
	@Override
	public Model getUPDATEModel(Representation rep) {
		ModelImpl model = new ModelImpl();
		
		model.property("sortWeight", new IntegerProperty()).property("required", new BooleanProperty());
		
		return model;
	}
	
	@Override
	public Course getParent(CourseModule instance) {
		return instance.getCourse();
	}
	
	@Override
	public void setParent(CourseModule instance, Course parent) {
		instance.setCourse(parent);
	}
	
	@Override
	public PageableResult doGetAll(Course parent, RequestContext context) throws ResponseException {
		List<CourseModule> modules = getService().getCourseModulesByCourse(parent);
		return new NeedsPaging<CourseModule>(modules, context);
	}
	
	@PropertySetter("moduleType")
	public static void setModuleType(CourseModule module, String moduleType) {
		module.setModuleType(Enum.valueOf(CourseModuleType.class, moduleType.toUpperCase()));
	}
	
	private TrainingCourseService getService() {
		return Context.getService(TrainingCourseService.class);
	}
}
