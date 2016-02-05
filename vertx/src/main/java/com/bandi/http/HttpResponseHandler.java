package com.bandi.http;

import java.util.HashMap;

import javax.ws.rs.core.MediaType;

import org.apache.commons.httpclient.HttpStatus;
import org.raml.model.ActionType;

import com.bandi.cache.RAMLCache;
import com.bandi.data.ResponseData;
import com.bandi.util.Constants;
import com.bandi.util.Utils;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

public class HttpResponseHandler {

	private HttpServerRequest request;

	public HttpResponseHandler(HttpServerRequest request) {
		this.request = request;
	}

	public void createResponse() {

		HttpServerResponse response = request.response();
		ActionType actionType = Utils.convertHttpMethodToActionType(request.method());
		
		String uri = Utils.convertURLToString(request.uri()); 

		if (uri != null && RAMLCache.presentInCache(uri, actionType)) {
			ResponseData responseData = RAMLCache.getResponseDataFromCache(uri, actionType);
			response.setStatusCode(HttpStatus.SC_OK);
			response.headers().add("Content-Type", responseData.getResponseContentType());

			String responseText = responseData.getMimeType().getExample();

			if (responseText != null) {
				response.headers().add("Content-Length", String.valueOf(responseText.length()));
				response.write(responseText);
			}
		} else {
			String NOT_FOUND = "Could not find an example in cache for request URI " + uri;
			response.setStatusCode(HttpStatus.SC_NOT_FOUND);
			response.headers().add("Content-Length", String.valueOf(NOT_FOUND.length()));
			response.write(NOT_FOUND);
		}
		response.end();
	}

	public void close() {
		request.response().close();
	}

}
