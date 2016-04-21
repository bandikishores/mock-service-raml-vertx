package com.bandi.http;

import java.util.HashMap;

import com.bandi.data.ResponseData;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

public class HttpRequestResponseHandler implements Handler<HttpServerRequest> {
	
	private RoutingContext routingContext;
	
	public HttpRequestResponseHandler(RoutingContext routingContext) {
		this.routingContext = routingContext;
	}

	@Override
	public void handle(HttpServerRequest request) {
		System.out.println("incoming request!");

		Buffer fullRequestBody = Buffer.buffer();
		
		if (request.method() == HttpMethod.POST) {
		//	request.handler(new HttpPostRequestHandler(fullRequestBody));

			/*request.endHandler(new Handler<Void>() {
				@Override
				public void handle(Void event) {
					// here you can access the 
                    // fullRequestBody Buffer instance.
				}
			});*/
		}
		
		HttpResponseHandler httpResponseHandler = new HttpResponseHandler(routingContext, request);
		httpResponseHandler.createResponse();
		httpResponseHandler.close();
	}

}
