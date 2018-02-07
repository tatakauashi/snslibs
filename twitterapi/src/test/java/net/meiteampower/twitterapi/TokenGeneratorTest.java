package net.meiteampower.twitterapi;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuth2Token;
import twitter4j.auth.RequestToken;

public class TokenGeneratorTest {

	private static final Logger logger = LoggerFactory.getLogger(TokenGeneratorTest.class);

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {

		TokenGenerator generator = new TokenGenerator();
		RequestToken req = generator.getRequestToken();
		logger.error("req=" + req.toString());

		String url = req.getAuthorizationURL();
		logger.error("authorization_url=" + url);


		String verifier = "1638246";
		AccessToken token = generator.getAccessToken(verifier);
		logger.error("AccessToken=" + token.toString());
		logger.error("AccessToken.getToken()=" + token.getToken().toString());
		logger.error("AccessToken.getTokenSecret=" + token.getTokenSecret().toString());

	}

	/**
	 * ベアラートークンを取得するテスト。
	 */
	@Test
	public void testGetOAuth2Token() {

		try {
			TokenGenerator generator = new TokenGenerator();
			OAuth2Token actual = generator.getOAuth2Token();
			System.out.println("OAuth2Token=" + actual.getAccessToken());

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
