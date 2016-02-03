package com.bandi.http;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;

public class HttpPostRequestHandler implements Handler<Buffer> {
	
	Buffer fullRequestBody;
	
	public HttpPostRequestHandler(Buffer fullRequestBody) {
		this.fullRequestBody = fullRequestBody;
	}

	@Override
	public void handle(Buffer buffer) {
		fullRequestBody.appendBuffer(buffer);
	}

}
