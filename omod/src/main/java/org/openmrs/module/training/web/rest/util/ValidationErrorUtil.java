/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.training.web.rest.util;

import org.openmrs.api.ValidationException;
import org.openmrs.api.context.Context;

import java.util.stream.Collectors;

public class ValidationErrorUtil {
	
	/**
	 * Extracts validation error messages from a ValidationException
	 * 
	 * @param e the ValidationException
	 * @return formatted error message string
	 */
	public static String extractValidationMessage(ValidationException e) {
		if (e.getErrors() != null && e.getErrors().hasErrors()) {
			return e.getErrors().getAllErrors().stream().map(error -> Context.getMessageSourceService()
			        .getMessage(error.getCode(), error.getArguments(), error.getDefaultMessage(), Context.getLocale()))
			        .collect(Collectors.joining("; "));
		}
		return e.getMessage();
	}
}
