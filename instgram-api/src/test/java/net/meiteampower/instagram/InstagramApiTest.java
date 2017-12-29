package net.meiteampower.instagram;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.meiteampower.instagram.entity.PostPage;
import net.meiteampower.util.ReshapeJson;

public class InstagramApiTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * String getJson(String requestUrl)
	 */
	@Test
	public void testGetJson() {

		String shortcode = "BdCjERGhVx0";
		String urlTemplate = "https://www.instagram.com/p/%s/";
		try {
			//
			String url = String.format(urlTemplate, shortcode);
			InstagramApi api = new InstagramApi();
			Method method = InstagramApi.class.getDeclaredMethod("getJson", String.class);
			method.setAccessible(true);
			String json = (String)method.invoke(api, url);

			String writePath = "C:/tmp/" + shortcode + ".json.txt";
			try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(writePath))) {
				writer.write(new ReshapeJson().executeDetail(json));
			}

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testGetPostPageJson() {

		String shortcode = "BdCjERGhVx0";
		try {
			InstagramApi api = new InstagramApi();
			PostPage postPage = api.getPostPage(shortcode);
			assertNotNull(postPage);
			assertEquals(1, postPage.getDisplayUrls().size());
			assertEquals("https://scontent-nrt1-1.cdninstagram.com/t51.2885-15/e35/25008933_135552063900409_7370328098010562560_n.jpg", postPage.getDisplayUrls().get(0));

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
