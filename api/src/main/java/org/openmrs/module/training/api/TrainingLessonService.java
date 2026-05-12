/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.training.api;

import org.openmrs.annotation.Authorized;
import org.openmrs.api.OpenmrsService;
import org.openmrs.api.ValidationException;
import org.openmrs.module.training.api.util.TrainingPrivilegeConstants;
import org.openmrs.module.training.model.Lesson;

import java.util.List;

public interface TrainingLessonService extends OpenmrsService {
	
	@Authorized(TrainingPrivilegeConstants.MANAGE)
	Lesson getLessonById(Integer id);
	
	@Authorized(TrainingPrivilegeConstants.MANAGE)
	Lesson getLessonByUuid(String uuid);
	
	@Authorized(TrainingPrivilegeConstants.MANAGE)
	List<Lesson> getAllLessons(boolean includeRetired);
	
	@Authorized(TrainingPrivilegeConstants.MANAGE)
	Lesson saveLesson(Lesson lesson) throws ValidationException;
	
	@Authorized(TrainingPrivilegeConstants.MANAGE)
	void retireLesson(Lesson lesson, String reason);
}
