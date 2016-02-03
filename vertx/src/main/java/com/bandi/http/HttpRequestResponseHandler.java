package com.bandi.http;

import java.util.HashMap;

import com.bandi.data.ResponseData;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;

public class HttpRequestResponseHandler implements Handler<HttpServerRequest> {
	
	HashMap<String, ResponseData> cacheofRAML;

	public HttpRequestResponseHandler(HashMap<String, ResponseData> cacheofRAML2) {
		this.cacheofRAML = cacheofRAML2;
	}

	@Override
	public void handle(HttpServerRequest request) {
		System.out.println("incoming request!");

		Buffer fullRequestBody = Buffer.buffer();
		
		if (request.method() == HttpMethod.POST) {
			request.handler(new HttpPostRequestHandler(fullRequestBody));

			/*request.endHandler(new Handler<Void>() {
				@Override
				public void handle(Void event) {
					// here you can access the 
                    // fullRequestBody Buffer instance.
				}
			});*/
		}
		
		HttpResponseHandler httpResponseHandler = new HttpResponseHandler(request, cacheofRAML);
		httpResponseHandler.createResponse();
		httpResponseHandler.close();
	}

}
