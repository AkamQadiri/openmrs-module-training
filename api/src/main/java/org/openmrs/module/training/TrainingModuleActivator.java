/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.training;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptComplex;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptName;
import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.ConceptService;
import org.openmrs.api.UserService;
import org.openmrs.api.context.Context;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.module.training.api.util.TrainingMediaConstants;
import org.openmrs.module.training.api.util.TrainingPrivilegeConstants;
import org.openmrs.module.training.api.util.TrainingRoleConstants;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class TrainingModuleActivator extends BaseModuleActivator {
	
	private static final Log log = LogFactory.getLog(TrainingModuleActivator.class);
	
	public void started() {
		log.info("Started Training Module");
		ensureTrainingRolesExist();
		ensureTrainingMediaConceptExists();
	}
	
	public void shutdown() {
		log.info("Shutdown Training Module");
	}
	
	private void ensureTrainingRolesExist() {
		UserService userService = Context.getUserService();
		
		Role studentRole = userService.getRole(TrainingRoleConstants.STUDENT);
		if (studentRole == null) {
			studentRole = new Role();
			studentRole.setRole(TrainingRoleConstants.STUDENT);
			studentRole.setDescription("Can view and take training courses");
			
			// Add core OpenMRS privileges for students
			String[] studentPrivileges = { TrainingPrivilegeConstants.PARTICIPATE, "App: coreapps.systemAdministration",
			        "App: referenceapplication.legacyAdmin", "View Administration Functions",
			        "App: adminui.configuremetadata", "View Navigation Menu", "App: coreapps.findPatient",
			        "App: coreapps.patientDashboard", "Get Users", "Get Visits", "Add Patients", "Delete Patients",
			        "Edit Patients", "Get Patients", "View Patients", "Add Observations", "Delete Observations",
			        "Edit Observations", "Get Observations", "View Observations", "Add Encounters", "Delete Encounters",
			        "Edit Encounters", "Get Encounter Roles", "Get Encounters", "Get Encounter Types",
			        "Manage Encounter Roles", "Manage Encounter Types", "View Encounter Types", "View Encounters",
			        "Get Concepts", "Manage Concepts", "Manage Concept Attribute Types", "Manage Concept Classes",
			        "Manage Concept Datatypes", "Manage Concept Map Types", "Manage Concept Name tags",
			        "Manage Concept Reference Terms", "Manage Concept Sources", "Manage Concept Stop Words", "View Concepts",
			        "View Concept Classes", "View Concept Datatypes", "View Concept Sources", "Get Concept Attribute Types",
			        "Get Concept Classes", "Get Concept Datatypes", "Get Concept Map Types", "Get Concept Reference Terms",
			        "Get Concept Sources", "App: formentryapp.forms", "Get Forms", "Edit Forms", "Manage Forms",
			        "View Forms", "View Unpublished Forms", "Preview Forms", "Get Field Types", "Manage Field Types",
			        "View Field Types" };
			
			for (String privilegeName : studentPrivileges) {
				addPrivilegeToRole(studentRole, privilegeName, userService);
			}
			
			userService.saveRole(studentRole);
			log.info("Created Training Student role with privileges");
		}
		
		Role analystRole = userService.getRole(TrainingRoleConstants.ANALYST);
		if (analystRole == null) {
			analystRole = new Role();
			analystRole.setRole(TrainingRoleConstants.ANALYST);
			analystRole.setDescription("Can view training analytics");
			
			String[] analystPrivileges = { TrainingPrivilegeConstants.VIEW_ANALYTICS, "Get Users" };
			
			for (String privilegeName : analystPrivileges) {
				addPrivilegeToRole(analystRole, privilegeName, userService);
			}
			
			userService.saveRole(analystRole);
			log.info("Created Training Analyst role with privileges");
		}
		
		Role instructorRole = userService.getRole(TrainingRoleConstants.INSTRUCTOR);
		if (instructorRole == null) {
			instructorRole = new Role();
			instructorRole.setRole(TrainingRoleConstants.INSTRUCTOR);
			instructorRole.setDescription("Can manage training content and view analytics");
			
			Set<Role> inheritedRoles = new HashSet<>();
			if (studentRole != null) {
				inheritedRoles.add(studentRole);
			}
			
			if (analystRole != null) {
				inheritedRoles.add(analystRole);
			}
			
			if (inheritedRoles.size() > 0) {
				instructorRole.setInheritedRoles(inheritedRoles);
			}
			
			// Add additional core OpenMRS privileges for instructors
			String[] instructorPrivileges = { TrainingPrivilegeConstants.MANAGE, "Add Users", "Delete Users", "Edit Users",
			        "View Users", "Edit User Passwords", "Provider Management API", "Provider Management - Admin",
			        "Manage Providers", "Get Providers", "Add People", "Delete People", "Edit People", "Get People",
			        "View People", };
			
			for (String privilegeName : instructorPrivileges) {
				addPrivilegeToRole(instructorRole, privilegeName, userService);
			}
			
			userService.saveRole(instructorRole);
			log.info("Created Training Instructor role with privileges and inheritance from Student and Analyst role");
		}
	}
	
	private void addPrivilegeToRole(Role role, String privilegeName, UserService userService) {
		try {
			Privilege privilege = userService.getPrivilege(privilegeName);
			
			// Adds privilege if it dosen't exist - Only reason for doing this currently is
			// because the privilege Edit Forms is not in the openmrs ref application
			if (privilege == null) {
				privilege = new Privilege();
				privilege.setPrivilege(privilegeName);
				userService.savePrivilege(privilege);
			}
			
			role.addPrivilege(privilege);
		}
		catch (Exception e) {
			log.error("Error adding privilege '" + privilegeName + "' to role '" + role.getRole() + "'", e);
		}
	}
	
	private void ensureTrainingMediaConceptExists() {
		try {
			ConceptService conceptService = Context.getConceptService();
			
			// Check if concept already exists
			Concept existingConcept = conceptService.getConceptByUuid(TrainingMediaConstants.CONCEPT_UUID);
			if (existingConcept != null) {
				log.info("Training media concept already exists");
				return;
			}
			
			// Get complex datatype
			ConceptDatatype complexDatatype = conceptService.getConceptDatatypeByName("Complex");
			if (complexDatatype == null) {
				log.error("Complex datatype not found. Cannot create training media concept.");
				return;
			}
			
			// Get misc class
			ConceptClass miscClass = conceptService.getConceptClassByName("Misc");
			if (miscClass == null) {
				log.error("Misc concept class not found. Cannot create training media concept.");
				return;
			}
			
			// Create the concept
			ConceptComplex concept = new ConceptComplex();
			concept.setUuid(TrainingMediaConstants.CONCEPT_UUID);
			concept.setDatatype(complexDatatype);
			concept.setConceptClass(miscClass);
			concept.setSet(false);
			concept.setRetired(false);
			
			// Add concept name
			ConceptName conceptName = new ConceptName();
			conceptName.setName("Training Media");
			conceptName.setLocale(Locale.ENGLISH);
			conceptName.setLocalePreferred(true);
			conceptName.setConceptNameType(ConceptNameType.FULLY_SPECIFIED);
			concept.addName(conceptName);
			
			// Add description
			concept.addDescription(new ConceptDescription("Complex obs for training module media files", Locale.ENGLISH));
			
			concept.setHandler("ImageHandler");
			
			// Save the concept
			conceptService.saveConcept(concept);
			
			log.info("Created training media concept successfully");
		}
		catch (Exception e) {
			log.error("Failed to create training media concept", e);
			// Don't throw exception to allow module to start even if concept creation fails
		}
	}
}
