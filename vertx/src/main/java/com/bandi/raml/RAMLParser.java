package com.bandi.raml;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.collections.MapUtils;
import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.model.Response;

import com.bandi.data.ResponseData;
import com.bandi.log.Logger;

public class RAMLParser {

	private HashMap<String, ResponseData> cacheofRAML;

	public RAMLParser(HashMap<String, ResponseData> cacheofRAML) {
		this.cacheofRAML = cacheofRAML;
	}

	public void parse(Raml raml) {
		Map<String, Resource> resources = raml.getResources();
		if (MapUtils.isNotEmpty(resources)) {
			for (Resource resource : resources.values()) {
				if (MapUtils.isNotEmpty(resource.getActions())) {
					for (ActionType actionType : resource.getActions().keySet()) {
						if (ActionType.GET.equals(actionType)) {
							Action action = resource.getAction(actionType);
							Map<String, Response> responses = action.getResponses();
							if (MapUtils.isNotEmpty(responses)) {
								for (Response response : responses.values()) {
									if (MapUtils.isNotEmpty(response.getBody())) {
										// response.getBody().get(MediaType.APPLICATION_JSON).getExample();
										for (String contentType : response.getBody().keySet()) {
											ResponseData responseData = new ResponseData();
											responseData.setResponseContentType(contentType);
											responseData.setMimeType(response.getBody().get(contentType));

											cacheofRAML.put(resource.getUri(), responseData);
											break;
										}

									} else
										Logger.log("Media type body found was not belonging to "
												+ MediaType.APPLICATION_JSON);
								}
							}
						}
					}
				}
			}

			Logger.log(cacheofRAML.toString());
		} else {
			Logger.log("No resources found in RAML file " + raml.getTitle());
		}
	}

}
