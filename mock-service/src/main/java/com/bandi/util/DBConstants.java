package com.bandi.util;

public class DBConstants {

	/**
	 * Not to close the DB and clean the data when the last connection to DB is
	 * closed.
	 */
	public static final String KEEP_CONNECTION_ALIVE = ";DB_CLOSE_DELAY=-1";

	/**
	 * Creates an In Memory Database however this can be used only be the same
	 * virtual machine and class loader environment.
	 */
	public static final String MOCKSERVICE_PUBLIC_INMEMORY_DB_URL = "jdbc:h2:mem:mockservice";

	/**
	 * Creates an In Memory Database however this can be used by anyone who use
	 * the same url.
	 */
	public static final String MOCKSERVICE_PUBLIC_INMEMORY_TCP_DB_URL = "jdbc:h2:tcp://localhost/mem:mockservice";

	public static final String CREATE_DB_AND_INITIALIZE = ";INIT=runscript from '~/create.sql'\\;runscript from '~/init.sql'";
}
