package com.bandi.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;

import javax.ws.rs.NotSupportedException;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
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
import org.apache.http.util.EntityUtils;
import org.raml.model.MimeType;

import com.bandi.cache.ServerCache;
import com.bandi.data.ResponseData;
import com.bandi.data.ServerData;
import com.bandi.log.Logger;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Cookie;
import io.vertx.ext.web.RoutingContext;

public class HttpClientHandler {

	private HttpServerRequest request;

	private RoutingContext routingContext;

	public HttpClientHandler(RoutingContext routingContext, HttpServerRequest request) {
		this.request = request;
		this.routingContext = routingContext;
	}

	public ResponseData getResponseDataFromServer(String uri) {
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
				/*
				 * System.out.println("Form " +
				 * request.formAttributes().size()); System.out.println("body "
				 * + routingContext.getBodyAsString());
				 * System.out.println("data " + routingContext.data().size());
				 */
				/*
				 * request.bodyHandler(buffer -> {
				 * System.out.println("request data " + buffer.getString(0,
				 * buffer.length())); });
				 */
				// request.endHandler(System.out::println);
				// request.exceptionHandler(System.out::println);
				/*
				 * System.out.println("IsEnded : " + request.isEnded());
				 * System.out.println("Params "+request.params());
				 */

			} catch (ParseException e) {
				Logger.log(e);
			}
			try {
				if (request.formAttributes() != null && request.formAttributes().size() > 0) {
					List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
					request.formAttributes().entries().stream().forEach(
							entity -> urlParameters.add(new BasicNameValuePair(entity.getKey(), entity.getValue())));
					((HttpPost) httpRequestMethod).setEntity(new UrlEncodedFormEntity(urlParameters));
				}
				else if (routingContext.getBodyAsString() != null) {
					HttpEntity entity;
					entity = new ByteArrayEntity(routingContext.getBodyAsString().getBytes("UTF-8"));
					((HttpPost) httpRequestMethod).setEntity(entity);
				} 
			} catch (UnsupportedEncodingException e) {
				Logger.log(e);
			}
		} else
			throw new NotSupportedException("Http Method is not supported : " + request.method());

		// Copy Headers
		copyHeaders(httpRequestMethod);

		// Copy Cookies
		CookieStore cookieStore = copyCookies();
		HttpClientContext context = HttpClientContext.create();
		context.setCookieStore(cookieStore);

		return invokeExternalServer(countDown, httpclient, httpRequestMethod, context);

		/*
		 * HttpClient httpClient = HttpVerticle.getVert().createHttpClient();
		 * httpClient.getNow(serverData.getPort(), serverData.getUrl(), uri, new
		 * HttpClientResponseHandler(responseData, countDown));
		 * 
		 * try { countDown.await(); } catch (InterruptedException e) {
		 * Thread.currentThread().interrupt(); }
		 */
	}

	private ResponseData invokeExternalServer(CountDownLatch countDown, CloseableHttpClient httpclient,
			HttpRequestBase httpRequestMethod, HttpClientContext context) {
		try (CloseableHttpResponse httpResponse = httpclient.execute(httpRequestMethod, context)) {
			// Logger.log(httpResponse.getStatusLine());
			HttpEntity entity = httpResponse.getEntity();
			/*
			 * BufferedReader rd = new BufferedReader(new
			 * InputStreamReader(entity.getContent()));
			 * 
			 * 
			 * String line = ""; while ((line = rd.readLine()) != null) {
			 * response.append(line); }
			 */

			ResponseData responseData = new ResponseData();
			MimeType mimeType = new MimeType();
			mimeType.setExample(EntityUtils.toString(entity));
			mimeType.setType(entity.getContentType().getValue());
			responseData.setMimeType(mimeType);
			responseData.setStatusCode(httpResponse.getStatusLine().getStatusCode());

			EntityUtils.consume(entity);
			countDown.countDown();
			// System.out.println("Response from server " + responseData);
			return responseData;
		} catch (IllegalStateException e) {
			Logger.log(e);
		} catch (IOException e) {
			Logger.log(e);
		}
		return null;
	}

	private void copyHeaders(HttpRequestBase httpRequestMethod) {
		MultiMap map = request.headers();
		for (Entry<String, String> header : map) {
			if (header.getKey().equals("Content-Length") && request.method() == HttpMethod.POST)
				continue;

			httpRequestMethod.addHeader(header.getKey(), header.getValue());
		}
	}

	private CookieStore copyCookies() {
		CookieStore cookieStore = new BasicCookieStore();
		for (Cookie cookie : routingContext.cookies()) {
			BasicClientCookie newcookie = new BasicClientCookie(cookie.getName(), cookie.getValue());
			cookie.setDomain(cookie.getDomain());
			cookie.setPath(cookie.getPath());
			cookieStore.addCookie(newcookie);
		}
		return cookieStore;
	}
}
