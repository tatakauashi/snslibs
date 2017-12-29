package net.meiteampower.twitterapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuth2Authorization;
import twitter4j.auth.OAuth2Token;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;

/**
 * リクエストトークン、アクセストークンの取得を行う。
 * @author Kiyoshi
 */
public class TokenGenerator {

	private static final Logger logger = LoggerFactory.getLogger(TokenGenerator.class);

	private RequestToken req = null;
	private TwitterFactory factory;
	private Twitter twitter;

	public TokenGenerator() {
		factory = new TwitterFactory();
		twitter = factory.getInstance();
	}

	public OAuth2Token getOAuth2Token() throws TwitterException {
		Configuration conf = twitter.getConfiguration();
		OAuth2Authorization auth = new OAuth2Authorization(conf);
		return auth.getOAuth2Token();
	}

	public RequestToken getRequestToken() {

		req = null;
//		Configuration conf = ConfigurationContext.getInstance();

		try {
			req = twitter.getOAuthRequestToken("oob");
		} catch (TwitterException e) {
			e.printStackTrace();
			logger.error("リクエストトークンの生成に失敗しました。", e);
		}

		return req;
	}

	public AccessToken getAccessToken(String verifier) {
		AccessToken token = null;
		try {
			token = twitter.getOAuthAccessToken(req, verifier);
		} catch (TwitterException e) {
			e.printStackTrace();
			logger.error("アクセストークンの取得に失敗しました。", e);
		}

		return token;
	}
}
