package com.bandi.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;

import com.bandi.log.Logger;
import com.bandi.main.VertxMain;
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
		System.out.println("incoming Static request!");
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

		InputStream in = VertxMain.class.getClassLoader().getResourceAsStream(Constants.WEBROOT + Constants.INDEX_PAGE);
		
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
