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
import org.openmrs.User;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.training.api.dao.TrainingEnrollmentDao;
import org.openmrs.module.training.model.Course;
import org.openmrs.module.training.model.CourseEnrollment;
import org.openmrs.module.training.model.CourseModule;
import org.openmrs.module.training.model.ModuleProgress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository("trainingEnrollmentDao")
@Transactional
public class TrainingEnrollmentDaoImpl implements TrainingEnrollmentDao {
	
	@Autowired
	private DbSessionFactory sessionFactory;
	
	private DbSession getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	@Override
	public CourseEnrollment getCourseEnrollmentByUuid(String uuid) {
		Criteria criteria = getSession().createCriteria(CourseEnrollment.class);
		criteria.add(Restrictions.eq("uuid", uuid));
		return (CourseEnrollment) criteria.uniqueResult();
	}
	
	@Override
	public CourseEnrollment getCourseEnrollmentByUserAndCourse(User user, Course course) {
		Criteria criteria = getSession().createCriteria(CourseEnrollment.class);
		criteria.add(Restrictions.eq("user", user));
		criteria.add(Restrictions.eq("course", course));
		criteria.add(Restrictions.eq("voided", false));
		return (CourseEnrollment) criteria.uniqueResult();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<CourseEnrollment> getCourseEnrollmentsByUser(User user) {
		Criteria criteria = getSession().createCriteria(CourseEnrollment.class);
		criteria.add(Restrictions.eq("user", user));
		criteria.add(Restrictions.eq("voided", false));
		criteria.addOrder(Order.desc("enrolledAt"));
		return criteria.list();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<CourseEnrollment> getCourseEnrollmentsByCourse(Course course) {
		Criteria criteria = getSession().createCriteria(CourseEnrollment.class);
		criteria.add(Restrictions.eq("course", course));
		criteria.add(Restrictions.eq("voided", false));
		criteria.addOrder(Order.desc("enrolledAt"));
		return criteria.list();
	}
	
	@Override
	public CourseEnrollment saveCourseEnrollment(CourseEnrollment enrollment) {
		getSession().saveOrUpdate(enrollment);
		return enrollment;
	}
	
	// Module Progress
	@Override
	public ModuleProgress getModuleProgress(CourseEnrollment enrollment, CourseModule module) {
		Criteria criteria = getSession().createCriteria(ModuleProgress.class);
		criteria.add(Restrictions.eq("enrollment", enrollment));
		criteria.add(Restrictions.eq("courseModule", module));
		criteria.add(Restrictions.eq("voided", false));
		return (ModuleProgress) criteria.uniqueResult();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<ModuleProgress> getModuleProgressByEnrollment(CourseEnrollment enrollment) {
		Criteria criteria = getSession().createCriteria(ModuleProgress.class);
		criteria.add(Restrictions.eq("enrollment", enrollment));
		criteria.add(Restrictions.eq("voided", false));
		return criteria.list();
	}
	
	@Override
	public ModuleProgress saveModuleProgress(ModuleProgress progress) {
		getSession().saveOrUpdate(progress);
		return progress;
	}
}
