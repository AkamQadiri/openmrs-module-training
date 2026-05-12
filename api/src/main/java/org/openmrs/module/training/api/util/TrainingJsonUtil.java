/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.training.api.util;

import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.io.InputStream;

public class TrainingJsonUtil {
	
	private static final ObjectMapper MINIFIED_MAPPER = new ObjectMapper();
	
	static {
		// Configure mapper for minified output
		MINIFIED_MAPPER.configure(SerializationFeature.INDENT_OUTPUT, false);
	}
	
	/**
	 * Converts an object to JSON string representation.
	 * 
	 * @param object the object to serialize
	 * @return JSON string representation
	 * @throws APIException if serialization fails
	 */
	public static String toJsonString(Object object) {
		if (object == null) {
			return null;
		}
		
		try {
			return MINIFIED_MAPPER.writeValueAsString(object);
		}
		catch (JsonProcessingException e) {
			throw new APIException(Context.getMessageSourceService().getMessage("training.json.object.serialize.failed",
			    new Object[] { e.getMessage() }, Context.getLocale()), e);
		}
	}
	
	/**
	 * Converts an object to minified JSON string
	 * 
	 * @param object the object to serialize
	 * @return minified JSON string
	 * @throws APIException if serialization fails
	 */
	public static String toMinifiedJson(Object object) {
		if (object == null) {
			return null;
		}
		
		try {
			return MINIFIED_MAPPER.writeValueAsString(object);
		}
		catch (JsonProcessingException e) {
			throw new APIException(Context.getMessageSourceService().getMessage("training.json.serialize.failed",
			    new Object[] { e.getMessage() }, Context.getLocale()), e);
		}
	}
	
	/**
	 * Parses a JSON string to the specified class
	 * 
	 * @param json the JSON string
	 * @param clazz the target class
	 * @return deserialized object
	 * @throws APIException if parsing fails
	 */
	public static <T> T fromJson(String json, Class<T> clazz) {
		if (json == null || json.trim().isEmpty()) {
			return null;
		}
		
		try {
			return MINIFIED_MAPPER.readValue(json, clazz);
		}
		catch (JsonProcessingException e) {
			throw new APIException(Context.getMessageSourceService().getMessage("training.json.parse.failed",
			    new Object[] { e.getMessage() }, Context.getLocale()), e);
		}
	}
	
	/**
	 * Parses a JSON string to a JsonNode
	 * 
	 * @param json the JSON string
	 * @return JsonNode representation
	 * @throws APIException if parsing fails
	 */
	public static JsonNode parseJsonNode(String json) {
		if (json == null || json.trim().isEmpty()) {
			return null;
		}
		
		try {
			return MINIFIED_MAPPER.readTree(json);
		}
		catch (JsonProcessingException e) {
			throw new APIException(Context.getMessageSourceService().getMessage("training.json.parse.failed",
			    new Object[] { e.getMessage() }, Context.getLocale()), e);
		}
	}
	
	/**
	 * Parses JSON from an InputStream to a JsonNode
	 * 
	 * @param inputStream the input stream containing JSON
	 * @return JsonNode representation
	 * @throws APIException if parsing fails
	 */
	public static JsonNode parseJsonNode(InputStream inputStream) {
		if (inputStream == null) {
			throw new APIException(Context.getMessageSourceService().getMessage("training.json.inputstream.null", null,
			    Context.getLocale()));
		}
		
		try {
			// Use regular mapper for reading resources (schemas don't need minification)
			return MINIFIED_MAPPER.readTree(inputStream);
		}
		catch (IOException e) {
			throw new APIException(Context.getMessageSourceService().getMessage("training.json.parse.stream.failed",
			    new Object[] { e.getMessage() }, Context.getLocale()), e);
		}
	}
}
