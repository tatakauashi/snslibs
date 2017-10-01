/**
 *
 */
package net.meiteampower.net.instagram;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.meiteampower.instagram.InstagramApi;
import net.meiteampower.instagram.entity.PostPage;
import net.meiteampower.instagram.entity.ProfilePage;
import net.meiteampower.util.ReshapeJson;

/**
 * @author kie
 *
 */
public class InstagramApiTest {

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

			String reshaped = new ReshapeJson().executeDetail(json, "\t");
			System.out.println(reshaped);

		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
	@Test
	public void testGetProfilePage() {

		try {
			ProfilePage actual = new InstagramApi().getProfilePage("sakai__mei");
			System.out.println("id: " + actual.getId());
			System.out.println("username: " + actual.getUsername());
			System.out.println("fullName: " + actual.getFullName());
			System.out.println("biography: " + actual.getBiography());
			System.out.println("followedBy: " + actual.getFollowedBy());
			System.out.println("follows: " + actual.getFollows());

		} catch (Exception e) {
			e.printStackTrace();
			fail();
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

			// 画像１つ。
			PostPage actual = new InstagramApi().getPostPage("BZgDev7DcIA");
			System.out.println("text: " + actual.getText());
			for (String url : actual.getDisplayUrls()) {
				System.out.println("display_url: " + url);
			}
			assertEquals(3, actual.getDisplayUrls().size());

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testGson() {

		try {
			String json = read("json.txt");
			String reshaped = new ReshapeJson().executeDetail(json, "\t");
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
