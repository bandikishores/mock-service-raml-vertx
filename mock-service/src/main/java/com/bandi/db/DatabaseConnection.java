package com.bandi.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.PostConstruct;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import com.bandi.log.Logger;
import com.bandi.util.DBConstants;

import lombok.Getter;

public class DatabaseConnection {

	@PostConstruct
	public static void printCompleted() {
		Logger.info("Database Initialized");
	}

	@Getter(lazy = true)
	private static final ObjectPool<PoolableConnection> connectionPool = createConnectionPool();

	private static ObjectPool<PoolableConnection> createConnectionPool() {
		// 1. Register the Driver to the jbdc.driver java property
		registerJDBCDriver(DBConstants.JDBC_DRIVER);
		// 2. Create the Connection Factory (DriverManagerConnectionFactory)
		ConnectionFactory connectionFactory = getConnectionFactory();
		// 3. Instantiate the Factory of Pooled Objects
		PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);
		// 4. Create the Pool with the PoolableConnection objects
		ObjectPool<PoolableConnection> pool = new GenericObjectPool<>(poolableConnectionFactory);
		// 5. Set the objectPool to enforces the association (prevent bugs)
		poolableConnectionFactory.setPool(pool);

		return pool;
	}

	/**
	 * Refer to h2 features on how to make this
	 * http://www.h2database.com/html/features.html#in_memory_databases
	 */
	public static Connection getConnection() throws ClassNotFoundException, SQLException {
		registerJDBCDriver(DBConstants.JDBC_DRIVER);
		Connection conn = DriverManager.getConnection(DBConstants.FINAL_DB_URL);
		return conn;
	}

	/**
	 * Get a Connection Factory, the default implementation is a
	 * DriverManagerConnectionFactory
	 */
	private static ConnectionFactory getConnectionFactory() {
		return new DriverManagerConnectionFactory(DBConstants.FINAL_DB_URL_INIT, null);
	}

	/**
	 * @param driver
	 */
	private static void registerJDBCDriver(String driver) {
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			Logger.error("There was not able to find the driver class");
		}
	}

	public static void testAllConnections() {
		try {
			ObjectPool<PoolableConnection> connectionPool = DatabaseConnection.getConnectionPool();

			try (PoolableConnection poolConnection = connectionPool.borrowObject();
					Statement statement = poolConnection.createStatement();) {
				statement.execute("SELECT * FROM RESPONSE_DATA");
				ResultSet resultSet = statement.getResultSet();
				resultSet.next();
				Logger.error(" Result from DB Pooled Connection : " + resultSet.getString(4));
			}

			try (Connection jdbcConnection = getConnection();
					Statement jdbcStatement = jdbcConnection.createStatement();) {
				jdbcStatement.execute("SELECT * FROM URI_CACHE");
				ResultSet resultSet = jdbcStatement.getResultSet();
				resultSet.next();
				Logger.error(" Result from JDBC Connection : " + resultSet.getString(1));
			}
		} catch (Exception e) {
			Logger.log(e);
		}
	}

}
