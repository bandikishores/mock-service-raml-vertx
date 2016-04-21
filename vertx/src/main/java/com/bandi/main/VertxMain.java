package com.bandi.main;

import org.apache.commons.lang3.ArrayUtils;

import com.bandi.cache.ServerCache;
import com.bandi.data.ServerData;
import com.bandi.util.Constants;
import com.bandi.verticle.HttpVerticle;
import io.vertx.core.Vertx;

public class VertxMain {

	public static void main(String[] args) {
		if (ArrayUtils.isNotEmpty(args)) {
			System.setProperty(Constants.RAML_FOLDER, args[0]);
		}
		

		ServerData serverData = new ServerData();
		serverData.setUrl("qa1007.mw.corp.inmobi.com");
		serverData.setPort(11100);
		/* Kish - TODO Remove this during release */
		ServerCache.insertInToCache("advertiser", serverData);
		
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new HttpVerticle());

		/*
		 * vertx.deployVerticle(new MyVerticle(), stringAsyncResult -> {
		 * System.out.println("BasicVerticle deployment complete"); });
		 */
	}

}
