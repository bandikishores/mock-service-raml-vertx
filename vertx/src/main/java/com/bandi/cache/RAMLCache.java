package com.bandi.cache;

import java.util.HashMap;

import com.bandi.data.ResponseData;
import com.bandi.log.Logger;

import lombok.Data;

@Data
public class RAMLCache {

	private static HashMap<String, ResponseData> cacheofRAML = new HashMap<String, ResponseData>();

	public static void insertInToCache(String uri, ResponseData responseData) {
		cacheofRAML.put(uri, responseData);
	}

	public static void printValuesInCache() {
		Logger.log(cacheofRAML);
	}

	public static boolean presentInCache(String uri) {
		return cacheofRAML.containsKey(uri);
	}

	public static ResponseData getResponseDataFromCache(String uri) {
		return cacheofRAML.get(uri);
	}

}
