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

import org.openmrs.Location;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.LocationService;
import org.openmrs.api.PersonService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.training.api.util.TrainingPrivilegeConstants;
import org.openmrs.module.training.api.util.TrainingRoleConstants;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.response.IllegalRequestException;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + "/training/user")
public class TrainingUserController {
	
	@RequestMapping(value = "/bulk-create-students", method = RequestMethod.POST)
	@ResponseBody
	public SimpleObject bulkCreateStudents(@RequestBody SimpleObject postBody, HttpServletRequest request) {
		if (!Context.hasPrivilege(TrainingPrivilegeConstants.MANAGE)) {
			throw new IllegalRequestException(Context.getMessageSourceService()
			        .getMessage("training.privilege.manage.required", null, Context.getLocale()));
		}
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> studentsData = (List<Map<String, Object>>) postBody.get("students");
		String defaultPassword = (String) postBody.get("defaultPassword");
		
		if (studentsData == null || studentsData.isEmpty()) {
			throw new IllegalRequestException(Context.getMessageSourceService().getMessage("training.students.list.required",
			    null, Context.getLocale()));
		}
		
		UserService userService = Context.getUserService();
		PersonService personService = Context.getPersonService();
		
		Role studentRole = userService.getRole(TrainingRoleConstants.STUDENT);
		if (studentRole == null) {
			throw new ObjectNotFoundException(Context.getMessageSourceService().getMessage("training.role.student.notfound",
			    new Object[] { TrainingRoleConstants.STUDENT }, Context.getLocale()));
		}
		
		List<SimpleObject> results = new ArrayList<>();
		
		for (Map<String, Object> studentData : studentsData) {
			SimpleObject result = new SimpleObject();
			String username = (String) studentData.get("username");
			result.put("username", username);
			
			User existingUser = userService.getUserByUsername(username);
			if (existingUser != null) {
				result.put("success", false);
				result.put("error", Context.getMessageSourceService().getMessage("training.user.username.exists",
				    new Object[] { username }, Context.getLocale()));
				results.add(result);
				continue;
			}
			
			Person person = new Person();
			PersonName name = new PersonName();
			name.setGivenName((String) studentData.get("givenName"));
			String familyName = (String) studentData.get("familyName");
			if (familyName != null && !familyName.trim().isEmpty()) {
				name.setFamilyName(familyName);
			}
			person.addName(name);
			
			String gender = (String) studentData.get("gender");
			person.setGender(gender.toUpperCase());
			
			person = personService.savePerson(person);
			
			User user = new User();
			user.setPerson(person);
			user.setUsername(username);
			user.addRole(studentRole);
			
			String password = (String) studentData.get("password");
			if (password == null || password.trim().isEmpty()) {
				if (defaultPassword == null || defaultPassword.trim().isEmpty()) {
					result.put("success", false);
					result.put("error",
					    Context.getMessageSourceService().getMessage("training.user.nopassword", null, Context.getLocale()));
					results.add(result);
					continue;
				}
				
				password = defaultPassword;
			}
			
			userService.createUser(user, password);
			
			result.put("success", true);
			result.put("uuid", user.getUuid());
			
			String defaultLocationName = (String) postBody.get("defaultLocation");
			if (defaultLocationName == null || defaultLocationName.trim().isEmpty()) {
				results.add(result);
				continue;
			}
			
			LocationService locationService = Context.getLocationService();
			Location defaultLocation = locationService.getLocation(defaultLocationName);
			if (defaultLocation == null) {
				result.put("warning", Context.getMessageSourceService().getMessage("training.user.location.notfound",
				    new Object[] { defaultLocationName }, Context.getLocale()));
				results.add(result);
				continue;
			}
			
			user.setUserProperty("defaultLocation", defaultLocation.getUuid());
			userService.saveUser(user);
			
			results.add(result);
		}
		
		SimpleObject response = new SimpleObject();
		response.put("results", results);
		
		return response;
	}
}
