package com.bandi.util;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.raml.model.ActionType;

import com.bandi.log.Logger;
import com.bandi.main.VertxMain;

import io.vertx.core.http.HttpMethod;

public class Utils {

	public static List<Path> getRAMLFilesPath() {
		List<Path> pathToFiles = null;
		String ramlFolderPath = System.getProperty(Constants.RAML_FOLDER);
		try {
			if (StringUtils.isBlank(ramlFolderPath)) {
				Enumeration<URL> urls = VertxMain.class.getClassLoader().getResources(Constants.ramlLocation);

				if (urls != null && urls.hasMoreElements()) {
					URL url = urls.nextElement();
					ramlFolderPath = url.getPath();
				}
			}
			if (StringUtils.isNotBlank(ramlFolderPath))
				pathToFiles = Files.walk(Paths.get(ramlFolderPath)).filter(Files::isRegularFile)
						.collect(Collectors.toList());
			else
				Logger.log("Couldn't find a valid raml folder to load RAMLs");
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
}
