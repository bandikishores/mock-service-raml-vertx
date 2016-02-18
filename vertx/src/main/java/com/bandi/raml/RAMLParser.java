package com.bandi.raml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.MimeType;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.model.Response;
import org.raml.parser.visitor.RamlDocumentBuilder;

import com.bandi.cache.RAMLCache;
import com.bandi.data.ResponseData;
import com.bandi.log.Logger;
import com.bandi.util.Constants;
import com.bandi.util.Utils;
import com.bandi.validate.Validator;
import com.google.common.io.Files;

public class RAMLParser {

	public void processRAML() {
		List<Path> pathToFiles = Utils.getRAMLFilesPath();

		if (CollectionUtils.isNotEmpty(pathToFiles)) {
			for (Path path : pathToFiles) {

				if (!path.toString().endsWith(Constants.RAML_EXTENSION))
					continue;

				String ramlLocation = path.toUri().toString();

				if (Validator.isValidRAML(ramlLocation)) {
					Raml raml = new RamlDocumentBuilder().build(ramlLocation);

					if (raml != null) {
						parse(raml, path.getParent().toString());
					} else {
						Logger.log(" Documentation not present for RAML to load example");
					}
				} else {
					Logger.log("Couldn't load raml at " + ramlLocation + " as RAML is Invalid");
				}
			}
		} else {
			Logger.log("No RAMLs found");
		}
	}

	public void parse(Raml raml, String ramlLocation) {
		Map<String, Resource> resources = raml.getResources();
		if (MapUtils.isNotEmpty(resources)) {
			for (Resource resource : resources.values()) {
				parseResource(resource, ramlLocation);
			}

			RAMLCache.printValuesInCache();
		} else {
			Logger.log("No resources found in RAML file " + raml.getTitle());
		}
	}

	private void parseResource(Resource resource, String ramlLocation) {
		if (MapUtils.isNotEmpty(resource.getActions())) {
			for (ActionType actionType : resource.getActions().keySet()) {
				if (ActionType.GET.equals(actionType)) {
					Action action = resource.getAction(actionType);
					Map<String, Response> responses = action.getResponses();
					if (MapUtils.isNotEmpty(responses)) {
						for (Response response : responses.values()) {
							insertExampleToCache(resource.getUri(), ramlLocation, actionType, response.getBody());
						}
					}
				} else if (ActionType.POST.equals(actionType)) {
					Action action = resource.getAction(actionType);
					insertExampleToCache(resource.getUri(), ramlLocation, actionType, action.getBody());
				} else {
					Logger.log("Supported RequestMethods are Get and Post, but found " + actionType);
				}
			}
		}

		if (MapUtils.isNotEmpty(resource.getResources())) {
			for (Resource nestedResource : resource.getResources().values()) {
				parseResource(nestedResource, ramlLocation);
			}
		}
	}

	private void insertExampleToCache(String uri, String ramlLocation, ActionType actionType,
			Map<String, MimeType> body) {

		if (MapUtils.isNotEmpty(body)) {
			// response.getBody().get(MediaType.APPLICATION_JSON).getExample();
			for (String contentType : body.keySet()) {
				ResponseData responseData = new ResponseData();
				responseData.setResponseContentType(contentType);
				responseData.setMimeType(body.get(contentType));
				responseData.setActionType(actionType);

				responseData.getMimeType()
						.setExample(parseAndExtractExample(responseData.getMimeType().getExample(), ramlLocation));

				RAMLCache.insertInToCache(uri, responseData);
				break;
			}

		} else {
			Logger.log("response body not found");
		}
	}

	private String parseAndExtractExample(String example, String ramlLocation) {
		if (example == null)
			return example;

		example = example.trim();
		if (example.startsWith(Constants.INCLUDE_TAG)) {
			String[] splitExample = example.split(Constants.INCLUDE_TAG);

			if (splitExample != null && splitExample.length > 1) {
				String exampleURL = splitExample[1];
				exampleURL = exampleURL.trim();

				if (!exampleURL.isEmpty()) {
					try {
						if (!ramlLocation.endsWith(Constants.ROOT))
							ramlLocation = ramlLocation + Constants.ROOT;

						example = Files.toString(new File(ramlLocation + exampleURL), StandardCharsets.UTF_8);
					} catch (IOException e) {
						Logger.log(e);
					}
				}
			}
		}
		return example;
	}

}
