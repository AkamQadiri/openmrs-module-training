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
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.training.api.TrainingAnalyticsService;
import org.openmrs.module.training.api.dao.TrainingCourseDao;
import org.openmrs.module.training.api.dao.TrainingEnrollmentDao;
import org.openmrs.module.training.api.dao.TrainingExerciseDao;
import org.openmrs.module.training.model.Course;
import org.openmrs.module.training.model.CourseEnrollment;
import org.openmrs.module.training.model.CourseFeedback;
import org.openmrs.module.training.model.CourseModule;
import org.openmrs.module.training.model.CourseModuleType;
import org.openmrs.module.training.model.Exercise;
import org.openmrs.module.training.model.ExerciseAttempt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("trainingAnalyticsService")
public class TrainingAnalyticsServiceImpl extends BaseOpenmrsService implements TrainingAnalyticsService {
	
	@Autowired
	private TrainingCourseDao courseDAO;
	
	@Autowired
	private TrainingEnrollmentDao enrollmentDAO;
	
	@Autowired
	private TrainingExerciseDao exerciseDAO;
	
	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> getCourseAnalytics(Course course) {
		Map<String, Object> analytics = new HashMap<>();
		
		List<CourseEnrollment> enrollments = enrollmentDAO.getCourseEnrollmentsByCourse(course);
		
		analytics.put("totalEnrollments", enrollments.size());
		analytics.put("activeEnrollments",
		    enrollments.stream().filter(e -> e.getCompletedAt() == null && !e.getVoided()).count());
		analytics.put("completedEnrollments", enrollments.stream().filter(e -> e.getCompletedAt() != null).count());
		
		// Average progress
		double avgProgress = enrollments.stream().mapToInt(CourseEnrollment::getProgressPercentage).average().orElse(0);
		analytics.put("averageProgress", avgProgress);
		
		// Completion rate
		long completed = enrollments.stream().filter(e -> e.getCompletedAt() != null).count();
		double completionRate = enrollments.size() > 0 ? (completed * 100.0 / enrollments.size()) : 0;
		analytics.put("completionRate", completionRate);
		
		// Exercise analytics
		List<Map<String, Object>> exerciseStats = new ArrayList<>();
		List<CourseModule> structure = courseDAO.getCourseModulesByCourse(course);
		
		for (CourseModule module : structure) {
			if (module.getModuleType() == CourseModuleType.EXERCISE) {
				exerciseStats.add(getExerciseAnalytics(module.getExercise()));
			}
		}
		analytics.put("exerciseStatistics", exerciseStats);
		
		// Feedback summary
		analytics.put("feedbackSummary", getCourseFeedbackSummary(course));
		
		return analytics;
	}
	
	private Map<String, Object> getCourseFeedbackSummary(Course course) {
		List<CourseFeedback> feedbackList = courseDAO.getCourseFeedbacksByCourse(course);
		
		Map<String, Object> summary = new HashMap<>();
		summary.put("totalResponses", feedbackList.size());
		
		if (feedbackList.isEmpty()) {
			return summary;
		}
		
		// Calculate averages
		double avgClarity = feedbackList.stream().mapToInt(CourseFeedback::getClarityRating).average().orElse(0);
		double avgDifficulty = feedbackList.stream().mapToInt(CourseFeedback::getDifficultyRating).average().orElse(0);
		double avgUsefulness = feedbackList.stream().mapToInt(CourseFeedback::getUsefulnessRating).average().orElse(0);
		double avgOverall = feedbackList.stream().mapToInt(CourseFeedback::getOverallRating).average().orElse(0);
		
		summary.put("averageClarityRating", avgClarity);
		summary.put("averageDifficultyRating", avgDifficulty);
		summary.put("averageUsefulnessRating", avgUsefulness);
		summary.put("averageOverallRating", avgOverall);
		
		// Collect comments
		List<String> comments = feedbackList.stream().map(CourseFeedback::getComment)
		        .filter(c -> c != null && !c.trim().isEmpty()).collect(Collectors.toList());
		
		summary.put("comments", comments);
		
		return summary;
	}
	
	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> getExerciseAnalytics(Exercise exercise) {
		Map<String, Object> analytics = new HashMap<>();
		
		analytics.put("exerciseName", exercise.getName());
		analytics.put("exerciseType", exercise.getExerciseType());
		
		List<ExerciseAttempt> allAttempts = exerciseDAO.getExerciseAttemptsByExercise(exercise);
		List<ExerciseAttempt> validAttempts = allAttempts.stream().filter(a -> a.getCorrect() != null)
		        .collect(Collectors.toList());
		
		analytics.put("totalAttempts", validAttempts.size());
		analytics.put("uniqueUsers", validAttempts.stream().map(ExerciseAttempt::getUser).distinct().count());
		
		long successfulAttempts = validAttempts.stream().filter(ExerciseAttempt::getCorrect).count();
		double successRate = validAttempts.size() > 0 ? (successfulAttempts * 100.0 / validAttempts.size()) : 0;
		analytics.put("successRate", successRate);
		
		// Average attempts per user
		Map<User, Long> attemptsPerUser = allAttempts.stream()
		        .collect(Collectors.groupingBy(ExerciseAttempt::getUser, Collectors.counting()));
		double avgAttempts = attemptsPerUser.values().stream().mapToLong(Long::longValue).average().orElse(0);
		analytics.put("averageAttemptsPerUser", avgAttempts);
		
		// Average time spent
		double avgTimeSeconds = allAttempts.stream().filter(a -> a.getTimeSpentSeconds() != null)
		        .mapToLong(ExerciseAttempt::getTimeSpentSeconds).average().orElse(0);
		analytics.put("averageTimeSpentMinutes", avgTimeSeconds / 60.0);
		
		return analytics;
	}
	
	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> getUserAnalytics(User user) {
		return buildUserAnalytics(user);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> getCurrentUserAnalytics() {
		User user = Context.getAuthenticatedUser();
		return buildUserAnalytics(user);
	}
	
	private Map<String, Object> buildUserAnalytics(User user) {
		Map<String, Object> analytics = new HashMap<>();
		
		List<CourseEnrollment> enrollments = enrollmentDAO.getCourseEnrollmentsByUser(user);
		
		analytics.put("totalCoursesEnrolled", enrollments.size());
		analytics.put("coursesCompleted", enrollments.stream().filter(e -> e.getCompletedAt() != null).count());
		analytics.put("coursesInProgress", enrollments.stream().filter(e -> e.getCompletedAt() == null).count());
		
		// Overall progress
		double avgProgress = enrollments.stream().mapToInt(CourseEnrollment::getProgressPercentage).average().orElse(0);
		analytics.put("overallProgress", avgProgress);
		
		// Course progress details
		List<Map<String, Object>> courseDetails = new ArrayList<>();
		for (CourseEnrollment enrollment : enrollments) {
			Map<String, Object> detail = new HashMap<>();
			detail.put("courseName", enrollment.getCourse().getName());
			detail.put("enrolledAt", enrollment.getEnrolledAt());
			detail.put("completedAt", enrollment.getCompletedAt());
			detail.put("lastAccessedAt", enrollment.getLastAccessedAt());
			detail.put("progressPercentage", enrollment.getProgressPercentage());
			courseDetails.add(detail);
		}
		analytics.put("courseDetails", courseDetails);
		
		return analytics;
	}
}
