/**
 *
 */
package net.meiteampower.net.instagram;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.meiteampower.instagram.InstagramApi;
import net.meiteampower.instagram.entity.FreqController;
import net.meiteampower.instagram.entity.PostPage;
import net.meiteampower.instagram.entity.ProfilePage;
import net.meiteampower.instagram.entity.QueryResponse;
import net.meiteampower.instagram.entity.Update;
import net.meiteampower.util.InstagramUtils;
import net.meiteampower.util.NetUtils;
import net.meiteampower.util.ReshapeJson;

/**
 * @author kie
 *
 */
public class InstagramApiTest {

	private static final Logger logger = LoggerFactory.getLogger(InstagramApiTest.class);
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

	/**
	 * {@link net.meiteampower.instagram.InstagramApi#getProfileJson(java.lang.String)} のためのテスト・メソッド。
	 */
	@Test
	public void testGetJson() {

		try {
			String json = new InstagramApi().getProfilePageJson("sakai__mei");
			System.out.println("json=" + json);
			assertFalse("".equals(json));

			String reshaped = new ReshapeJson().executeDetail(json);
			System.out.println(reshaped);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testGetProfilePage() {

		InstagramApi api = null;

		try {
			api = new InstagramApi();
			api.getFrontPage();
			api.login();
//			ProfilePage actual = api.getProfilePage("sakai__mei");
//			System.out.println("id: " + actual.getId());
//			System.out.println("username: " + actual.getUsername());
//			System.out.println("fullName: " + actual.getFullName());
//			System.out.println("biography: " + actual.getBiography());
//			System.out.println("followedBy: " + actual.getFollowedBy());
//			System.out.println("follows: " + actual.getFollows());
//
//			System.out.println("update_count: " + actual.getUpdateCount());
//			System.out.println("has_next_page: " + actual.isHasNextPage());
//			System.out.println("end_cursor: " + actual.getEndCursor());

//			// 2ページ目
//			FreqController freqCon = new FreqController();
//			api.getPageNext(actual.getId(), actual.getEndCursor(), freqCon);

			FreqController freqCon = new FreqController();
//			QueryResponse response = api.getByQuery(InstagramUtils.getFollowerQueryId(),
//					"id", actual.getId(), null, freqCon);
			QueryResponse response = new QueryResponse();
			api.getByQuery(InstagramUtils.getFollowerQueryId(),
					"id", "5621611953", null, freqCon, response);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			if (api != null) {
				try {
					api.logout();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Test
	public void testGetPostPage() {

		try {
//			// 画像３つ。
//			PostPage actual = new InstagramApi().getPostPage("BYFv8mwFq8H");
//			System.out.println("text: " + actual.getText());
//			for (String url : actual.getDisplayUrls()) {
//				System.out.println("display_url: " + url);
//			}
//			assertEquals(3, actual.getDisplayUrls().size());

			InstagramApi instagramApi = new InstagramApi();
			// 画像２つ。
			PostPage actual = instagramApi.getPostPage("BagdrIdhPl0");
			System.out.println("text: " + actual.getText());
			for (String url : actual.getDisplayUrls()) {
				System.out.println("display_url-1: " + url);
			}
//			assertEquals(2, actual.getDisplayUrls().size());

			// 画像１つ。
			actual = instagramApi.getPostPage("BbWnW9Yh7U5");
			System.out.println("text: " + actual.getText());
			for (String url : actual.getDisplayUrls()) {
				System.out.println("display_url-2 : " + url);
			}
//			assertEquals(1, actual.getDisplayUrls().size());

			Thread.sleep(10000);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testGetAndRegisterPostPage() {

		String shortcode = "BbYi4JYAeFk";
		try {
			InstagramApi instagramApi = new InstagramApi();
			// 画像２つ。
			PostPage actual = instagramApi.getPostPage(shortcode);
			System.out.println("text: " + actual.getText());
			int i = 0;
			for (String url : actual.getDisplayUrls()) {
				System.out.println("display_url-" + (++i) + ": " + url);
			}

			PostPage postInfo = new PostPage();
			postInfo.setShortcode(shortcode);
			postInfo.setId(actual.getId());
			postInfo.setText(actual.getText());

			List<String> list = new ArrayList<String>();
			for (String url : actual.getDisplayUrls()) {
				list.add(url);
			}
			postInfo.setDisplayUrls(list);

//			InstagramDao.registerPostInfo(postInfo);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testCheckLikeds() {

		String shortcode = "BbZDE0alhGr";
		String endCursor = null;
		FreqController freqCon = new FreqController();
		freqCon.setFirstSize(1000);
		QueryResponse response = new QueryResponse();

		try {
			InstagramApi api = new InstagramApi();
			api.getShortcodeLikeUsers(shortcode, endCursor, freqCon, response);
			String content = response.getJson();
			System.out.println("■■■First Json");
			System.out.println(content);
			logger.debug("■■■First Json:::" + content);

			// 30秒まつ。
			Thread.sleep(10000);

			// もう一度LIKEを取得する
			api.getShortcodeLikeUsers(shortcode, endCursor, freqCon, response);
			content = response.getJson();
			logger.debug("■■■Next  Json:::" + content);
			System.out.println(content);


		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testGetProfilePageJson() {

		try {
//			// 画像３つ。
//			PostPage actual = new InstagramApi().getPostPage("BYFv8mwFq8H");
//			System.out.println("text: " + actual.getText());
//			for (String url : actual.getDisplayUrls()) {
//				System.out.println("display_url: " + url);
//			}
//			assertEquals(3, actual.getDisplayUrls().size());

			// 画像１つ。
			String actual = new InstagramApi().getProfilePageJson("dholic_official");
			System.out.println(actual);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testGson() {

		try {
			String json = read("json.txt");
			String reshaped = new ReshapeJson().executeDetail(json);
			System.out.println(reshaped);

			JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);

			String latestId =jsonObject.get("entry_data").getAsJsonObject()
				.get("ProfilePage").getAsJsonArray()
				.get(0).getAsJsonObject()
				.get("user").getAsJsonObject()
				.get("media").getAsJsonObject()
				.get("nodes").getAsJsonArray()
				.get(0).getAsJsonObject()
				.get("id").getAsString();
			assertEquals("1583393128785491133", latestId);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testSavePostPics() {

		String shortcode = "BcjxSXdBngH";

		try {
			InstagramApi api = new InstagramApi();

			PostPage postPage = api.getPostPage(shortcode);
			downloadPics(postPage);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testSaveUserPics() {

		String username = "ryoha1009";

		try {
			InstagramApi api = new InstagramApi();

			ProfilePage profilePage = api.getProfilePage(username, Instant.now().minusSeconds(30 * 24 * 60 * 60));

			for (Update u : profilePage.getUpdateList()) {
				PostPage postPage = api.getPostPage(u.getShortcode());

				downloadPics(postPage);
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	private void downloadPics(PostPage postPage) throws Exception {

		int count = 0;
		String dateTimeString = LocalDateTime.ofInstant(postPage.getTakenAtTimestamp(), ZoneId.of("Asia/Tokyo")).format(
				DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		for (String url : postPage.getDisplayUrls()) {
			int extIndex = url.lastIndexOf('.');
			String ext = url.substring(extIndex);

			count++;
			// mp-utilを使用して写真ファイルをダウンロードする。
			String path = InstagramUtils.getPicDir()
					+ "/" + postPage.getUsername() + "/" + dateTimeString + "-" + count + ext;
			NetUtils.download(url, path);
		}
	}

	private String read(String filePath) throws IOException {

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF-8"));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append("\r\n");
			}
			return sb.toString();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
}
