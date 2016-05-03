package com.bandi.log;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Logger {

	public static void log(String str) {
		log.error(str);
	}

	public static void log(Exception e) {
		log.error("Exception Occured ", e);
	}

	public static void log(Object cacheofRAML) {
		log.debug(cacheofRAML.toString());
	}

}
