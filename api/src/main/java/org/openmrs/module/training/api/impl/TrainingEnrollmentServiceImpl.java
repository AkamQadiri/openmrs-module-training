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

import org.openmrs.User;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.training.api.TrainingEnrollmentService;
import org.openmrs.module.training.api.dao.TrainingCourseDao;
import org.openmrs.module.training.api.dao.TrainingEnrollmentDao;
import org.openmrs.module.training.model.Course;
import org.openmrs.module.training.model.CourseEnrollment;
import org.openmrs.module.training.model.CourseModule;
import org.openmrs.module.training.model.CourseModuleType;
import org.openmrs.module.training.model.ModuleProgress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service("trainingEnrollmentService")
public class TrainingEnrollmentServiceImpl extends BaseOpenmrsService implements TrainingEnrollmentService {
	
	@Autowired
	private TrainingCourseDao courseDAO;
	
	@Autowired
	private TrainingEnrollmentDao enrollmentDAO;
	
	@Override
	@Transactional(readOnly = true)
	public CourseEnrollment getCourseEnrollmentByUuid(String uuid) {
		return enrollmentDAO.getCourseEnrollmentByUuid(uuid);
	}
	
	@Transactional(readOnly = true)
	public List<CourseEnrollment> getCurrentUserEnrollments() {
		User user = Context.getAuthenticatedUser();
		
		return enrollmentDAO.getCourseEnrollmentsByUser(user);
	}
	
	@Override
	@Transactional(readOnly = true)
	public CourseEnrollment getCurrentUserEnrollmentByCourse(Course course) {
		User user = Context.getAuthenticatedUser();
		
		return enrollmentDAO.getCourseEnrollmentByUserAndCourse(user, course);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<CourseEnrollment> getCourseEnrollments(Course course) {
		return enrollmentDAO.getCourseEnrollmentsByCourse(course);
	}
	
	@Override
	public CourseEnrollment enrollCurrentUserInCourse(Course course) {
		User user = Context.getAuthenticatedUser();
		
		// Check if already enrolled
		CourseEnrollment existing = enrollmentDAO.getCourseEnrollmentByUserAndCourse(user, course);
		if (existing != null && !existing.getVoided()) {
			return existing;
		}
		
		CourseEnrollment enrollment = new CourseEnrollment();
		enrollment.setUser(user);
		enrollment.setCourse(course);
		enrollment.setEnrolledAt(new Date());
		enrollment.setLastAccessedAt(new Date());
		
		return enrollmentDAO.saveCourseEnrollment(enrollment);
	}
	
	@Override
	public void updateEnrollmentProgress(CourseEnrollment enrollment) {
		List<CourseModule> allModules = courseDAO.getCourseModulesByCourse(enrollment.getCourse());
		List<ModuleProgress> progressList = enrollmentDAO.getModuleProgressByEnrollment(enrollment);
		
		long completedCount = progressList.stream().filter(ModuleProgress::getCompleted).count();
		
		int total = allModules.size();
		int progressPercentage = total > 0 ? (int) (completedCount * 100 / total) : 0;
		
		enrollment.setProgressPercentage(progressPercentage);
		enrollment.setLastAccessedAt(new Date());
		
		if (progressPercentage == 100 && enrollment.getCompletedAt() == null) {
			enrollment.setCompletedAt(new Date());
		}
		
		enrollmentDAO.saveCourseEnrollment(enrollment);
	}
	
	// Module
	@Override
	public void trackModuleAccess(CourseEnrollment enrollment, CourseModule module) {
		// Check if we need to complete the previous module
		List<CourseModule> allModules = courseDAO.getCourseModulesByCourse(enrollment.getCourse());
		int currentModuleIndex = -1;
		for (int i = 0; i < allModules.size(); i++) {
			if (allModules.get(i).getId().equals(module.getId())) {
				currentModuleIndex = i;
				break;
			}
		}
		
		// Complete previous module based on rules
		if (currentModuleIndex > 0) {
			CourseModule previousModule = allModules.get(currentModuleIndex - 1);
			ModuleProgress prevProgress = enrollmentDAO.getModuleProgress(enrollment, previousModule);
			
			// Only process if previous module was started but not completed
			if (prevProgress != null && !prevProgress.getCompleted()) {
				boolean shouldComplete = false;
				
				if (previousModule.getModuleType() == CourseModuleType.LESSON) {
					// All lessons complete when moving to next module
					shouldComplete = true;
				} else if (previousModule.getModuleType() == CourseModuleType.EXERCISE) {
					if (previousModule.isRequired()) {
						// Required exercises don't auto-complete here
						shouldComplete = false;
					} else {
						// Non-required exercises complete when moving forward
						shouldComplete = true;
					}
				}
				
				if (shouldComplete) {
					completeModule(enrollment, previousModule);
				}
			}
		}
		
		if (!canAccessModule(enrollment, module)) {
			throw new APIException(
			        Context.getMessageSourceService().getMessage("training.courseModule.prerequisites.notmet"));
		}
		
		// Track access to current module
		ModuleProgress progress = enrollmentDAO.getModuleProgress(enrollment, module);
		
		if (progress == null) {
			progress = new ModuleProgress();
			progress.setEnrollment(enrollment);
			progress.setCourseModule(module);
			progress.setStartedAt(new Date());
			enrollmentDAO.saveModuleProgress(progress);
		}
		
		// Update enrollment
		enrollment.setLastAccessedModule(module);
		enrollment.setLastAccessedAt(new Date());
		enrollmentDAO.saveCourseEnrollment(enrollment);
	}
	
	private boolean canAccessModule(CourseEnrollment enrollment, CourseModule module) {
		List<CourseModule> allModules = courseDAO.getCourseModulesByCourse(enrollment.getCourse());
		
		// Find current module index
		int moduleIndex = -1;
		for (int i = 0; i < allModules.size(); i++) {
			if (allModules.get(i).getId().equals(module.getId())) {
				moduleIndex = i;
				break;
			}
		}
		
		if (moduleIndex == 0) {
			return true; // First module is always accessible
		}
		
		// Check if all previous required modules are completed
		List<ModuleProgress> progressList = enrollmentDAO.getModuleProgressByEnrollment(enrollment);
		Map<Integer, ModuleProgress> progressMap = progressList.stream()
		        .collect(Collectors.toMap(p -> p.getCourseModule().getId(), p -> p));
		
		for (int i = 0; i < moduleIndex; i++) {
			CourseModule prevModule = allModules.get(i);
			if (prevModule.isRequired()) {
				ModuleProgress progress = progressMap.get(prevModule.getId());
				if (progress == null || !progress.getCompleted()) {
					return false; // Required module not completed
				}
			}
		}
		
		return true;
	}
	
	@Override
	public void completeModule(CourseEnrollment enrollment, CourseModule module) {
		ModuleProgress progress = enrollmentDAO.getModuleProgress(enrollment, module);
		
		if (progress == null) {
			progress = new ModuleProgress();
			progress.setEnrollment(enrollment);
			progress.setCourseModule(module);
			progress.setStartedAt(new Date());
		}
		
		if (!progress.getCompleted()) {
			progress.setCompleted(true);
			progress.setCompletedAt(new Date());
			
			// Calculate time spent
			if (progress.getStartedAt() != null && progress.getCompletedAt() != null) {
				long timeSpent = (progress.getCompletedAt().getTime() - progress.getStartedAt().getTime()) / 1000;
				progress.setTimeSpentSeconds(timeSpent);
			}
			
			enrollmentDAO.saveModuleProgress(progress);
			updateEnrollmentProgress(enrollment);
		}
	}
	
	@Override
	public CourseModule getNextIncompleteModule(CourseEnrollment enrollment) {
		List<CourseModule> allModules = courseDAO.getCourseModulesByCourse(enrollment.getCourse());
		List<ModuleProgress> progressList = enrollmentDAO.getModuleProgressByEnrollment(enrollment);
		
		Set<Integer> completedModuleIds = progressList.stream().filter(ModuleProgress::getCompleted)
		        .map(p -> p.getCourseModule().getId()).collect(Collectors.toSet());
		
		return allModules.stream().filter(m -> !completedModuleIds.contains(m.getId())).findFirst().orElse(null);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<ModuleProgress> getModuleProgress(CourseEnrollment enrollment) {
		return enrollmentDAO.getModuleProgressByEnrollment(enrollment);
	}
	
}
