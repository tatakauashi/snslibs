package net.meiteampower.db.factory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.meiteampower.util.Crypto;

public class DBFactory {

	private static final Logger logger = LoggerFactory.getLogger(DBFactory.class);
	private static final String DB_PROP_FILE_NAME = "db.properties";

	private String connectionString;
	private String loginId;
	private String password;
	private String initialQuery;
	private String seed = "FzVR_fymZw4";

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
			if (bundle.containsKey("db.seed")) {
				seed = bundle.getString("db.seed");
			}

		} catch (Exception e) {
			logger.error("DB接続の生成に失敗しました。", e);
			throw new RuntimeException(e);
		}
	}

	private static DBFactory getInstance() {
		if (factory == null) {
			factory = new DBFactory();
		}
		return factory;
	}

	public static Connection getConnection() throws SQLException {

		DBFactory factory = getInstance();
		return getConnectionDetail(factory.password);
	}

	public static Connection getConnection(final String password) throws Exception {

		DBFactory factory = getInstance();
		return getConnectionDetail(Crypto.decrypt(factory.password, password));
	}

	private static Connection getConnectionDetail(final String password) throws SQLException {

		Connection connection = DriverManager.getConnection(
				factory.connectionString, factory.loginId, password);
		if (factory.initialQuery != null) {
			connection.prepareStatement(factory.initialQuery).executeQuery();
		}

		return connection;
	}

	public static String getPassword(final String p) {
		return Crypto.getPassword(p + getInstance().seed);
	}
}
