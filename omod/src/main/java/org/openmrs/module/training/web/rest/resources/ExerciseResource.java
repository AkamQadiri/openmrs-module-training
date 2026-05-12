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
import org.openmrs.module.training.api.TrainingExerciseService;
import org.openmrs.module.training.api.util.TrainingJsonUtil;
import org.openmrs.module.training.model.Exercise;
import org.openmrs.module.training.model.ExerciseType;
import org.openmrs.module.webservices.docs.swagger.core.property.EnumProperty;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
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
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;

import java.util.List;

@Resource(name = RestConstants.VERSION_1
        + "/training/exercise", supportedClass = Exercise.class, supportedOpenmrsVersions = { "1.11.6 - 2.*" })
public class ExerciseResource extends DelegatingCrudResource<Exercise> {
	
	@Override
	public Exercise getByUniqueId(String uniqueId) {
		return getService().getExerciseByUuid(uniqueId);
	}
	
	@Override
	public void delete(Exercise exercise, String reason, RequestContext context) throws ResponseException {
		getService().retireExercise(exercise, reason);
	}
	
	@Override
	public Exercise newDelegate() {
		return new Exercise();
	}
	
	@Override
	public Exercise save(Exercise exercise) {
		return getService().saveExercise(exercise);
	}
	
	@Override
	public void purge(Exercise exercise, RequestContext context) throws ResponseException {
		throw new UnsupportedOperationException("Purging exercises is not supported");
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		
		if (rep instanceof RefRepresentation) {
			description.addProperty("uuid");
			description.addProperty("name");
			description.addProperty("exerciseType");
		} else if (rep instanceof DefaultRepresentation) {
			description.addProperty("uuid");
			description.addProperty("name");
			description.addProperty("exerciseType");
			description.addProperty("allowRetry");
		} else if (rep instanceof FullRepresentation) {
			description.addProperty("uuid");
			description.addProperty("name");
			description.addProperty("exerciseType");
			description.addProperty("content");
			description.addProperty("allowRetry");
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
		description.addRequiredProperty("exerciseType");
		description.addRequiredProperty("content");
		description.addRequiredProperty("validation");
		description.addProperty("feedback");
		description.addProperty("allowRetry");
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getUpdatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("exerciseType");
		description.addProperty("content");
		description.addProperty("validation");
		description.addProperty("feedback");
		description.addProperty("allowRetry");
		return description;
	}
	
	@Override
	public Model getGETModel(Representation rep) {
		ModelImpl model = new ModelImpl();
		
		if (rep instanceof RefRepresentation) {
			model.property("uuid", new StringProperty()).property("name", new StringProperty()).property("exerciseType",
			    new EnumProperty(ExerciseType.class));
		} else if (rep instanceof DefaultRepresentation) {
			model.property("uuid", new StringProperty()).property("name", new StringProperty())
			        .property("exerciseType", new EnumProperty(ExerciseType.class))
			        .property("allowRetry", new BooleanProperty());
		} else if (rep instanceof FullRepresentation) {
			model.property("uuid", new StringProperty()).property("name", new StringProperty())
			        .property("exerciseType", new EnumProperty(ExerciseType.class)).property("content", new ObjectProperty())
			        .property("allowRetry", new BooleanProperty())
			        .property("creator", new RefProperty("#/definitions/UserGetRef"))
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
		
		model.property("exerciseType", new StringProperty()).property("content", new ObjectProperty())
		        .property("validation", new StringProperty()).property("feedback", new StringProperty())
		        .property("allowRetry", new BooleanProperty());
		
		model.required("exerciseType").required("content").required("validation");
		
		return model;
	}
	
	@Override
	public Model getUPDATEModel(Representation rep) {
		ModelImpl model = new ModelImpl();
		
		model.property("exerciseType", new StringProperty()).property("content", new ObjectProperty())
		        .property("validation", new StringProperty()).property("feedback", new StringProperty())
		        .property("allowRetry", new BooleanProperty());
		
		return model;
	}
	
	@PropertySetter("exerciseType")
	public static void setExerciseType(Exercise exercise, String exerciseType) {
		exercise.setExerciseType(Enum.valueOf(ExerciseType.class, exerciseType.toUpperCase().replace(" ", "_")));
	}
	
	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		boolean includeRetired = context.getIncludeAll();
		List<Exercise> exercises = getService().getAllExercises(includeRetired);
		
		return new NeedsPaging<Exercise>(exercises, context);
	}
	
	private TrainingExerciseService getService() {
		return Context.getService(TrainingExerciseService.class);
	}
	
	@PropertyGetter("content")
	public Object getContentAsObject(Exercise exercise) {
		return TrainingJsonUtil.fromJson(exercise.getContent(), Object.class);
	}
	
	@PropertySetter("content")
	public void setContentFromObject(Exercise exercise, Object content) {
		exercise.setContent(TrainingJsonUtil.toMinifiedJson(content));
	}
	
	@PropertySetter("validation")
	public void setValidationFromObject(Exercise exercise, Object validation) {
		exercise.setValidation(TrainingJsonUtil.toMinifiedJson(validation));
	}
	
	@PropertySetter("feedback")
	public void setFeedbackFromObject(Exercise exercise, Object feedback) {
		exercise.setFeedback(TrainingJsonUtil.toMinifiedJson(feedback));
	}
}
