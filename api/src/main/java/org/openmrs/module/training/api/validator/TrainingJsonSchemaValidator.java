/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.training.api.validator;

import org.openmrs.api.APIException;
import org.openmrs.api.ValidationException;
import org.openmrs.api.context.Context;
import org.openmrs.module.training.api.util.TrainingJsonUtil;
import org.openmrs.module.training.model.ExerciseType;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import java.io.InputStream;
import java.util.Set;

@Component
public class TrainingJsonSchemaValidator {
	
	private final JsonSchemaFactory schemaFactory;
	
	public TrainingJsonSchemaValidator() {
		schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
	}
	
	public void validateLessonContent(String content) throws ValidationException {
		validateJson(content, "/schemas/lesson-content.schema.json");
	}
	
	public void validateExerciseContent(String content, ExerciseType exerciseType) throws ValidationException {
		String schemaPath = getExerciseContentSchemaPath(exerciseType);
		validateJson(content, schemaPath);
	}
	
	public void validateExerciseValidation(String validation) throws ValidationException {
		validateJson(validation, "/schemas/exercise-validation.schema.json");
	}
	
	public void validateExerciseFeedback(String feedback) throws ValidationException {
		if (feedback != null && !feedback.trim().isEmpty()) {
			validateJson(feedback, "/schemas/exercise-feedback.schema.json");
		}
	}
	
	public void validateExerciseAttemptResponse(String response, ExerciseType exerciseType) throws ValidationException {
		if (response != null && !response.trim().isEmpty()) {
			String schemaPath = getExerciseAttemptResponseSchemaPath(exerciseType);
			validateJson(response, schemaPath);
		}
	}
	
	private void validateJson(String jsonString, String schemaPath) throws ValidationException {
		try {
			JsonNode jsonNode = TrainingJsonUtil.parseJsonNode(jsonString);
			JsonSchema schema = loadSchema(schemaPath);
			
			Set<ValidationMessage> errors = schema.validate(jsonNode);
			
			if (!errors.isEmpty()) {
				StringBuilder errorDetails = new StringBuilder();
				for (ValidationMessage error : errors) {
					if (errorDetails.length() > 0) {
						errorDetails.append("; ");
					}
					errorDetails.append(error.getMessage());
				}
				
				throw new ValidationException(Context.getMessageSourceService().getMessage("training.json.validation.failed",
				    new Object[] { errorDetails.toString() }, Context.getLocale()));
			}
		}
		catch (APIException e) {
			throw e;
		}
		catch (Exception e) {
			throw new ValidationException(Context.getMessageSourceService().getMessage("training.json.validation.error",
			    new Object[] { e.getMessage() }, Context.getLocale()), e);
		}
	}
	
	private JsonSchema loadSchema(String schemaPath) throws APIException {
		try (InputStream schemaStream = getClass().getResourceAsStream(schemaPath)) {
			if (schemaStream == null) {
				throw new APIException(Context.getMessageSourceService().getMessage("training.json.schema.notfound",
				    new Object[] { schemaPath }, Context.getLocale()));
			}
			JsonNode schemaNode = TrainingJsonUtil.parseJsonNode(schemaStream);
			return schemaFactory.getSchema(schemaNode);
		}
		catch (Exception e) {
			throw new APIException(Context.getMessageSourceService().getMessage("training.json.schema.load.failed",
			    new Object[] { schemaPath, e.getMessage() }, Context.getLocale()), e);
		}
	}
	
	private String getExerciseContentSchemaPath(ExerciseType exerciseType) {
		switch (exerciseType) {
			case MULTIPLE_CHOICE:
				return "/schemas/exercise-content-multiple-choice.schema.json";
			case TRUE_FALSE:
				return "/schemas/exercise-content-true-false.schema.json";
			case FILL_IN_BLANK:
				return "/schemas/exercise-content-fill-blank.schema.json";
			case MATCHING:
				return "/schemas/exercise-content-matching.schema.json";
			case ORDERING:
				return "/schemas/exercise-content-ordering.schema.json";
			case CONCEPT_CREATION:
				return "/schemas/exercise-content-concept-creation.schema.json";
			case FORM_CREATION:
				return "/schemas/exercise-content-form-creation.schema.json";
			default:
				throw new APIException(Context.getMessageSourceService().getMessage("training.exercise.type.unknown",
				    new Object[] { exerciseType }, Context.getLocale()));
		}
	}
	
	private String getExerciseAttemptResponseSchemaPath(ExerciseType exerciseType) {
		switch (exerciseType) {
			case MULTIPLE_CHOICE:
				return "/schemas/exercise-attempt-response-multiple-choice.schema.json";
			case TRUE_FALSE:
				return "/schemas/exercise-attempt-response-true-false.schema.json";
			case FILL_IN_BLANK:
				return "/schemas/exercise-attempt-response-fill-blank.schema.json";
			case MATCHING:
				return "/schemas/exercise-attempt-response-matching.schema.json";
			case ORDERING:
				return "/schemas/exercise-attempt-response-ordering.schema.json";
			case CONCEPT_CREATION:
				return "/schemas/exercise-attempt-response-concept-creation.schema.json";
			case FORM_CREATION:
				return "/schemas/exercise-attempt-response-form-creation.schema.json";
			default:
				throw new APIException(Context.getMessageSourceService().getMessage("training.exercise.type.unknown",
				    new Object[] { exerciseType }, Context.getLocale()));
		}
	}
}
