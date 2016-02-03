package com.bandi.util;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

import com.bandi.log.Logger;
import com.bandi.main.VertxMain;

public class Utils {

	public static List<Path> getRAMLFilesPath() {
		List<Path> pathToFiles = null;
		try {
			Enumeration<URL> urls = VertxMain.class.getClassLoader().getResources(Constants.ramlLocation);

			if (urls != null && urls.hasMoreElements()) {
				URL url = urls.nextElement();
				pathToFiles = Files.walk(Paths.get(url.getPath())).filter(Files::isRegularFile)
						.collect(Collectors.toList());
			}
		} catch (IOException e) {
			Logger.log(e);
		}
		return pathToFiles;
	}
}
