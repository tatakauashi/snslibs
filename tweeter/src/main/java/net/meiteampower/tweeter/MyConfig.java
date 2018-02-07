package net.meiteampower.tweeter;

import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.meiteampower.tweeter.db.DBAccessor;

/**
 * 設定値を定義する。
 * @author kie
 */
public final class MyConfig {

	private static final Logger loggger = LoggerFactory.getLogger(MyConfig.class);

	private static long id = 0;
	private static MyConfig singleton = null;
	private String myAccessToken;
	private String myAccessTokenSecret;
	private String tmpDir;
	private String tweetPassword = "";

	private MyConfig() {
	}

	private static void initialize() {
		singleton = new MyConfig();

		ResourceBundle bundle = ResourceBundle.getBundle("tweeter");
		if (bundle.containsKey("oauth.accessToken")) {
			singleton.myAccessToken = bundle.getString("oauth.accessToken");
		} else {
			loggger.warn("Can't find property 'oauth.accessToken'.");
		}
		if (bundle.containsKey("oauth.accessTokenSecret")) {
			singleton.myAccessTokenSecret = bundle.getString("oauth.accessTokenSecret");
		} else {
			loggger.warn("Can't find property 'oauth.accessTokenSecret'.");
		}

		initProperties();
	}

	private static void initProperties() {
		ResourceBundle bundle = ResourceBundle.getBundle("tweeter");
		if (bundle.containsKey("tmp.dir")) {
			singleton.tmpDir = bundle.getString("tmp.dir");
		} else {
			loggger.warn("Can't find property 'tmp.dir'.");
		}
		if (bundle.containsKey("tweet.password")) {
			singleton.tweetPassword = bundle.getString("tweet.password");
		} else {
			loggger.warn("Can't find property 'tweet.password'.");
		}
	}

	public static MyConfig getInstance() {
		if (singleton == null) {
			return getInstance(0);
		}
		return singleton;
	}
	public static MyConfig getInstance(long id) {
		if (id == 0) {
			initialize();
		} else {
			// ＤＢから取得する。
			singleton = new MyConfig();
			singleton.myAccessToken = "";
			singleton.myAccessTokenSecret = "";

			initProperties();

			try {
				String[] token = DBAccessor.getAccessToken(String.valueOf(id));
				if (token != null && token.length == 2) {
					singleton.myAccessToken = token[0];
					singleton.myAccessTokenSecret = token[1];
					singleton.id = id;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return singleton;
	}

	public String getMyAccessToken() {
		return myAccessToken;
	}
	public String getMyAccessTokenSecret() {
		return myAccessTokenSecret;
	}
	public final String getTmpDir() {
		return tmpDir;
	}
	public final String getTweetPassword() {
		return tweetPassword;
	}
}
