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

import com.bandi.log.Logger;
import com.bandi.main.VertxMain;

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
}
