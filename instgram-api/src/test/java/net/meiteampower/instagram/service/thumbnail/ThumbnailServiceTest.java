package net.meiteampower.instagram.service.thumbnail;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.meiteampower.instagram.InstagramApi;
import net.meiteampower.instagram.entity.PostPage;

public class ThumbnailServiceTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGet() {

		String shortcode = "BbeP6mABi__";
		String writePath = "C:/tmp/";
//		String footerImgFilePath = "src/main/images/instagram_sakai__mei.png";
		try {
			InstagramApi api = new InstagramApi();
			PostPage postPage = api.getPostPage(shortcode);

			ThumbnailService service = new ThumbnailService(postPage);

			ThumbnailParameter param = new ThumbnailParameter();
			ThumbnailData data = new ThumbnailData();

			param.setShortcode(shortcode);
			param.setWritePath(writePath);
//			param.setFooterImgFilePath(footerImgFilePath);

			service.get(param, data);

			assertNotNull(data.getFilePath());

			System.out.println("postPage.getFullName()=" + postPage.getFullName());
			System.out.println("postPage.getText()=" + postPage.getText());

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
