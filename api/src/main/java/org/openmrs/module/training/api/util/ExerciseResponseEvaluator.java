/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.training.api.util;

import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptNumeric;
import org.openmrs.Form;
import org.openmrs.FormResource;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.ClobDatatypeStorage;
import org.openmrs.module.training.model.EvaluationResult;
import org.openmrs.module.training.model.Exercise;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ExerciseResponseEvaluator {
	
	public static EvaluationResult evaluateResponse(Exercise exercise, String response) {
		EvaluationResult result = new EvaluationResult(true);
		
		try {
			JsonNode responseNode = TrainingJsonUtil.parseJsonNode(response);
			JsonNode contentNode = TrainingJsonUtil.parseJsonNode(exercise.getContent());
			JsonNode validationNode = TrainingJsonUtil.parseJsonNode(exercise.getValidation());
			
			if (responseNode == null) {
				result.setCorrect(false);
				return result;
			}
			
			switch (exercise.getExerciseType()) {
				case MULTIPLE_CHOICE:
					evaluateMultipleChoice(result, responseNode, validationNode);
					break;
				case TRUE_FALSE:
					evaluateTrueFalse(result, responseNode, validationNode);
					break;
				case FILL_IN_BLANK:
					evaluateFillInBlank(result, responseNode, validationNode, contentNode);
					break;
				case MATCHING:
					evaluateMatching(result, responseNode, validationNode);
					break;
				case ORDERING:
					evaluateOrdering(result, responseNode, validationNode);
					break;
				case CONCEPT_CREATION:
					evaluateConceptCreation(result, responseNode, contentNode);
					break;
				case FORM_CREATION:
					evaluateFormCreation(result, responseNode, contentNode);
					break;
				default:
					throw new APIException("Unknown exercise type: " + exercise.getExerciseType());
			}
		}
		catch (Exception e) {
			result.addError("training.exercise.error.evaluation", e.getMessage());
		}
		
		return result;
	}
	
	// ========== Quiz Exercise Evaluators ==========
	
	private static void evaluateMultipleChoice(EvaluationResult result, JsonNode response, JsonNode validation) {
		String selectedOption = response.path("selectedOption").asText();
		String correctAnswer = validation.path("correctAnswer").asText();
		result.setCorrect(selectedOption.equals(correctAnswer));
	}
	
	private static void evaluateTrueFalse(EvaluationResult result, JsonNode response, JsonNode validation) {
		boolean answer = response.path("answer").asBoolean();
		boolean correctAnswer = validation.path("correctAnswer").asBoolean();
		result.setCorrect(answer == correctAnswer);
	}
	
	private static void evaluateFillInBlank(EvaluationResult result, JsonNode response, JsonNode validation,
	        JsonNode content) {
		JsonNode correctAnswers = validation.path("correctAnswers");
		if (!correctAnswers.isObject()) {
			result.setCorrect(false);
			return;
		}
		
		String userLocale = Context.getLocale().getLanguage();
		JsonNode options = content.path("options");
		
		Iterator<Map.Entry<String, JsonNode>> blankEntries = correctAnswers.fields();
		while (blankEntries.hasNext()) {
			Map.Entry<String, JsonNode> entry = blankEntries.next();
			String blankId = entry.getKey();
			String correctAnswer = entry.getValue().asText().trim();
			String userAnswer = normalizeAnswer(response.path(blankId).asText("").trim(), userLocale, options);
			
			if (!userAnswer.equalsIgnoreCase(correctAnswer)) {
				result.setCorrect(false);
				return;
			}
		}
	}
	
	private static String normalizeAnswer(String userAnswer, String userLocale, JsonNode options) {
		if (userLocale.equals("en") || !options.isArray()) {
			return userAnswer;
		}
		
		for (JsonNode option : options) {
			String localizedText = option.path(userLocale).asText("").trim();
			if (localizedText.equalsIgnoreCase(userAnswer)) {
				return option.path("en").asText("").trim();
			}
		}
		
		return userAnswer;
	}
	
	private static void evaluateMatching(EvaluationResult result, JsonNode response, JsonNode validation) {
		JsonNode correctMatches = validation.path("correctMatches");
		if (!correctMatches.isObject()) {
			result.setCorrect(false);
			return;
		}
		
		Iterator<Map.Entry<String, JsonNode>> matches = correctMatches.fields();
		while (matches.hasNext()) {
			Map.Entry<String, JsonNode> match = matches.next();
			String itemId = match.getKey();
			String correctMatch = match.getValue().asText();
			String userMatch = response.path(itemId).asText("");
			
			if (!userMatch.equals(correctMatch)) {
				result.setCorrect(false);
				return;
			}
		}
	}
	
	private static void evaluateOrdering(EvaluationResult result, JsonNode response, JsonNode validation) {
		JsonNode userOrder = response.path("order");
		JsonNode correctOrder = validation.path("correctOrder");
		
		if (!userOrder.isArray() || !correctOrder.isArray()) {
			result.setCorrect(false);
			return;
		}
		
		if (userOrder.size() != correctOrder.size()) {
			result.setCorrect(false);
			return;
		}
		
		for (int i = 0; i < correctOrder.size(); i++) {
			if (!userOrder.get(i).asText().equals(correctOrder.get(i).asText())) {
				result.setCorrect(false);
				return;
			}
		}
	}
	
	// ========== Concept Creation Evaluator ==========
	
	private static void evaluateConceptCreation(EvaluationResult result, JsonNode response, JsonNode content) {
		String conceptUuid = response.path("conceptUuid").asText();
		
		if (conceptUuid.isEmpty()) {
			result.addError("training.concept.error.noUuid");
			return;
		}
		
		Concept concept = Context.getConceptService().getConceptByUuid(conceptUuid);
		if (concept == null) {
			result.addError("training.concept.error.notFound");
			return;
		}
		
		Integer currentUserId = Context.getAuthenticatedUser().getUserId();
		Integer conceptCreatorId = concept.getCreator().getUserId();
		if (!currentUserId.equals(conceptCreatorId)) {
			result.addError("training.concept.error.notCreatedByUser");
			return;
		}
		
		JsonNode requirements = content.path("requirements");
		String userLocale = Context.getLocale().getLanguage();
		
		validateConceptBasicProperties(result, concept, requirements);
		validateConceptNames(result, concept, requirements, userLocale);
		validateConceptDescription(result, concept, requirements, userLocale);
		validateConceptSetMembers(result, concept, requirements, userLocale);
		validateConceptNumericProperties(result, concept, requirements);
		validateConceptCodedProperties(result, concept, requirements, userLocale);
		validateConceptMappings(result, concept, requirements);
	}
	
	private static void validateConceptBasicProperties(EvaluationResult result, Concept concept, JsonNode requirements) {
		if (requirements.has("class")) {
			String required = requirements.get("class").asText();
			String actual = concept.getConceptClass().getName();
			if (!required.equals(actual)) {
				result.addError("training.concept.error.classMismatch", actual, required);
			}
		}
		
		if (requirements.has("datatype")) {
			String required = requirements.get("datatype").asText();
			String actual = concept.getDatatype().getName();
			if (!required.equals(actual)) {
				result.addError("training.concept.error.datatypeMismatch", actual, required);
			}
		}
		
		if (requirements.has("isSet")) {
			Boolean required = requirements.get("isSet").asBoolean();
			Boolean actual = concept.getSet();
			if (!required.equals(actual)) {
				result.addError("training.concept.error.isSetMismatch", actual, required);
			}
		} else if (concept.getSet() != null && concept.getSet()) {
			result.addError("training.concept.error.isSetShouldBeEmpty");
		}
		
		if (requirements.has("version")) {
			String required = requirements.get("version").asText();
			String actual = concept.getVersion();
			if (!required.isEmpty() && actual != null && !required.equals(actual)) {
				result.addError("training.concept.error.versionMismatch", actual, required);
			}
		} else if (concept.getVersion() != null && !concept.getVersion().isEmpty()) {
			result.addError("training.concept.error.versionShouldBeEmpty", concept.getVersion());
		}
	}
	
	private static void validateConceptNames(EvaluationResult result, Concept concept, JsonNode requirements,
	        String userLocale) {
		if (!requirements.has("name")) {
			long nonMandatoryNames = concept.getNames().stream().filter(cn -> !cn.getVoided()).filter(
			    cn -> cn.getConceptNameType() == null || !cn.getConceptNameType().equals(ConceptNameType.FULLY_SPECIFIED))
			        .count();
			
			if (nonMandatoryNames > 0) {
				result.addError("training.concept.error.unexpectedNamesFound");
			}
			return;
		}
		
		JsonNode nameNode = requirements.get("name");
		
		if (nameNode.has("primary")) {
			JsonNode primaryNode = nameNode.get("primary");
			String primaryName = normalizeLocalizedText(primaryNode, userLocale);
			
			if (!primaryName.isEmpty()) {
				String primaryNameWithUsername = primaryName + " - " + concept.getCreator().getUsername();
				boolean found = concept.getNames().stream()
				        .anyMatch(cn -> cn.getName().equals(primaryNameWithUsername) && cn.getConceptNameType() != null
				                && cn.getConceptNameType().equals(ConceptNameType.FULLY_SPECIFIED));
				
				if (!found) {
					result.addError("training.concept.error.primaryNameNotFound", primaryNameWithUsername);
				}
			}
		}
		
		if (nameNode.has("synonyms")) {
			validateConceptNameList(result, concept, nameNode.get("synonyms"), "training.concept.error.synonymNotFound",
			    userLocale);
		} else {
			long synonymCount = concept.getNames().stream().filter(cn -> !cn.getVoided()).filter(
			    cn -> cn.getConceptNameType() == null || (!cn.getConceptNameType().equals(ConceptNameType.FULLY_SPECIFIED)
			            && !cn.getConceptNameType().equals(ConceptNameType.SHORT)
			            && !cn.getConceptNameType().equals(ConceptNameType.INDEX_TERM)))
			        .count();
			
			if (synonymCount > 0) {
				result.addError("training.concept.error.unexpectedSynonymsFound");
			}
		}
		
		if (nameNode.has("searchTerms")) {
			validateConceptNameList(result, concept, nameNode.get("searchTerms"),
			    "training.concept.error.searchTermNotFound", userLocale);
		} else {
			long searchTermCount = concept.getNames().stream().filter(cn -> !cn.getVoided())
			        .filter(
			            cn -> cn.getConceptNameType() != null && cn.getConceptNameType().equals(ConceptNameType.INDEX_TERM))
			        .count();
			
			if (searchTermCount > 0) {
				result.addError("training.concept.error.unexpectedSearchTermsFound");
			}
		}
		
		if (nameNode.has("shortName")) {
			JsonNode shortNameNode = nameNode.get("shortName");
			String shortName = normalizeLocalizedText(shortNameNode, userLocale);
			
			if (!shortName.isEmpty()) {
				boolean found = concept.getShortNames().stream().anyMatch(cn -> cn.getName().equals(shortName));
				
				if (!found) {
					result.addError("training.concept.error.shortNameNotFound", shortName);
				}
			}
		} else if (!concept.getShortNames().isEmpty()) {
			result.addError("training.concept.error.unexpectedShortNameFound");
		}
	}
	
	private static void validateConceptNameList(EvaluationResult result, Concept concept, JsonNode nameListNode,
	        String errorKey, String userLocale) {
		if (!nameListNode.isArray()) {
			return;
		}
		
		for (JsonNode nameNode : nameListNode) {
			String name = normalizeLocalizedText(nameNode, userLocale);
			if (!name.isEmpty()) {
				boolean found = concept.getNames().stream().anyMatch(cn -> cn.getName().equals(name));
				
				if (!found) {
					result.addError(errorKey, name);
				}
			}
		}
	}
	
	private static void validateConceptDescription(EvaluationResult result, Concept concept, JsonNode requirements,
	        String userLocale) {
		if (!requirements.has("description")) {
			if (!concept.getDescriptions().isEmpty()) {
				result.addError("training.concept.error.unexpectedDescriptionFound");
			}
			
			return;
		}
		
		JsonNode descriptionNode = requirements.get("description");
		String description = normalizeLocalizedText(descriptionNode, userLocale);
		
		if (description.isEmpty()) {
			return;
		}
		
		boolean found = concept.getDescriptions().stream().anyMatch(cd -> cd.getDescription().equals(description));
		
		if (!found) {
			result.addError("training.concept.error.descriptionMismatch");
		}
	}
	
	private static void validateConceptSetMembers(EvaluationResult result, Concept concept, JsonNode requirements,
	        String userLocale) {
		if (!requirements.has("setMembers")) {
			if (concept.getSet() && !concept.getSetMembers().isEmpty()) {
				result.addError("training.concept.error.unexpectedSetMembersFound");
			}
			
			return;
		}
		
		JsonNode requiredSetMembers = requirements.get("setMembers");
		
		if (!concept.getSet()) {
			result.addError("training.concept.error.notASet");
			return;
		}
		
		Set<String> memberNames = new HashSet<>();
		for (Concept member : concept.getSetMembers()) {
			memberNames.add(member.getName().getName().toLowerCase());
		}
		
		for (JsonNode memberNode : requiredSetMembers) {
			String conceptName = normalizeLocalizedText(memberNode.path("conceptName"), userLocale);
			if (conceptName.isEmpty()) {
				result.addError("training.concept.error.missingConceptNameInSetMember", conceptName);
				continue;
			}
			
			if (!memberNames.contains(conceptName.toLowerCase())) {
				result.addError("training.concept.error.setMemberNotFound", conceptName);
			}
		}
	}
	
	private static void validateConceptNumericProperties(EvaluationResult result, Concept concept, JsonNode requirements) {
		if (!requirements.has("numericProperties")) {
			ConceptNumeric numericConcept = Context.getConceptService().getConceptNumeric(concept.getId());
			if (numericConcept != null) {
				boolean hasAnyNumericProperty = numericConcept.getHiAbsolute() != null
				        || numericConcept.getHiCritical() != null || numericConcept.getHiNormal() != null
				        || numericConcept.getLowAbsolute() != null || numericConcept.getLowCritical() != null
				        || numericConcept.getLowNormal() != null
				        || (numericConcept.getUnits() != null && !numericConcept.getUnits().isEmpty())
				        || numericConcept.getAllowDecimal() != null || numericConcept.getDisplayPrecision() != null;
				
				if (hasAnyNumericProperty) {
					result.addError("training.concept.error.unexpectedNumericPropertiesFound");
				}
			}
			return;
		}
		
		JsonNode numericCriteria = requirements.get("numericProperties");
		ConceptNumeric numericConcept = Context.getConceptService().getConceptNumeric(concept.getId());
		
		if (numericConcept == null) {
			result.addError("training.concept.error.notNumeric");
			return;
		}
		
		validateNumericProperty(result, numericCriteria, "hiAbsolute", numericConcept.getHiAbsolute());
		validateNumericProperty(result, numericCriteria, "hiCritical", numericConcept.getHiCritical());
		validateNumericProperty(result, numericCriteria, "hiNormal", numericConcept.getHiNormal());
		validateNumericProperty(result, numericCriteria, "lowAbsolute", numericConcept.getLowAbsolute());
		validateNumericProperty(result, numericCriteria, "lowCritical", numericConcept.getLowCritical());
		validateNumericProperty(result, numericCriteria, "lowNormal", numericConcept.getLowNormal());
		
		if (numericCriteria.has("units")) {
			String required = numericCriteria.get("units").asText();
			String actual = numericConcept.getUnits();
			if (!required.equals(actual)) {
				result.addError("training.concept.error.unitsMismatch", actual, required);
			}
		} else if (numericConcept.getUnits() != null && !numericConcept.getUnits().isEmpty()) {
			result.addError("training.concept.error.unitsShouldBeEmpty", numericConcept.getUnits());
		}
		
		if (numericCriteria.has("allowDecimal")) {
			Boolean required = numericCriteria.get("allowDecimal").asBoolean();
			Boolean actual = numericConcept.getAllowDecimal();
			if (!required.equals(actual)) {
				result.addError("training.concept.error.allowDecimalMismatch", actual, required);
			}
		} else if (numericConcept.getAllowDecimal() != null) {
			result.addError("training.concept.error.allowDecimalShouldBeEmpty");
		}
		
		if (numericCriteria.has("displayPrecision")) {
			Integer required = numericCriteria.get("displayPrecision").asInt();
			Integer actual = numericConcept.getDisplayPrecision();
			if (!required.equals(actual)) {
				result.addError("training.concept.error.displayPrecisionMismatch", actual, required);
			}
		} else if (numericConcept.getDisplayPrecision() != null) {
			result.addError("training.concept.error.displayPrecisionShouldBeEmpty");
		}
	}
	
	private static void validateNumericProperty(EvaluationResult result, JsonNode criteria, String propertyName,
	        Double actualValue) {
		if (criteria.has(propertyName)) {
			Double required = criteria.get(propertyName).asDouble();
			if (!required.equals(actualValue)) {
				result.addError("training.concept.error." + propertyName + "Mismatch", actualValue, required);
			}
		} else if (actualValue != null) {
			result.addError("training.concept.error." + propertyName + "ShouldBeEmpty", actualValue);
		}
	}
	
	private static void validateConceptCodedProperties(EvaluationResult result, Concept concept, JsonNode requirements,
	        String userLocale) {
		if (!requirements.has("codedProperties")) {
			if (!concept.getAnswers().isEmpty()) {
				result.addError("training.concept.error.unexpectedAnswersFound");
			}
			
			return;
		}
		
		JsonNode codedCriteria = requirements.get("codedProperties");
		
		Collection<ConceptAnswer> answers = concept.getAnswers();
		Set<String> answerNames = new HashSet<>();
		for (ConceptAnswer answer : answers) {
			answerNames.add(answer.getAnswerConcept().getName().getName().toLowerCase());
		}
		
		for (JsonNode answerNode : codedCriteria) {
			String conceptName = answerNode.path("conceptName").isTextual() ? answerNode.path("conceptName").asText()
			        : normalizeLocalizedText(answerNode.get("conceptName"), userLocale);
			
			if (!answerNames.contains(conceptName.toLowerCase())) {
				result.addError("training.concept.error.codedAnswerNotFound", conceptName);
			}
		}
	}
	
	private static void validateConceptMappings(EvaluationResult result, Concept concept, JsonNode requirements) {
		if (!requirements.has("mappings")) {
			if (!concept.getConceptMappings().isEmpty()) {
				result.addError("training.concept.error.unexpectedMappingsFound");
			}
			
			return;
		}
		
		JsonNode requiredMappings = requirements.get("mappings");
		
		for (JsonNode mappingNode : requiredMappings) {
			String relationship = mappingNode.path("relationship").asText();
			String source = mappingNode.path("source").asText();
			String code = mappingNode.path("code").asText();
			
			boolean found = concept.getConceptMappings().stream().anyMatch(cm -> {
				boolean sourceMatches = cm.getConceptReferenceTerm().getConceptSource().getName().equals(source);
				boolean codeMatches = cm.getConceptReferenceTerm().getCode().equals(code);
				boolean relationshipMatches = relationship.isEmpty()
				        || cm.getConceptMapType().getName().equals(relationship);
				return sourceMatches && codeMatches && relationshipMatches;
			});
			
			if (!found) {
				result.addError("training.concept.error.mappingNotFound", relationship, source, code);
			}
		}
	}
	
	// ========== Form Creation Evaluator ==========
	
	private static void evaluateFormCreation(EvaluationResult result, JsonNode response, JsonNode content) {
		String formUuid = response.path("formUuid").asText();
		
		if (formUuid.isEmpty()) {
			result.addError("training.form.error.noUuid");
			return;
		}
		
		Form form = Context.getFormService().getFormByUuid(formUuid);
		if (form == null) {
			result.addError("training.form.error.notFound");
			return;
		}
		
		Integer currentUserId = Context.getAuthenticatedUser().getUserId();
		Integer formCreatorId = form.getCreator().getUserId();
		if (!currentUserId.equals(formCreatorId)) {
			result.addError("training.form.error.notCreatedByUser");
			return;
		}
		
		JsonNode requirements = content.path("requirements");
		String userLocale = Context.getLocale().getLanguage();
		
		validateFormBasicProperties(result, form, requirements, userLocale);
		validateFormSchemaComprehensive(result, form, requirements, userLocale);
	}
	
	private static void validateFormBasicProperties(EvaluationResult result, Form form, JsonNode requirements,
	        String userLocale) {
		if (requirements.has("name")) {
			String requiredName = normalizeLocalizedText(requirements.get("name"), userLocale) + " - "
			        + form.getCreator().getUsername();
			String actual = form.getName();
			if (!requiredName.equals(actual)) {
				result.addError("training.form.error.nameMismatch", actual, requiredName);
			}
		}
		
		if (requirements.has("description")) {
			String requiredDescription = normalizeLocalizedText(requirements.get("description"), userLocale);
			if (!form.getDescription().equals(requiredDescription)) {
				result.addError("training.form.error.descriptionMismatch");
			}
		} else if (form.getDescription() != null && !form.getDescription().isEmpty()) {
			result.addError("training.form.error.descriptionShouldBeEmpty", form.getDescription());
		}
		
		if (requirements.has("version")) {
			String required = requirements.get("version").asText();
			String actual = form.getVersion();
			if (!required.equals(actual)) {
				result.addError("training.form.error.versionMismatch", actual, required);
			}
		} else if (form.getVersion() != null && !form.getVersion().isEmpty()) {
			result.addError("training.form.error.versionShouldBeEmpty", form.getVersion());
		}
		
		if (requirements.has("encounterType")) {
			String required = requirements.get("encounterType").asText();
			String actual = form.getEncounterType() != null ? form.getEncounterType().getName() : null;
			if (!required.isEmpty() && !required.equals(actual)) {
				result.addError("training.form.error.encounterTypeMismatch", actual, required);
			}
		} else if (form.getEncounterType() != null) {
			result.addError("training.form.error.encounterTypeShouldBeEmpty", form.getEncounterType().getName());
		}
		
		if (requirements.has("published")) {
			Boolean required = requirements.get("published").asBoolean();
			Boolean actual = form.getPublished();
			if (!required.equals(actual)) {
				result.addError("training.form.error.publishedMismatch", actual, required);
			}
		} else if (form.getPublished() != null && form.getPublished()) {
			result.addError("training.form.error.publishedShouldBeEmpty");
		}
	}
	
	private static void validateFormSchemaComprehensive(EvaluationResult result, Form form, JsonNode requirements,
	        String userLocale) {
		FormResource formResource = Context.getFormService().getFormResource(form, "JSON schema");
		if (formResource == null) {
			if (!requirements.has("pages")) {
				return;
			}
			
			result.addError("training.form.error.noJsonSchema");
			return;
		}
		
		JsonNode formSchema = loadFormSchema(result, formResource);
		if (formSchema == null) {
			return;
		}
		
		JsonNode actualPages = formSchema.path("pages");
		if (!actualPages.isArray()) {
			result.addError("training.form.error.noPages");
			return;
		}
		
		if (requirements.has("pages")) {
			validatePages(result, actualPages, requirements.get("pages"), userLocale);
		} else if (actualPages.size() > 0) {
			result.addError("training.form.error.unexpectedPagesFound");
		}
	}
	
	private static JsonNode loadFormSchema(EvaluationResult result, FormResource formResource) {
		String valueReference = formResource.getValueReference();
		ClobDatatypeStorage clobStorage = Context.getDatatypeService().getClobDatatypeStorageByUuid(valueReference);
		String formSchemaJson = clobStorage.getValue();
		JsonNode formSchema = TrainingJsonUtil.parseJsonNode(formSchemaJson);
		if (formSchema == null) {
			result.addError("training.form.error.invalidJsonFormat");
		}
		
		return formSchema;
	}
	
	private static void validatePages(EvaluationResult result, JsonNode actualPages, JsonNode requiredPages,
	        String userLocale) {
		for (JsonNode requiredPage : requiredPages) {
			String pageLabel = normalizeLocalizedText(requiredPage.get("label"), userLocale);
			if (pageLabel.isEmpty()) {
				continue;
			}
			
			JsonNode actualPage = findNodeByLabel(actualPages, pageLabel);
			if (actualPage == null) {
				result.addError("training.form.error.pageNotFound", pageLabel);
				continue;
			}
			
			if (requiredPage.has("sections")) {
				validateSections(result, actualPage, requiredPage.get("sections"), pageLabel, userLocale);
			} else {
				JsonNode actualSections = actualPage.path("sections");
				if (actualSections.isArray() && actualSections.size() > 0) {
					result.addError("training.form.error.unexpectedSectionsFound", pageLabel);
				}
			}
		}
	}
	
	private static void validateSections(EvaluationResult result, JsonNode actualPage, JsonNode requiredSections,
	        String pageLabel, String userLocale) {
		if (!requiredSections.isArray()) {
			return;
		}
		
		JsonNode actualSections = actualPage.path("sections");
		if (!actualSections.isArray()) {
			result.addError("training.form.error.noSections", pageLabel);
			return;
		}
		
		for (JsonNode requiredSection : requiredSections) {
			String sectionLabel = normalizeLocalizedText(requiredSection.get("label"), userLocale);
			if (sectionLabel.isEmpty()) {
				continue;
			}
			
			JsonNode actualSection = findNodeByLabel(actualSections, sectionLabel);
			if (actualSection == null) {
				result.addError("training.form.error.sectionNotFound", sectionLabel);
				continue;
			}
			
			validateSectionProperties(result, actualSection, requiredSection);
			
			if (requiredSection.has("questions")) {
				validateQuestions(result, actualSection, requiredSection.get("questions"), sectionLabel, userLocale);
			} else {
				JsonNode actualQuestions = actualSection.path("questions");
				if (actualQuestions.isArray() && actualQuestions.size() > 0) {
					result.addError("training.form.error.unexpectedQuestionsFound", sectionLabel);
				}
			}
		}
	}
	
	private static void validateSectionProperties(EvaluationResult result, JsonNode actualSection,
	        JsonNode requiredSection) {
		if (requiredSection.has("isExpanded")) {
			String required = requiredSection.path("isExpanded").asText();
			String actual = actualSection.path("isExpanded").asText();
			if (!required.isEmpty() && !required.equals(actual)) {
				result.addError("training.form.error.isExpandedMismatch", actual, required);
			}
		} else {
			String actual = actualSection.path("isExpanded").asText();
			if (!actual.isEmpty() && !actual.equals("false")) {
				result.addError("training.form.error.isExpandedShouldBeEmpty", actual);
			}
		}
	}
	
	private static void validateQuestions(EvaluationResult result, JsonNode actualSection, JsonNode requiredQuestions,
	        String sectionLabel, String userLocale) {
		if (!requiredQuestions.isArray()) {
			return;
		}
		
		JsonNode actualQuestions = actualSection.path("questions");
		if (!actualQuestions.isArray()) {
			result.addError("training.form.error.noQuestions", sectionLabel);
			return;
		}
		
		for (JsonNode requiredQuestion : requiredQuestions) {
			String questionId = normalizeLocalizedText(requiredQuestion.get("id"), userLocale);
			if (questionId.isEmpty()) {
				continue;
			}
			
			JsonNode actualQuestion = findNodeById(actualQuestions, questionId);
			if (actualQuestion == null) {
				result.addError("training.form.error.questionNotFound", questionId);
				continue;
			}
			
			validateQuestionProperties(result, actualQuestion, requiredQuestion, questionId, userLocale);
		}
	}
	
	private static void validateQuestionProperties(EvaluationResult result, JsonNode actualQuestion,
	        JsonNode requiredQuestion, String questionId, String userLocale) {
		
		if (requiredQuestion.has("label")) {
			String required = normalizeLocalizedText(requiredQuestion.get("label"), userLocale);
			String actual = actualQuestion.path("label").asText();
			if (!required.isEmpty() && !required.equals(actual)) {
				result.addError("training.form.error.questionLabelMismatch", actual, required);
			}
		}
		
		if (requiredQuestion.has("required")) {
			boolean required = requiredQuestion.path("required").asBoolean();
			boolean actual = actualQuestion.path("required").asBoolean();
			if (required != actual) {
				result.addError("training.form.error.questionRequiredMismatch", actual ? 1 : 0, required ? 1 : 0);
			}
		} else {
			boolean actual = actualQuestion.path("required").asBoolean();
			if (actual) {
				result.addError("training.form.error.questionRequiredShouldBeEmpty");
			}
		}
		
		if (requiredQuestion.has("type")) {
			String required = requiredQuestion.path("type").asText();
			String actual = actualQuestion.path("type").asText();
			if (!required.isEmpty() && !required.equals(actual)) {
				result.addError("training.form.error.questionTypeMismatch", actual, required);
			}
		}
		
		if (requiredQuestion.has("questionOptions")) {
			validateQuestionOptions(result, actualQuestion, requiredQuestion.get("questionOptions"), questionId);
		} else {
			JsonNode actualOptions = actualQuestion.path("questionOptions");
			if (!actualOptions.isMissingNode()) {
				validateQuestionOptionsEmpty(result, actualOptions);
			}
		}
	}
	
	private static void validateQuestionOptions(EvaluationResult result, JsonNode actualQuestion, JsonNode requiredOptions,
	        String questionId) {
		
		JsonNode actualOptions = actualQuestion.path("questionOptions");
		
		if (requiredOptions.has("rendering")) {
			String required = requiredOptions.path("rendering").asText();
			String actual = actualOptions.path("rendering").asText();
			if (!required.isEmpty() && !required.equals(actual)) {
				result.addError("training.form.error.questionRenderingMismatch", actual, required);
			}
		} else {
			String actual = actualOptions.path("rendering").asText();
			if (!actual.isEmpty()) {
				result.addError("training.form.error.questionRenderingShouldBeEmpty", actual);
			}
		}
		
		if (requiredOptions.has("concept")) {
			JsonNode requiredConcept = requiredOptions.get("concept");
			
			if (requiredConcept.has("id")) {
				String requiredId = requiredConcept.path("id").asText();
				String actualId = actualOptions.path("concept").asText();
				if (!requiredId.isEmpty() && !requiredId.equals(actualId)) {
					String requiredName = "";
					String actualName = "";
					
					Concept reqConcept = Context.getConceptService().getConceptByUuid(requiredId);
					if (reqConcept != null) {
						requiredName = reqConcept.getName().getName();
					}
					
					Concept actConcept = Context.getConceptService().getConceptByUuid(actualId);
					if (actConcept != null) {
						actualName = actConcept.getName().getName();
					}
					
					result.addError("training.form.error.questionConceptMismatch", actualName, actualId, requiredName,
					    requiredId);
				}
			}
		} else {
			String actual = actualOptions.path("concept").asText();
			if (!actual.isEmpty()) {
				result.addError("training.form.error.questionConceptShouldBeEmpty", actual);
			}
		}
		
		if (requiredOptions.has("min")) {
			String required = requiredOptions.path("min").asText();
			String actual = actualOptions.path("min").asText();
			if (!required.isEmpty() && !required.equals(actual)) {
				result.addError("training.form.error.questionMinMismatch", actual, required);
			}
		} else {
			String actual = actualOptions.path("min").asText();
			if (!actual.isEmpty() && !actual.equals("0")) {
				result.addError("training.form.error.questionMinShouldBeEmpty", actual);
			}
		}
		
		if (requiredOptions.has("max")) {
			String required = requiredOptions.path("max").asText();
			String actual = actualOptions.path("max").asText();
			if (!required.isEmpty() && !required.equals(actual)) {
				result.addError("training.form.error.questionMaxMismatch", actual, required);
			}
		} else {
			String actual = actualOptions.path("max").asText();
			if (!actual.isEmpty() && !actual.equals("*")) {
				result.addError("training.form.error.questionMaxShouldBeEmpty", actual);
			}
		}
	}
	
	private static void validateQuestionOptionsEmpty(EvaluationResult result, JsonNode actualOptions) {
		String rendering = actualOptions.path("rendering").asText();
		String concept = actualOptions.path("concept").asText();
		String min = actualOptions.path("min").asText();
		String max = actualOptions.path("max").asText();
		
		if (!rendering.isEmpty() || !concept.isEmpty() || (!min.isEmpty() && !min.equals("0"))
		        || (!max.isEmpty() && !max.equals("*"))) {
			result.addError("training.form.error.unexpectedQuestionOptionsFound");
		}
	}
	
	// ========== Helper Methods ==========
	
	private static String normalizeLocalizedText(JsonNode localizedNode, String userLocale) {
		if (!localizedNode.isObject()) {
			return "";
		}
		
		// Try user's locale first
		String text = localizedNode.path(userLocale).asText("");
		
		// Fall back to English if user's locale doesn't exist
		if (text.isEmpty()) {
			text = localizedNode.path("en").asText("");
		}
		
		return text.trim();
	}
	
	private static JsonNode findNodeByLabel(JsonNode nodes, String label) {
		for (JsonNode node : nodes) {
			if (node.path("label").asText().equals(label)) {
				return node;
			}
		}
		return null;
	}
	
	private static JsonNode findNodeById(JsonNode nodes, String id) {
		for (JsonNode node : nodes) {
			if (node.path("id").asText().equals(id)) {
				return node;
			}
		}
		return null;
	}
}
