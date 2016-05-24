package com.bandi.util;

public class DBConstants {

	/**
	 * Not to close the DB and clean the data when the last connection to DB is
	 * closed.
	 */
	public static final String KEEP_CONNECTION_ALIVE = "DB_CLOSE_DELAY=-1;";

	/**
	 * Creates an In Memory Database however this can be used only be the same
	 * virtual machine and class loader environment.
	 */
	public static final String MOCKSERVICE_PUBLIC_INMEMORY_DB_URL = "jdbc:h2:mem:mockservice;";

	/**
	 * Schema name
	 */
	private static final String SCHEMA_NAME = "MOCK";

	/**
	 * Init Delimiter of h2
	 */
	private static final String DELIMITER = "\\;";

	/**
	 * Creating Schema
	 */
	public static final String MOCKSERVICE_SCHEMA = "INIT=CREATE SCHEMA IF NOT EXISTS " + DBConstants.SCHEMA_NAME
			+ DBConstants.DELIMITER + "SET SCHEMA " + DBConstants.SCHEMA_NAME + DBConstants.DELIMITER;

	/**
	 * Creates an In Memory Database however this can be used by anyone who use
	 * the same url.
	 */
	public static final String MOCKSERVICE_PUBLIC_INMEMORY_TCP_DB_URL = "jdbc:h2:tcp://localhost/mem:mockservice;";

	public static final String CREATE_DB_AND_INITIALIZE = "RUNSCRIPT FROM 'classpath:scripts/create.sql'"
			+ DBConstants.DELIMITER + "RUNSCRIPT FROM 'classpath:scripts/init.sql'";

	public static final String JDBC_DRIVER = "org.h2.Driver";

	public static final String FINAL_DB_URL_INIT = DBConstants.MOCKSERVICE_PUBLIC_INMEMORY_DB_URL
			+ DBConstants.KEEP_CONNECTION_ALIVE + DBConstants.MOCKSERVICE_SCHEMA + DBConstants.CREATE_DB_AND_INITIALIZE ;

	public static final String FINAL_DB_URL = DBConstants.MOCKSERVICE_PUBLIC_INMEMORY_DB_URL
			+ DBConstants.KEEP_CONNECTION_ALIVE + DBConstants.MOCKSERVICE_SCHEMA;
}
