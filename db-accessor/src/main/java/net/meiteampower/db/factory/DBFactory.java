package net.meiteampower.db.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

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
//			String runPath = this.getClass().getClassLoader().getResource("").getPath();
//			File propFile = new File(runPath + DB_PROP_FILE_NAME);
//			System.out.println("propFile.getAbsolutePath()=" + propFile.getAbsolutePath());
			ResourceBundle bundle = ResourceBundle.getBundle("db");

//			Properties prop = new Properties();
//			prop.load(new FileInputStream(propFile));

//			connectionString = prop.getProperty("connectionString");
//			loginId = prop.getProperty("dbLoginId");
//			password = prop.getProperty("dbPassword");
			connectionString = bundle.getString("connectionString");
			loginId = bundle.getString("dbLoginId");
			password = bundle.getString("dbPassword");
			if (bundle.containsKey("initialQuery")) {
				initialQuery = bundle.getString("initialQuery");
			}

		} catch (Exception e) {
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
