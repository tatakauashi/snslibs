package net.meiteampower.tweeter;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.meiteampower.instagram.InstagramApi;
import net.meiteampower.instagram.entity.PostPage;
import net.meiteampower.instagram.service.post.PostService;
import net.meiteampower.twitterapi.TokenGenerator;
import net.meiteampower.twitterapi.service.status.StatusService;
import twitter4j.auth.OAuth2Token;

/**
 * Unit test for simple App.
 */
public class AppTest {

	private static final Logger logger = LoggerFactory.getLogger(AppTest.class);

	@Before
	public void setUp() {

	}

	@After
	public void tearDown() {

	}

	@Test
	public void test() {

		try {
			InstagramApi api = new InstagramApi();
			PostService service = new PostService(api);
			List<PostPage> list = service.get("nhk_nw9", 1440);
			for (PostPage postPage : list) {
				logger.debug("postPage.getTakenAtTimestamp()=" + postPage.getTakenAtTimestamp());
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testTweet() {

		try {
			StatusService service = new StatusService(
					MyConfig.getInstance().getMyAccessToken(),
					MyConfig.getInstance().getMyAccessTokenSecret());
			List<String> list = new ArrayList<String>();
			list.add("c:/tmp/1987919308998164341_1514295657952.jpg.layered.jpg");

			service.tweet("ヲトメ噺でのおでこだし。", list);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testGetOauth2Token() {

		try {
			TokenGenerator generator = new TokenGenerator();
			OAuth2Token oAuth2Token = generator.getOAuth2Token();
			logger.info("OAuth2Token=" + oAuth2Token.getAccessToken());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
