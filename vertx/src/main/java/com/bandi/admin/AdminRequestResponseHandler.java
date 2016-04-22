package com.bandi.admin;

import com.bandi.http.HttpPostRequestHandler;
import com.bandi.http.HttpResponseHandler;
import com.bandi.log.Logger;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

public class AdminRequestResponseHandler implements Handler<HttpServerRequest> {

	private RoutingContext routingContext;
	
	public AdminRequestResponseHandler(RoutingContext routingContext) {
		this.routingContext = routingContext;
	}
	
	@Override
	public void handle(HttpServerRequest request) {
		Logger.log("Admin request!");

		Buffer fullRequestBody = Buffer.buffer();

		if (request.method() == HttpMethod.POST) {
			request.handler(new HttpPostRequestHandler(fullRequestBody));
		}

		HttpResponseHandler httpResponseHandler = new HttpResponseHandler(routingContext, request);
		httpResponseHandler.createResponse();
		httpResponseHandler.close();
	}
}
