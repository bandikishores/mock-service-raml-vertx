package com.bandi.verticle;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.raml.model.Resource;
import org.raml.parser.loader.DefaultResourceLoader;
import org.raml.parser.tagresolver.IncludeResolver;
import org.raml.parser.tagresolver.JacksonTagResolver;
import org.raml.parser.tagresolver.JaxbTagResolver;
import org.raml.parser.tagresolver.TagResolver;
import org.raml.parser.visitor.YamlDocumentBuilder;
import org.yaml.snakeyaml.Yaml;

import com.bandi.admin.AdminRoutingContext;
import com.bandi.db.DatabaseConnection;
import com.bandi.http.HttpRoutingContext;
import com.bandi.log.Logger;
import com.bandi.raml.RAMLParser;
import com.bandi.util.Constants;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import lombok.Getter;

public class HttpVerticle extends AbstractVerticle {

	@Getter
	private static Vertx vert;

	@Override
	public void start(Future<Void> startFuture) {

		vert = vertx;

		HttpServer httpServer = vertx.createHttpServer();
		
		DatabaseConnection.printCompleted();
		
		DatabaseConnection.testAllConnections();

		RAMLParser ramlParser = new RAMLParser();
		ramlParser.processRAML();

		Router router = Router.router(vertx);
		router.route(Constants.ROOT).handler(StaticHandler.create());
		router.route().handler(BodyHandler.create());
		router.route().handler(new HttpRoutingContext());
		httpServer.requestHandler(router::accept).listen(Constants.PORT);

		HttpServer adminServer = vertx.createHttpServer();
		Router adminRouter = Router.router(vertx);
		// router1.route().handler(StaticHandler.create());
		
		/*
		   router1.route(Constants.ROOT).handler(ctx -> {
			Logger.log("Got an HTTP request to /");
			ctx.response().sendFile("webroot/index.html");
		});
		
		*/
		adminRouter.route(Constants.ROOT).handler(BodyHandler.create());
		adminRouter.route(Constants.ROOT).handler(new AdminRoutingContext());
		adminRouter.route(Constants.ROOT+Constants.UPDATE).handler(BodyHandler.create());
		adminRouter.route(Constants.ROOT+Constants.UPDATE).handler(new AdminRoutingContext());
		adminServer.requestHandler(adminRouter::accept);
		adminServer.listen(Constants.ADMIN_PORT);

		Logger.error("Mock Service started!");
	}

	@Override
	public void stop(Future stopFuture) throws Exception {
		Logger.error("Mock Service stopped!");
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
			Logger.log(e);
		}
	}

}
