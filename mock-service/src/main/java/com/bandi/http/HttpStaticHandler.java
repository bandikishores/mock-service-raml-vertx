package com.bandi.http;

import javax.annotation.PostConstruct;

import com.bandi.log.Logger;
import com.bandi.util.Constants;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.handler.StaticHandler;
import lombok.Getter;

@Getter
public class HttpStaticHandler implements Handler<RoutingContext> {

	private StaticHandler staticHandler;

	@PostConstruct
	private void postInitialize() {
		staticHandler = StaticHandler.create();
		staticHandler.setIndexPage(Constants.INDEX_PAGE);
		staticHandler.setWebRoot(Constants.WEBROOT);
	}
	
	@Override
	public void handle(RoutingContext routingContext) {
		// Logger.log("incoming Static request!");
		HttpServerRequest request = routingContext.request();
		HttpServerResponse response = request.response();
		
		// request.handler(StaticHandler.create());
		routingContext.next();
	}

	/*@Override
	public void handle(RoutingContext routingContext) {
		System.out.println("incoming request!");
		HttpServerRequest request = routingContext.request();
		HttpServerResponse response = request.response();

		response.setStatusCode(HttpStatus.SC_OK);
		response.headers().add("Content-Type", MediaType.TEXT_HTML);

		InputStream in = MockServiceMain.class.getClassLoader().getResourceAsStream(Constants.WEBROOT + Constants.INDEX_PAGE);
		
		try {
			File tempFile = File.createTempFile("index", ".xhtml");
			tempFile.deleteOnExit();
			FileOutputStream out = new FileOutputStream(tempFile);
			IOUtils.copy(in, out);

			response.sendFile(tempFile.getAbsolutePath());
		} catch (IOException e) {
			Logger.log(e);
		}

		response.end();
		response.close();
	}*/
}
