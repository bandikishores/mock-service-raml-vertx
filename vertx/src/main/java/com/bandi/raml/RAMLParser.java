package com.bandi.raml;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.model.Response;
import org.raml.parser.visitor.RamlDocumentBuilder;

import com.bandi.cache.RAMLCache;
import com.bandi.data.ResponseData;
import com.bandi.log.Logger;
import com.bandi.util.Utils;
import com.bandi.validate.Validator;

public class RAMLParser {

	public void processRAML() {
		List<Path> pathToFiles = Utils.getRAMLFilesPath();

		if (CollectionUtils.isNotEmpty(pathToFiles)) {
			for (Path path : pathToFiles) {
				String ramlLocation = path.toUri().toString();

				if (Validator.isValidRAML(ramlLocation)) {
					Raml raml = new RamlDocumentBuilder().build(ramlLocation);

					if (raml != null) {
						parse(raml);
					} else {
						Logger.log(" Documentation not present for RAML to load example");
					}
				} else {
					Logger.log("Couldn't load raml at " + ramlLocation);
				}
			}
		} else {
			Logger.log("No RAMLs found");
		}
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

											RAMLCache.insertInToCache(resource.getUri(), responseData);
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

			RAMLCache.printValuesInCache();
		} else {
			Logger.log("No resources found in RAML file " + raml.getTitle());
		}
	}

}
