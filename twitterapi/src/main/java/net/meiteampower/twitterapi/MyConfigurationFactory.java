package net.meiteampower.twitterapi;

import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * @author kie
 *
 */
public class MyConfigurationFactory {

    public static Configuration getConfiguration() {
    	return getConfiguration(null, null);
    }

    public static Configuration getConfiguration(String oauthAccessToken, String oauthAccessTokenSecret) {
    	ConfigurationBuilder cb = new ConfigurationBuilder();
    	cb.setDebugEnabled(true)
    		.setOAuthAccessToken(oauthAccessToken)
    		.setOAuthAccessTokenSecret(oauthAccessTokenSecret);
    	return cb.build();
    }

}
