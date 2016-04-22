package com.bandi.http;

import org.apache.commons.httpclient.HttpStatus;
import org.raml.model.ActionType;

import com.bandi.cache.RAMLCache;
import com.bandi.client.HttpClientHandler;
import com.bandi.data.ResponseData;
import com.bandi.log.Logger;
import com.bandi.util.Utils;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class HttpResponseHandler {

	private HttpServerRequest request;

	private RoutingContext routingContext;

	public HttpResponseHandler(RoutingContext routingContext, HttpServerRequest request) {
		this.request = request;
		this.routingContext = routingContext;
	}

	public void createResponse() {
		HttpServerResponse response = null;
		ActionType actionType = Utils.convertHttpMethodToActionType(request.method());

		String uri = Utils.convertURLToString(request.uri());

		if (uri != null && RAMLCache.presentInCache(uri, actionType)) {
			ResponseData responseData = RAMLCache.getResponseDataFromCache(uri, actionType);
			response = createResponse(responseData);
		} else {
			ResponseData responseData = null;

			try {
				responseData = new HttpClientHandler(routingContext, request).getResponseDataFromServer(uri);
			} catch (Exception e) {
				Logger.log(e);
			}

			if (responseData != null) {
				response = createResponse(responseData);
			} else {
				response = request.response();
				String NOT_FOUND = "Could not find an example in cache for request URI " + uri;
				response.setStatusCode(HttpStatus.SC_NOT_FOUND);
				response.headers().add("Content-Length", String.valueOf(NOT_FOUND.length()));
				response.write(NOT_FOUND);
			}
		}
		response.end();
	}

	private HttpServerResponse createResponse(ResponseData responseData) {
		HttpServerResponse response = request.response();

		if (responseData.getStatusCode() != null)
			response.setStatusCode(responseData.getStatusCode());
		else
			response.setStatusCode(HttpStatus.SC_OK);

		response.headers().add("Content-Type", responseData.getResponseContentType());

		String responseText = responseData.getMimeType().getExample();

		if (responseText != null) {
			response.headers().add("Content-Length", String.valueOf(responseText.length()));
			response.write(responseText);
		}
		return response;
	}

	public void close() {
		request.response().close();
	}

}
