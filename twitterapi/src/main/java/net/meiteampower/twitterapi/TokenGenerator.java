package net.meiteampower.twitterapi;

import org.apache.log4j.Logger;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * リクエストトークン、アクセストークンの取得を行う。
 * @author Kiyoshi
 */
public class TokenGenerator {

	private static final Logger logger = Logger.getLogger(TokenGenerator.class);

	private RequestToken req = null;
	private TwitterFactory factory;
	private Twitter twitter;

	public TokenGenerator() {
		factory = new TwitterFactory();
		twitter = factory.getInstance();
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
