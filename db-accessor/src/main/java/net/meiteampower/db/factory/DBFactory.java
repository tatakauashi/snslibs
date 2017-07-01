package net.meiteampower.db.factory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class DBFactory {

	private static final Logger logger = Logger.getLogger(DBFactory.class);
	private static final String DB_PROP_FILE_NAME = "db.properties";

	private String connectionString;
	private String loginId;
	private String password;
	private String initialQuery;

	private static DBFactory factory;

	private DBFactory() {

		try {
			String runPath = this.getClass().getClassLoader().getResource("").getPath();
			File propFile = new File(runPath + DB_PROP_FILE_NAME);
//			System.out.println("propFile.getAbsolutePath()=" + propFile.getAbsolutePath());

			Properties prop = new Properties();
			prop.load(new FileInputStream(propFile));

			connectionString = prop.getProperty("connectionString");
			loginId = prop.getProperty("dbLoginId");
			password = prop.getProperty("dbPassword");
			if (prop.containsKey("initialQuery")) {
				initialQuery = prop.getProperty("initialQuery");
			}

		} catch (IOException e) {
			logger.error("DB接続の生成に失敗しました。", e);
			throw new RuntimeException(e);
		}
	}

	public static Connection getConnection() throws SQLException {

		if (factory == null) {
			factory = new DBFactory();
		}

		Connection connection = DriverManager.getConnection(
				factory.connectionString, factory.loginId, factory.password);
		if (factory.initialQuery != null) {
			connection.prepareStatement(factory.initialQuery).executeQuery();
		}

		return connection;
	}
}
