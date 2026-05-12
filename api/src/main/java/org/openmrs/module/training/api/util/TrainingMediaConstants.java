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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TrainingMediaConstants {
	
	public static final String CONCEPT_UUID = "a8a0f3a2-1350-11df-a1f1-0026b9348838";
	
	public static final Set<String> ALLOWED_MIME_TYPES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
	    // Images
	    "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp", "image/svg+xml",
	    
	    // Videos
	    "video/mp4", "video/webm",
	    
	    // Documents
	    "application/pdf",
	    
	    // Audio
	    "audio/mpeg", // .mp3
	    "audio/webm")));
	
	public static final long MAX_FILE_SIZE = 52428800L; // 50 MB in bytes
}
