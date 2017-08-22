package net.meiteampower.twitterapi;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TokenGeneratorTest {

	private static final Logger logger = Logger.getLogger(TokenGeneratorTest.class);

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


		String verifier = "test";
		AccessToken token = generator.getAccessToken(verifier);
		logger.error("AccessToken=" + token.toString());

	}

}
