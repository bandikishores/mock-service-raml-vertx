package com.bandi.log;

public class Logger {
	
	public static void log(String log)
	{
		System.out.println(log);
	}

	public static void log(Exception e) {
		e.printStackTrace();
	}

	public static void log(Object cacheofRAML) {
		System.out.println(cacheofRAML.toString());
	}

}
