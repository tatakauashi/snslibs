/**
 *
 */
package net.meiteampower.twitterapi;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.meiteampower.twitterapi.data.Status;
import net.meiteampower.twitterapi.data.User;
import net.meiteampower.util.ReshapeJson;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * @author tatak
 *
 */
public class TwitterApiAccessorTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {

		String method = "GET";
		String resourceUrl = "https://api.twitter.com/1.1/lists/statuses.json";
		TwParams twParams = new TwParams();
		TwData twData = new TwData();

		twParams.addQueryParam("owner_screen_name", "kiyoshimeiteam");
		twParams.addQueryParam("slug", "list4");
		twParams.addQueryParam("count", "200");
		twParams.addQueryParam("include_entities", "false");
		twParams.addQueryParam("include_rts", "false");
		twParams.addQueryParam("max_id", (871356018629910530L - 1) + "");
		try {
			boolean actual = TwitterApiAccessor.getInstance().execute(method, resourceUrl, twParams, twData);
			assertTrue(actual);

			assertEquals(200, twData.getResponseCode());
			String str = StringEscapeUtils.unescapeJava(twData.getContent());

			String filePath = "lists_list4_" + getDateTimeString() + ".txt";
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"));
				writer.write(str);

			} finally {
				if (writer != null) {
					writer.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void getStatusById() {

		String method = "GET";
		String resourceUrl = "https://api.twitter.com/1.1/statuses/show.json";
		TwParams twParams = new TwParams();
		TwData twData = new TwData();


		twParams.addQueryParam("id", "944370349386342400");	//"907219849876615168");//"930404247174225920");
		twParams.addQueryParam("trim_user", "false");
		twParams.addQueryParam("include_my_retweet", "true");
		twParams.addQueryParam("include_entities", "true");
		twParams.addQueryParam("include_ext_alt_text", "true");
//		twParams.addQueryParam("max_id", (871356018629910530L - 1) + "");

		try {
			boolean actual = TwitterApiAccessor.getInstance().execute(method, resourceUrl, twParams, twData);
			assertTrue(actual);

			assertEquals(200, twData.getResponseCode());
			String str = StringEscapeUtils.unescapeJava(twData.getContent());

			String filePath = "status_" + twParams.getQueryParams().get("id") + "_" + getDateTimeString() + ".txt";
			BufferedWriter writer = null;
			String filePathRaw = "status_" + twParams.getQueryParams().get("id") + "_Raw_" + getDateTimeString() + ".txt";
			BufferedWriter writerRaw = null;
			try {
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"));
				writer.write(str);

				String reshapedJson = new ReshapeJson().executeDetail(twData.getContent());
				writerRaw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePathRaw), "UTF-8"));
				writerRaw.write(reshapedJson);

			} finally {
				if (writer != null) {
					writer.close();
				}
				if (writerRaw != null) {
					writerRaw.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}


	@Test
	public void getUserShow() {

		String method = "GET";
		String resourceUrl = "https://api.twitter.com/1.1/users/show.json";
		TwParams twParams = new TwParams();
		TwData twData = new TwData();


//		twParams.addQueryParam("id", "874221001717891072");
		twParams.addQueryParam("screen_name", "mei__sakai");

		try {
			boolean actual = TwitterApiAccessor.getInstance().execute(method, resourceUrl, twParams, twData);
			assertTrue(actual);

			assertEquals(200, twData.getResponseCode());
			String str = StringEscapeUtils.unescapeJava(twData.getContent());

			String filePath = "user_show_" + twParams.getQueryParams().get("id") + "_" + getDateTimeString() + ".txt";
			BufferedWriter writer = null;
			String filePathRaw = "user_show_" + twParams.getQueryParams().get("id") + "_Raw_" + getDateTimeString() + ".txt";
			BufferedWriter writerRaw = null;
			try {
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"));
				writer.write(str);

				String reshapedJson = new ReshapeJson().executeDetail(twData.getContent());
				writerRaw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePathRaw), "UTF-8"));
				writerRaw.write(reshapedJson);

			} finally {
				if (writer != null) {
					writer.close();
				}
				if (writerRaw != null) {
					writerRaw.close();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void getUserTimeline() {

		String method = "GET";
		String resourceUrl = "https://api.twitter.com/1.1/statuses/user_timeline.json";
		TwParams twParams = new TwParams();
		TwData twData = new TwData();


//		twParams.addQueryParam("id", "874221001717891072");
		twParams.addQueryParam("screen_name", "mei__sakai");
		twParams.addQueryParam("count", "200");
		// tweet_mode=extended
		twParams.addQueryParam("tweet_mode", "extended");

		try {
			boolean actual = TwitterApiAccessor.getInstance().execute(method, resourceUrl, twParams, twData);
			assertTrue(actual);

			assertEquals(200, twData.getResponseCode());
//			String content = StringEscapeUtils.unescapeJava(twData.getContent());
			String content = twData.getContent();

			String reshapedJson = new ReshapeJson().executeDetail(content);
			System.out.println(reshapedJson);

			String filePath = "results/" + "mei__sakai_" + getDateTimeString() + ".txt";
			try (BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream(filePath), "UTF-8"))) {
				writer.write(reshapedJson);
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void getUserTimelineToStatuses() {

		String method = "GET";
		String resourceUrl = "https://api.twitter.com/1.1/statuses/user_timeline.json";
		TwParams twParams = new TwParams();
		TwData twData = new TwData();


//		twParams.addQueryParam("id", "874221001717891072");
		twParams.addQueryParam("screen_name", "mei__sakai");
		twParams.addQueryParam("count", "2");
		// tweet_mode=extended
		twParams.addQueryParam("tweet_mode", "extended");

		Configuration conf = TwitterApiAccessor.getInstance().getDefaultConfiguration();
		twParams.addRequestHeader("Authorization", conf.getOAuth2TokenType() + " "
				+ conf.getOAuth2AccessToken());

		try {
			boolean actual = TwitterApiAccessor.getInstance().execute(method, resourceUrl, twParams, twData);
			assertTrue(actual);

			assertEquals(200, twData.getResponseCode());
//			String content = StringEscapeUtils.unescapeJava(twData.getContent());
			String content = twData.getContent();

			JsonArray jsonArray = new Gson().fromJson(content, JsonArray.class);
			List<Status> statusList = new ArrayList<Status>();
			for (JsonElement elem : jsonArray) {
				statusList.add(new Status(elem.getAsJsonObject()));

				// toString() でJSON文字列で取得可能。しかし、テキストはデコードされている。
				System.out.println(elem.toString());

				String json = elem.toString();
				JsonObject statusObject = new Gson().fromJson(json, JsonObject.class);
				System.out.println("full_text=" + statusObject.get("full_text").getAsString());

				User user = new User(statusObject.get("user").toString());
				System.out.println("------------------------- User");
				System.out.println("id         : " + user.getUserId());
				System.out.println("name       : " + user.getName());
				System.out.println("screen_name: " + user.getScreenName());
			}

//			for (Status status : statusList) {
//				System.out.println("--------------------------------");
//				System.out.println(status.getCreatedAt());
//				System.out.println(status.getText());
//				List<String> mediaUrls = status.getMediaUrlHttpsList();
//				if (mediaUrls != null) {
//					for (String mediaUrl : mediaUrls) {
//						System.out.println(mediaUrl);
//					}
//				}
//			}

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testConfigurationBuilder() {

		try {
			String oauthAccessToken = "oauthAccessToken123";
			String oauthAccessTokenSecret = "oauthAccessTokenSecretABC";

			ConfigurationBuilder cb = new ConfigurationBuilder();
	    	cb.setDebugEnabled(true)
	    		.setOAuthAccessToken(oauthAccessToken)
	    		.setOAuthAccessTokenSecret(oauthAccessTokenSecret);
			Configuration configuration = cb.build();

			assertEquals("rAqv6oAZcbDUqYTs0mmK1wEre", configuration.getOAuthConsumerKey());
			assertEquals("aeNLNW0OEmoMiP3TUg5Vw1emHFMPZNMyKr15poUANW9D7fDHU5", configuration.getOAuthConsumerSecret());
			assertEquals(oauthAccessToken, configuration.getOAuthAccessToken());
			assertEquals(oauthAccessTokenSecret, configuration.getOAuthAccessTokenSecret());

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testConfigurationBuilder2() {

		try {
			ConfigurationBuilder cb = new ConfigurationBuilder();
	    	cb.setDebugEnabled(true);
			Configuration configuration = cb.build();

			assertEquals("rAqv6oAZcbDUqYTs0mmK1wEre", configuration.getOAuthConsumerKey());
			assertEquals("aeNLNW0OEmoMiP3TUg5Vw1emHFMPZNMyKr15poUANW9D7fDHU5", configuration.getOAuthConsumerSecret());
			assertNull(configuration.getOAuthAccessToken());
			assertNull(configuration.getOAuthAccessTokenSecret());
			assertEquals("bearer", configuration.getOAuth2TokenType());
			assertEquals("AAAAAAAAAAAAAAAAAAAAALsv0wAAAAAAh35UX0NY9wntfOG/92nIZuR5NIs=O6114CDTfy7N6LhRIprSgfTDHnWcxrxYVZhmAUU2K16XjPHku3", configuration.getOAuth2AccessToken());
			assertTrue(configuration.isApplicationOnlyAuthEnabled());

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	@Ignore
	public void testTwitter() {

		try {
			Twitter twitter = TwitterApiAccessor.getInstance().getTwitter();
			ResponseList<twitter4j.Status> userTimeline = twitter.getUserTimeline("mei__sakai");
			for (twitter4j.Status status : userTimeline) {
				System.out.println(status.getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
	private String getDateTimeString(Date date) {
		return DATE_TIME_FORMAT.format(date);
	}

	private String getDateTimeString() {
		return getDateTimeString(new Date());
	}
}
