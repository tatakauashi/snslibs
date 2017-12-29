package net.meiteampower.tweeter;

import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 設定値を定義する。
 * @author kie
 */
public final class MyConfig {

	private static final Logger loggger = LoggerFactory.getLogger(MyConfig.class);

	private static String myAccessToken;
	private static String myAccessTokenSecret;
	private static String tmpDir;

	private MyConfig() {
	}

	static {
		ResourceBundle bundle = ResourceBundle.getBundle("tweeter");
		if (bundle.containsKey("oauth.accessToken")) {
			myAccessToken = bundle.getString("oauth.accessToken");
		} else {
			loggger.warn("Can't find property 'oauth.accessToken'.");
		}
		if (bundle.containsKey("oauth.accessTokenSecret")) {
			myAccessTokenSecret = bundle.getString("oauth.accessTokenSecret");
		} else {
			loggger.warn("Can't find property 'oauth.accessTokenSecret'.");
		}
		if (bundle.containsKey("tmp.dir")) {
			tmpDir = bundle.getString("tmp.dir");
		} else {
			loggger.warn("Can't find property 'tmp.dir'.");
		}
	}

	public static String getMyAccessToken() {
		return myAccessToken;
	}
	public static String getMyAccessTokenSecret() {
		return myAccessTokenSecret;
	}
	public static final String getTmpDir() {
		return tmpDir;
	}
}
