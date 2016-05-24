package com.bandi.cache;

import org.raml.model.ActionType;

import com.bandi.data.ResponseData;
import com.bandi.log.Logger;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import lombok.Data;

@Data
public class RAMLCache {

	private static Table<String, String, ResponseData> cacheofRAML = HashBasedTable.create();

	public static void insertInToCache(String uri, String actionType, ResponseData responseData) {
		cacheofRAML.put(uri, actionType, responseData);
	}

	public static void printValuesInCache() {
		Logger.log(cacheofRAML);
	}

	public static boolean presentInCache(String uri, ActionType actionType) {
		if (cacheofRAML.contains(uri, actionType.name())) {
			return true;
		}
		return false;
	}

	public static ResponseData getResponseDataFromCache(String uri, ActionType actionType) {
		if (presentInCache(uri, actionType)) {
			return cacheofRAML.get(uri, actionType.name());
		}
		return null;
	}

}
