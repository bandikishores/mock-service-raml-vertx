package com.bandi.cache;

import java.util.HashMap;
import java.util.Set;

import com.bandi.data.ServerData;

import lombok.Data;

@Data
public class ServerCache {

	private static HashMap<String, ServerData> cacheofServers = new HashMap<>();

	public static void insertInToCache(String baseURI, ServerData serverData) {
		cacheofServers.put(baseURI, serverData);
	}

	public static boolean isServerPresent(String baseURI) {
		return cacheofServers.containsKey(baseURI);
	}

	public static ServerData getServerData(String baseURI) {
		return cacheofServers.get(baseURI);
	}

	public static Set<String> getAllServerBaseURI() {
		return cacheofServers.keySet();
	}
}
