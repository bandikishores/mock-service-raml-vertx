package com.bandi.verticle;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.DocumentationItem;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.model.Response;
import org.raml.parser.loader.DefaultResourceLoader;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.tagresolver.IncludeResolver;
import org.raml.parser.tagresolver.JacksonTagResolver;
import org.raml.parser.tagresolver.JaxbTagResolver;
import org.raml.parser.tagresolver.TagResolver;
import org.raml.parser.visitor.RamlDocumentBuilder;
import org.raml.parser.visitor.RamlValidationService;
import org.raml.parser.visitor.YamlDocumentBuilder;
import org.yaml.snakeyaml.Yaml;

import com.bandi.data.ResponseData;
import com.bandi.http.HttpRequestResponseHandler;
import com.bandi.log.Logger;
import com.bandi.util.Constants;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

public class MyVerticle extends AbstractVerticle {

	private HttpServer httpServer = null;

	private HashMap<String, ResponseData> cacheofRAML = new HashMap<String, ResponseData>();

	@Override
	public void start(Future<Void> startFuture) {
		Logger.log("MyVerticle started!");

		httpServer = vertx.createHttpServer();

		processRAML();

		httpServer.requestHandler(new HttpRequestResponseHandler(cacheofRAML));

		httpServer.listen(Constants.PORT);
	}

	private void processRAML() {
		String file = Constants.ramlLocation;
		URL url = getClass().getClassLoader().getResource(file);
		String ramlLocation = url.toString();

		List<ValidationResult> results = RamlValidationService.createDefault().validate(ramlLocation);

		if (CollectionUtils.isEmpty(results)) {
			Raml raml = new RamlDocumentBuilder().build(ramlLocation);

			if (raml != null) {
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
				}
			} else {
				Logger.log(" Documentation not present for RAML to load example");
			}
		} else {
			Logger.log("Couldn't load raml at " + ramlLocation);
		}
	}

	@Override
	public void stop(Future stopFuture) throws Exception {
		Logger.log("MyVerticle stopped!");
	}

	private TagResolver[] defaultResolver(TagResolver[] tagResolvers) {
		TagResolver[] defaultResolvers = new TagResolver[] { new IncludeResolver(), new JacksonTagResolver(),
				new JaxbTagResolver() };
		return (TagResolver[]) ArrayUtils.addAll(defaultResolvers, tagResolvers);
	}

	private void yamlParserForExtractingExample(URL url, String ramlLocation, Resource resource) {
		Yaml yaml = (Yaml) new YamlDocumentBuilder(Yaml.class, new DefaultResourceLoader(), defaultResolver(null))
				.build(ramlLocation);

		FileReader fr;
		try {
			fr = new FileReader(url.getFile());

			Map config = (Map) yaml.load(fr);
			Map usersConfig = ((Map) config.get(resource.getUri()));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
