package net.meiteampower.twitterapi;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;

/**
 * @author kie
 *
 */
public class MyTwitterFactory {

	public static Twitter getInstance() {
		return new TwitterFactory().getInstance();
	}

	public static Twitter getInstance(String oauthAccessToken, String oauthAccessTokenSecret) {
		Configuration conf = MyConfigurationFactory.getConfiguration(
				oauthAccessToken, oauthAccessTokenSecret);
		return new TwitterFactory(conf).getInstance();
	}
}
