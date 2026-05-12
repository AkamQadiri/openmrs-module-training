/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.training.web.rest.controller;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.training.api.TrainingAnalyticsService;
import org.openmrs.module.training.api.TrainingCourseService;
import org.openmrs.module.training.model.Course;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + "/training/analytics")
public class TrainingAnalyticsController {
	
	@RequestMapping(value = "/course/{uuid}", method = RequestMethod.GET)
	@ResponseBody
	public SimpleObject getCourseAnalytics(@PathVariable("uuid") String courseUuid, HttpServletRequest request) {
		Course course = getCourseService().getCourseByUuid(courseUuid);
		
		if (course == null) {
			throw new ObjectNotFoundException(Context.getMessageSourceService().getMessage("training.course.notfound",
			    new Object[] { courseUuid }, Context.getLocale()));
		}
		
		Map<String, Object> analytics = getAnalyticsService().getCourseAnalytics(course);
		SimpleObject result = new SimpleObject();
		result.putAll(analytics);
		return result;
	}
	
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	@ResponseBody
	public SimpleObject getCurrentUserAnalytics(HttpServletRequest request) {
		Map<String, Object> analytics = getAnalyticsService().getCurrentUserAnalytics();
		SimpleObject result = new SimpleObject();
		result.putAll(analytics);
		return result;
	}
	
	@RequestMapping(value = "/user/{username}", method = RequestMethod.GET)
	@ResponseBody
	public SimpleObject getUserAnalytics(@PathVariable("username") String username, HttpServletRequest request) {
		User user = Context.getUserService().getUserByUsername(username);
		if (user == null) {
			throw new ObjectNotFoundException(Context.getMessageSourceService().getMessage("training.user.notfound",
			    new Object[] { username }, Context.getLocale()));
		}
		
		Map<String, Object> analytics = getAnalyticsService().getUserAnalytics(user);
		SimpleObject result = new SimpleObject();
		result.putAll(analytics);
		
		SimpleObject userObj = new SimpleObject();
		userObj.put("uuid", user.getUuid());
		userObj.put("display", user.getDisplayString());
		result.put("user", userObj);
		
		return result;
	}
	
	private TrainingAnalyticsService getAnalyticsService() {
		return Context.getService(TrainingAnalyticsService.class);
	}
	
	private TrainingCourseService getCourseService() {
		return Context.getService(TrainingCourseService.class);
	}
}
