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

import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.training.api.TrainingMediaService;
import org.openmrs.module.training.api.util.TrainingMediaConstants;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.response.IllegalRequestException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Controller
@RequestMapping("/rest/" + RestConstants.VERSION_1 + "/training/media")
public class TrainingMediaController extends BaseRestController {
	
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	@ResponseBody
	public SimpleObject upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
		if (!TrainingMediaConstants.ALLOWED_MIME_TYPES.contains(file.getContentType())) {
			throw new IllegalRequestException(Context.getMessageSourceService().getMessage("training.media.invalid.filetype",
			    new Object[] { file.getContentType() }, Context.getLocale()));
		}
		
		if (file.getSize() > TrainingMediaConstants.MAX_FILE_SIZE) {
			throw new IllegalRequestException(Context.getMessageSourceService().getMessage("training.media.file.toolarge",
			    new Object[] { TrainingMediaConstants.MAX_FILE_SIZE }, Context.getLocale()));
		}
		
		try {
			String uuid = getMediaService().uploadMedia(file.getBytes(), file.getOriginalFilename(), file.getContentType());
			
			SimpleObject result = new SimpleObject();
			result.put("uuid", uuid);
			result.put("filename", file.getOriginalFilename());
			result.put("size", file.getSize());
			result.put("contentType", file.getContentType());
			return result;
		}
		catch (IOException e) {
			throw new APIException(Context.getMessageSourceService().getMessage("training.media.upload.failed",
			    new Object[] { e.getMessage() }, Context.getLocale()));
		}
	}
	
	@RequestMapping(value = "/{uuid}", method = RequestMethod.GET)
	@ResponseBody
	public SimpleObject getMediaInfo(@PathVariable("uuid") String uuid, HttpServletRequest request) {
		Map<String, Object> mediaInfo = getMediaService().getMediaInfo(uuid);
		SimpleObject result = new SimpleObject();
		result.putAll(mediaInfo);
		return result;
	}
	
	@RequestMapping(value = "/{uuid}", method = RequestMethod.DELETE)
	@ResponseBody
	public void delete(@PathVariable("uuid") String uuid, HttpServletRequest request) {
		getMediaService().deleteMedia(uuid);
	}
	
	private TrainingMediaService getMediaService() {
		return Context.getService(TrainingMediaService.class);
	}
}
