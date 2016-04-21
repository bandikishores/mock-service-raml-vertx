package com.bandi.admin;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

public class AdminRoutingContext implements Handler<RoutingContext> {

	@Override
	public void handle(RoutingContext routingContext) {
		new AdminRequestResponseHandler(routingContext).handle(routingContext.request());
	}
}
