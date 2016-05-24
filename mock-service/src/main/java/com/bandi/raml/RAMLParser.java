package com.bandi.raml;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
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

				if(isRAMLExcluded(path))
					continue;

				String ramlLocation = path.toUri().toString();

				if (Validator.isValidRAML(ramlLocation)) {
					Raml raml = new RamlDocumentBuilder().build(ramlLocation);

					if (raml != null) {
						parse(raml, path.getParent().toString());
					} else {
						Logger.error(" Documentation not present for RAML to load example");
					}
				} else {
					Logger.error("Couldn't load raml at " + ramlLocation + " as RAML is Invalid");
				}
			}
		} else {
			Logger.error("No RAMLs found");
		}
	}

	private boolean isRAMLExcluded(Path path) {
		if (!path.toString().endsWith(Constants.RAML_EXTENSION))
			return true;
		else if(path.toString().contains(Constants.TRAITS))
			return true;
		
		return false;
	}

	public void parse(Raml raml, String ramlLocation) {
		
		String basePath = raml.getBasePath();
		if(StringUtils.isEmpty(basePath)) basePath = "";
		
		Map<String, Resource> resources = raml.getResources();
		if (MapUtils.isNotEmpty(resources)) {
			for (Resource resource : resources.values()) {
				parseResource(resource, ramlLocation, basePath);
			}

			// RAMLCache.printValuesInCache();
		} else {
			Logger.error("No resources found in RAML file " + raml.getTitle());
		}
	}

	private void parseResource(Resource resource, String ramlLocation, String basePath) {
		if (MapUtils.isNotEmpty(resource.getActions())) {
			for (ActionType actionType : resource.getActions().keySet()) {
				if (ActionType.GET.equals(actionType)) {
					Action action = resource.getAction(actionType);
					Map<String, Response> responses = action.getResponses();
					if (MapUtils.isNotEmpty(responses)) {
						for (Response response : responses.values()) {
							insertExampleToCache(basePath + resource.getUri(), ramlLocation, actionType, response.getBody());
						}
					}
				} else if (ActionType.POST.equals(actionType)) {
					Action action = resource.getAction(actionType);
					Map<String, Response> responses = action.getResponses();
					if (MapUtils.isNotEmpty(responses)) {
						for (Response response : responses.values()) {
							insertExampleToCache(basePath + resource.getUri(), ramlLocation, actionType, response.getBody());
						}
					}
				} else {
					Logger.error("Supported RequestMethods are Get and Post, but found " + actionType);
				}
			}
		}

		if (MapUtils.isNotEmpty(resource.getResources())) {
			for (Resource nestedResource : resource.getResources().values()) {
				parseResource(nestedResource, ramlLocation, basePath);
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
			Logger.error("response body not found");
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
