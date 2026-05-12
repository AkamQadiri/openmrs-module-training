/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.training.api.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.training.api.dao.TrainingLessonDao;
import org.openmrs.module.training.model.Lesson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository("trainingLessonDao")
@Transactional
public class TrainingLessonDaoImpl implements TrainingLessonDao {
	
	@Autowired
	private DbSessionFactory sessionFactory;
	
	private DbSession getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	@Override
	public Lesson getLessonById(Integer id) {
		return (Lesson) getSession().get(Lesson.class, id);
	}
	
	@Override
	public Lesson getLessonByUuid(String uuid) {
		Criteria criteria = getSession().createCriteria(Lesson.class);
		criteria.add(Restrictions.eq("uuid", uuid));
		return (Lesson) criteria.uniqueResult();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Lesson> getAllLessons(boolean includeRetired) {
		Criteria criteria = getSession().createCriteria(Lesson.class);
		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", false));
		}
		criteria.addOrder(Order.asc("dateCreated"));
		return criteria.list();
	}
	
	@Override
	public Lesson saveLesson(Lesson lesson) {
		getSession().saveOrUpdate(lesson);
		return lesson;
	}
}
