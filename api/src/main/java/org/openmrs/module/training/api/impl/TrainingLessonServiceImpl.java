/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.training.api.impl;

import org.openmrs.api.ValidationException;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.training.api.TrainingLessonService;
import org.openmrs.module.training.api.dao.TrainingLessonDao;
import org.openmrs.module.training.api.validator.LessonValidator;
import org.openmrs.module.training.model.Lesson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;

import java.util.Date;
import java.util.List;

@Service("trainingLessonService")
public class TrainingLessonServiceImpl extends BaseOpenmrsService implements TrainingLessonService {
	
	@Autowired
	private TrainingLessonDao lessonDAO;
	
	@Autowired
	private LessonValidator lessonValidator;
	
	@Override
	@Transactional(readOnly = true)
	public Lesson getLessonById(Integer id) {
		return lessonDAO.getLessonById(id);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Lesson getLessonByUuid(String uuid) {
		return lessonDAO.getLessonByUuid(uuid);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Lesson> getAllLessons(boolean includeRetired) {
		return lessonDAO.getAllLessons(includeRetired);
	}
	
	@Override
	public Lesson saveLesson(Lesson lesson) {
		Errors errors = new BindException(lesson, "lesson");
		lessonValidator.validate(lesson, errors);
		
		if (errors.hasErrors()) {
			throw new ValidationException(errors);
		}
		
		return lessonDAO.saveLesson(lesson);
	}
	
	@Override
	public void retireLesson(Lesson lesson, String reason) {
		lesson.setRetired(true);
		lesson.setRetireReason(reason);
		lesson.setDateRetired(new Date());
		lessonDAO.saveLesson(lesson);
	}
}
