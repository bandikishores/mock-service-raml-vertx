package com.bandi.main;

import org.apache.commons.lang3.ArrayUtils;

import com.bandi.util.Constants;
import com.bandi.verticle.HttpVerticle;
import io.vertx.core.Vertx;

public class VertxMain {

	public static void main(String[] args) {
		if (ArrayUtils.isNotEmpty(args)) {
			System.setProperty(Constants.RAML_FOLDER, args[0]);
		}
		
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new HttpVerticle());

		/*
		 * vertx.deployVerticle(new MyVerticle(), stringAsyncResult -> {
		 * System.out.println("BasicVerticle deployment complete"); });
		 */
	}

}
