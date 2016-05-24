package com.bandi.http;

import java.util.concurrent.CountDownLatch;

import com.bandi.data.ResponseData;
import com.bandi.log.Logger;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClientResponse;
import lombok.Getter;

@Getter
public class HttpClientResponseHandler implements Handler<HttpClientResponse> {

	ResponseData responseData;
	
	CountDownLatch countDown;
	
	
	public HttpClientResponseHandler(ResponseData responseData, CountDownLatch countDown) {
		this.responseData = responseData;
		this.countDown = countDown;
	}

	@Override
	public void handle(HttpClientResponse httpClientResponse) {
		httpClientResponse.exceptionHandler(System.out::println);
		
		httpClientResponse.bodyHandler(new Handler<Buffer>() {
			@Override
			public void handle(Buffer buffer) {
				Logger.error("Response (" + buffer.length() + "): ");
				Logger.error(buffer.getString(0, buffer.length()));
			}
		});
		
		httpClientResponse.endHandler(handler -> countDown.countDown());
	}

	
}
