package com.bandi.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.dbcp2.ConnectionFactory;

import com.bandi.util.DBConstants;

public class DatabaseConnection {

	// @Getter(lazy = true)
	// private final Connection conn = getConnection();

	/**
	 * Refer to h2 features on how to make this
	 * http://www.h2database.com/html/features.html#in_memory_databases
	 */
	public Connection getConnection() throws ClassNotFoundException, SQLException {
		Class.forName("org.h2.Driver");
		/*Connection conn = DriverManager
				.getConnection(DBConstants.MOCKSERVICE_PUBLIC_INMEMORY_TCP_DB_URL + DBConstants.KEEP_CONNECTION_ALIVE);
		return conn;*/
		return null;
	}

	public static void setupDriver() {
		/*Class.forName("org.h2.Driver");
		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(DBConstants.MOCKSERVICE_PUBLIC_INMEMORY_TCP_DB_URL + DBConstants.KEEP_CONNECTION_ALIVE ,null);
		PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
		ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableConnectionFactory);
		
*/
	}

}
