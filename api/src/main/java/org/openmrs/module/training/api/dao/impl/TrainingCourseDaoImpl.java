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

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.training.api.dao.TrainingCourseDao;
import org.openmrs.module.training.model.Course;
import org.openmrs.module.training.model.CourseEnrollment;
import org.openmrs.module.training.model.CourseFeedback;
import org.openmrs.module.training.model.CourseModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository("trainingCourseDao")
@Transactional
public class TrainingCourseDaoImpl implements TrainingCourseDao {
	
	@Autowired
	private DbSessionFactory sessionFactory;
	
	private DbSession getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	@Override
	public Course getCourseById(Integer id) {
		return (Course) getSession().get(Course.class, id);
	}
	
	@Override
	public Course getCourseByUuid(String uuid) {
		Criteria criteria = getSession().createCriteria(Course.class);
		criteria.add(Restrictions.eq("uuid", uuid));
		return (Course) criteria.uniqueResult();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Course> getAllCourses(boolean includeRetired) {
		Criteria criteria = getSession().createCriteria(Course.class);
		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", false));
		}
		criteria.addOrder(Order.asc("dateCreated"));
		return criteria.list();
	}
	
	@Override
	public Course saveCourse(Course course) {
		getSession().saveOrUpdate(course);
		return course;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Course> searchCourses(String query, boolean includeRetired, boolean includePublishedOnly) {
		Criteria criteria = getSession().createCriteria(Course.class, "c");
		
		// Search in name and description fields
		if (StringUtils.isNotBlank(query)) {
			String searchPattern = "%" + query.toLowerCase() + "%";
			
			criteria.createAlias("c.names", "cn", Criteria.LEFT_JOIN);
			criteria.createAlias("c.descriptions", "cd", Criteria.LEFT_JOIN);
			
			criteria.add(Restrictions.or(
			    Restrictions.and(Restrictions.ilike("cn.name", searchPattern), Restrictions.eq("cn.voided", false)),
			    Restrictions.and(Restrictions.ilike("cd.description", searchPattern), Restrictions.eq("cd.voided", false))));
		}
		
		// Filter by retired status
		if (!includeRetired) {
			criteria.add(Restrictions.eq("c.retired", false));
		}
		
		// Filter by published status if specified
		if (includePublishedOnly) {
			criteria.add(Restrictions.eq("c.published", includePublishedOnly));
		}
		
		// Make results distinct
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		// Order by creation date
		criteria.addOrder(Order.asc("c.dateCreated"));
		
		return criteria.list();
	}
	
	// Course Module
	@Override
	public CourseModule getCourseModuleByUuid(String uuid) {
		Criteria criteria = getSession().createCriteria(CourseModule.class);
		criteria.add(Restrictions.eq("uuid", uuid));
		return (CourseModule) criteria.uniqueResult();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<CourseModule> getCourseModulesByCourse(Course course) {
		Criteria criteria = getSession().createCriteria(CourseModule.class);
		criteria.add(Restrictions.eq("course", course));
		criteria.add(Restrictions.eq("voided", false));
		criteria.addOrder(Order.asc("sortWeight"));
		return criteria.list();
	}
	
	@Override
	public CourseModule saveCourseModule(CourseModule module) {
		getSession().saveOrUpdate(module);
		return module;
	}
	
	@Override
	public void deleteCourseModule(CourseModule module) {
		getSession().delete(module);
	}
	
	// Feedback
	@Override
	public CourseFeedback getCourseFeedbackByEnrollment(CourseEnrollment enrollment) {
		Criteria criteria = getSession().createCriteria(CourseFeedback.class);
		criteria.add(Restrictions.eq("enrollment", enrollment));
		criteria.add(Restrictions.eq("voided", false));
		return (CourseFeedback) criteria.uniqueResult();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<CourseFeedback> getCourseFeedbacksByCourse(Course course) {
		String hql = "SELECT f FROM CourseFeedback f " + "WHERE f.enrollment.course = :course AND f.voided = false "
		        + "ORDER BY f.dateCreated DESC";
		return getSession().createQuery(hql).setParameter("course", course).list();
	}
	
	@Override
	public CourseFeedback saveCourseFeedback(CourseFeedback feedback) {
		getSession().saveOrUpdate(feedback);
		return feedback;
	}
}
