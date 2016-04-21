package com.bandi.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;

import javax.ws.rs.NotSupportedException;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.raml.model.ActionType;
import org.raml.model.MimeType;

import com.bandi.cache.RAMLCache;
import com.bandi.cache.ServerCache;
import com.bandi.data.ResponseData;
import com.bandi.data.ServerData;
import com.bandi.log.Logger;
import com.bandi.util.Utils;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Cookie;
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
				responseData = getResponseDataFromServer(uri);
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

	private ResponseData getResponseDataFromServer(String uri) {
		CountDownLatch countDown = new CountDownLatch(1);

		String baseURI = uri.split("/")[1];
		ServerData serverData = ServerCache.getServerData(baseURI);
		if (serverData == null)
			return null;

		CloseableHttpClient httpclient = HttpClients.createDefault();

		HttpRequestBase httpRequestMethod = null;

		if (request.method() == HttpMethod.GET)
			httpRequestMethod = new HttpGet(serverData.toString() + uri);
		else if (request.method() == HttpMethod.POST) {
			httpRequestMethod = new HttpPost(serverData.toString() + uri);

			try {
				/*System.out.println("Form " + request.formAttributes().size());
				System.out.println("body " + routingContext.getBodyAsString());
				System.out.println("data " + routingContext.data().size());*/
				/*request.bodyHandler(buffer -> {
					System.out.println("request data " + buffer.getString(0, buffer.length()));
				});*/
				//request.endHandler(System.out::println);
				//request.exceptionHandler(System.out::println);
				/*System.out.println("IsEnded : " + request.isEnded());
				System.out.println("Params "+request.params());*/
				
			} catch (ParseException e) {
				Logger.log(e);
			}
			HttpEntity entity;
			try {
				entity = new ByteArrayEntity(routingContext.getBodyAsString().getBytes("UTF-8"));
				((HttpPost)httpRequestMethod).setEntity(entity);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
			throw new NotSupportedException("Http Method is not supported : " + request.method());

		// Copy Headers
		MultiMap map = request.headers();
		for (Entry<String, String> header : map) {
			if (header.getKey().equals("Content-Length") && request.method() == HttpMethod.POST)
				continue;

			httpRequestMethod.addHeader(header.getKey(), header.getValue());
		}

		// Copy Cookies
		CookieStore cookieStore = new BasicCookieStore();
		for (Cookie cookie : routingContext.cookies()) {
			BasicClientCookie newcookie = new BasicClientCookie(cookie.getName(), cookie.getValue());
			cookie.setDomain(cookie.getDomain());
			cookie.setPath(cookie.getPath());
			cookieStore.addCookie(newcookie);
		}

		HttpClientContext context = HttpClientContext.create();
		context.setCookieStore(cookieStore);

		// StringBuilder response = new StringBuilder();

		try (CloseableHttpResponse httpResponse = httpclient.execute(httpRequestMethod, context)) {
			System.out.println(httpResponse.getStatusLine());
			HttpEntity entity = httpResponse.getEntity();

			BufferedReader rd = new BufferedReader(new InputStreamReader(entity.getContent()));

			/*
			 * String line = ""; while ((line = rd.readLine()) != null) {
			 * response.append(line); }
			 */

			ResponseData responseData = new ResponseData();
			MimeType mimeType = new MimeType();
			mimeType.setExample(EntityUtils.toString(entity));
			responseData.setMimeType(mimeType);
			responseData.setStatusCode(httpResponse.getStatusLine().getStatusCode());
			responseData.setResponseContentType(entity.getContentType().getValue());

			EntityUtils.consume(entity);
			countDown.countDown();
			// System.out.println("Response from server " + responseData);
			return responseData;
		} catch (IllegalStateException e) {
			Logger.log(e);
		} catch (IOException e) {
			Logger.log(e);
		}

		/*
		 * HttpClient httpClient = HttpVerticle.getVert().createHttpClient();
		 * httpClient.getNow(serverData.getPort(), serverData.getUrl(), uri, new
		 * HttpClientResponseHandler(responseData, countDown));
		 * 
		 * try { countDown.await(); } catch (InterruptedException e) {
		 * Thread.currentThread().interrupt(); }
		 */
		return null;
	}

	public void close() {
		request.response().close();
	}

}
