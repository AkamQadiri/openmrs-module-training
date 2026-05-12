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
import org.openmrs.module.training.api.dao.TrainingExerciseDao;
import org.openmrs.module.training.model.CourseEnrollment;
import org.openmrs.module.training.model.Exercise;
import org.openmrs.module.training.model.ExerciseAttempt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository("trainingExerciseDao")
@Transactional
public class TrainingExerciseDaoImpl implements TrainingExerciseDao {
	
	@Autowired
	private DbSessionFactory sessionFactory;
	
	private DbSession getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	@Override
	public Exercise getExerciseById(Integer id) {
		return (Exercise) getSession().get(Exercise.class, id);
	}
	
	@Override
	public Exercise getExerciseByUuid(String uuid) {
		Criteria criteria = getSession().createCriteria(Exercise.class);
		criteria.add(Restrictions.eq("uuid", uuid));
		return (Exercise) criteria.uniqueResult();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Exercise> getAllExercises(boolean includeRetired) {
		Criteria criteria = getSession().createCriteria(Exercise.class);
		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", false));
		}
		criteria.addOrder(Order.asc("dateCreated"));
		return criteria.list();
	}
	
	@Override
	public Exercise saveExercise(Exercise exercise) {
		getSession().saveOrUpdate(exercise);
		return exercise;
	}
	
	// Exercise Attempt
	@Override
	public ExerciseAttempt saveExerciseAttempt(ExerciseAttempt attempt) {
		getSession().saveOrUpdate(attempt);
		return attempt;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<ExerciseAttempt> getExerciseAttemptsByEnrollmentAndExercise(CourseEnrollment enrollment, Exercise exercise) {
		Criteria criteria = getSession().createCriteria(ExerciseAttempt.class);
		criteria.add(Restrictions.eq("exercise", exercise));
		if (enrollment != null) {
			criteria.add(Restrictions.eq("enrollment", enrollment));
		}
		criteria.add(Restrictions.eq("voided", false));
		criteria.addOrder(Order.desc("attemptNumber"));
		return criteria.list();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<ExerciseAttempt> getExerciseAttemptsByExercise(Exercise exercise) {
		Criteria criteria = getSession().createCriteria(ExerciseAttempt.class);
		criteria.add(Restrictions.eq("exercise", exercise));
		criteria.add(Restrictions.eq("voided", false));
		return criteria.list();
	}
}
