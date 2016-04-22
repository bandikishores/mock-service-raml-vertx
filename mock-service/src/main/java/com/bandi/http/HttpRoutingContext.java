package com.bandi.http;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class HttpRoutingContext implements Handler<RoutingContext> {

	@Override
	public void handle(RoutingContext routingContext) {
		new HttpRequestResponseHandler(routingContext).handle(routingContext.request());
	}
}
