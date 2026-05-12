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

import org.openmrs.Obs;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.training.api.TrainingMediaService;
import org.openmrs.module.training.api.util.TrainingMediaConstants;
import org.openmrs.obs.ComplexData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service("trainingMediaService")
public class TrainingMediaServiceImpl extends BaseOpenmrsService implements TrainingMediaService {
	
	@Override
	public String uploadMedia(byte[] content, String filename, String mimeType) {
		// Create a complex obs to store the media
		Obs obs = new Obs();
		obs.setConcept(Context.getConceptService().getConceptByUuid(TrainingMediaConstants.CONCEPT_UUID));
		obs.setPerson(Context.getAuthenticatedUser().getPerson());
		obs.setObsDatetime(new Date());
		
		// Create complex data
		ComplexData complexData = new ComplexData(filename, content);
		obs.setComplexData(complexData);
		
		obs = Context.getObsService().saveObs(obs, null);
		
		return obs.getUuid();
	}
	
	@Override
	public void deleteMedia(String complexObsUuid) {
		Obs obs = Context.getObsService().getObsByUuid(complexObsUuid);
		
		if (obs == null) {
			throw new APIException(Context.getMessageSourceService().getMessage("training.media.notfound",
			    new Object[] { complexObsUuid }, Context.getLocale()));
		}
		
		if (!obs.isComplex()) {
			throw new APIException(Context.getMessageSourceService().getMessage("training.media.not.complex"));
		}
		
		if (!obs.getConcept().getUuid().equals(TrainingMediaConstants.CONCEPT_UUID)) {
			throw new APIException(Context.getMessageSourceService().getMessage("training.media.not.training"));
		}
		
		String voidReason = "Deleted via Training Module";
		Context.getObsService().voidObs(obs, voidReason);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> getMediaInfo(String complexObsUuid) {
		Map<String, Object> info = new HashMap<>();
		
		Obs obs = Context.getObsService().getObsByUuid(complexObsUuid);
		if (obs != null && obs.isComplex()) {
			info.put("uuid", obs.getUuid());
			info.put("filename", obs.getValueText());
			info.put("uploadDate", obs.getObsDatetime());
			info.put("uploadedBy", obs.getCreator().getUsername());
		}
		
		return info;
	}
}
