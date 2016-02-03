package com.bandi.log;

import java.io.IOException;

public class Logger {
	
	public static void log(String log)
	{
		System.out.println(log);
	}

	public static void log(IOException e) {
		System.out.println(e);
	}

}
