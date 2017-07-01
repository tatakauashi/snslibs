/**
 *
 */
package net.meiteampower.twitterapi;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringEscapeUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.meiteampower.twitterapi.util.ReshapeJson;

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


		twParams.addQueryParam("id", "874221001717891072");
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

				String reshapedJson = new ReshapeJson().executeDetail(twData.getContent(), "\t");
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

				String reshapedJson = new ReshapeJson().executeDetail(twData.getContent(), "\t");
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
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
	private String getDateTimeString(Date date) {
		return DATE_TIME_FORMAT.format(date);
	}

	private String getDateTimeString() {
		return getDateTimeString(new Date());
	}
}
