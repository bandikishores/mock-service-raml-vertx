package com.bandi.cache;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.raml.model.ActionType;

import com.bandi.data.ResponseData;
import com.bandi.log.Logger;
import com.bandi.util.Utils;

import lombok.Data;

@Data
public class RAMLCache {

	private static HashMap<String, List<ResponseData>> cacheofRAML = new HashMap<String, List<ResponseData>>();

	public static void insertInToCache(String uri, ResponseData responseData) {
		List<ResponseData> responseDataList = cacheofRAML.get(uri);

		if (CollectionUtils.isEmpty(responseDataList))
			responseDataList = new ArrayList<ResponseData>();

		for (Iterator<ResponseData> iterator = responseDataList.iterator(); iterator.hasNext();) {
			ResponseData existingResponse = iterator.next();
			if (existingResponse.getActionType().equals(responseData.getActionType())) {
				iterator.remove();
				break;
			}
		}

		responseDataList.add(responseData);
		cacheofRAML.put(uri, responseDataList);
	}

	public static void printValuesInCache() {
		Logger.log(cacheofRAML);
	}

	public static boolean presentInCache(String uri, ActionType actionType) {
		if (cacheofRAML.containsKey(uri)) {
			List<ResponseData> responseDataList = cacheofRAML.get(uri);

			if (CollectionUtils.isNotEmpty(responseDataList)) {
				for (ResponseData responseData : responseDataList) {
					if (responseData.getActionType().equals(actionType)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static ResponseData getResponseDataFromCache(String uri, ActionType actionType) {
		if (cacheofRAML.containsKey(uri)) {
			List<ResponseData> responseDataList = cacheofRAML.get(uri);

			if (CollectionUtils.isNotEmpty(responseDataList)) {
				for (ResponseData responseData : responseDataList) {
					if (responseData.getActionType().equals(actionType)) {
						return responseData;
					}
				}
			}
		}
		return null;
	}

}
