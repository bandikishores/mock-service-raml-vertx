package com.bandi.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.raml.model.ActionType;

import com.bandi.log.Logger;
import com.bandi.main.MockServiceMain;

import io.vertx.core.http.HttpMethod;

public class Utils {

	public static List<Path> getRAMLFilesPath() {
		List<Path> pathToFiles = null;
		String ramlFolderPath = System.getProperty(Constants.RAML_FOLDER);
		try {
			if (StringUtils.isBlank(ramlFolderPath)) {
				Enumeration<URL> urls = MockServiceMain.class.getClassLoader().getResources(Constants.ramlLocation);

				if (urls != null && urls.hasMoreElements()) {
					URL url = urls.nextElement();
					ramlFolderPath = url.getPath();
				}
			}
			if (StringUtils.isNotBlank(ramlFolderPath))
				pathToFiles = Files.walk(Paths.get(ramlFolderPath)).filter(Files::isRegularFile)
						.collect(Collectors.toList());
			else
				Logger.error("Couldn't find a valid raml folder to load RAMLs");
		} catch (IOException e) {
			Logger.log(e);
		}
		return pathToFiles;
	}

	public static ActionType convertHttpMethodToActionType(HttpMethod httpMethod) {
		if (HttpMethod.GET.equals(httpMethod))
			return ActionType.GET;
		else if (HttpMethod.POST.equals(httpMethod))
			return ActionType.POST;
		else if (HttpMethod.PUT.equals(httpMethod))
			return ActionType.PUT;
		else if (HttpMethod.DELETE.equals(httpMethod))
			return ActionType.DELETE;
		else if (HttpMethod.HEAD.equals(httpMethod))
			return ActionType.HEAD;
		else if (HttpMethod.OPTIONS.equals(httpMethod))
			return ActionType.OPTIONS;
		else if (HttpMethod.TRACE.equals(httpMethod))
			return ActionType.TRACE;
		else if (HttpMethod.PATCH.equals(httpMethod))
			return ActionType.PATCH;

		return null;
	}

	public static String convertURLToPath(String uri) {

		if (StringUtils.isEmpty(uri))
			return uri;

		try {
			String escapedURI = URLDecoder.decode(uri, "UTF-8");

			escapedURI = new java.net.URI(escapedURI).getPath();

			if (escapedURI.endsWith("/"))
				escapedURI = escapedURI.substring(0, escapedURI.length() - 1);

			return escapedURI;
		} catch (UnsupportedEncodingException e) {
			Logger.log(e);
		} catch (URISyntaxException e) {
			Logger.log(e);
		}
		return uri;
	}

	public static String convertURLToString(String uri) {

		if (StringUtils.isEmpty(uri))
			return uri;

		try {
			String escapedURI = URLDecoder.decode(uri, "UTF-8");

			if (escapedURI.endsWith("/"))
				escapedURI = escapedURI.substring(0, escapedURI.length() - 1);

			return escapedURI;
		} catch (UnsupportedEncodingException e) {
			Logger.log(e);
		}
		return uri;
	}
}
