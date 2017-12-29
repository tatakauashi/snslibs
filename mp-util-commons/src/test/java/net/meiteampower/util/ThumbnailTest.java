/**
 *
 */
package net.meiteampower.util;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.time.Instant;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author kie
 *
 */
public class ThumbnailTest {

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
	 * {@link net.meiteampower.util.Thumbnail#scaleImage(java.lang.String, java.lang.String, double, int, int, java.lang.String)} のためのテスト・メソッド。
	 */
	@Test
	public void testScaleImageStringStringDoubleIntIntString() {

		String fromFilePath = "C:/Users/kie/Desktop/DNjDw1aVoAEjeya.jpg";
		String toFilePath = "C:/Users/kie/Desktop/DNjDw1aVoAEjeya_thumb_" + Instant.now().getEpochSecond() + ".jpg";
		double scale = 0.7;
		int toWidth = 300;
		int toHeight = 600;
		String ext = "png";
		try {
			Thumbnail.scaleImage(fromFilePath, toFilePath, scale, toWidth, toHeight, ext);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testScaleImageStringStringDoubleString() {

		String fromFilePath = "C:/Users/kie/Desktop/DNjDw1aVoAEjeya.jpg";
		String toFilePath = "C:/Users/kie/Desktop/DNjDw1aVoAEjeya_thumb_" + Instant.now().getEpochSecond() + ".jpg";
		double scale = 0.7;
		String ext = null;
		try {
			Thumbnail.scaleImage(fromFilePath, toFilePath, scale, ext);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testDrawString() {

		String baseFilePath = "C:/Users/kie/git2/instgram-api/src/main/images/instagram_base.png";
		String s = "instagram.com/sakai__mei";
		String toFilePath = "C:/tmp/instagram_sakai__mei.png";
		int x = 52;
		int y = 28;
		try {
			InputStream is = new FileInputStream(baseFilePath);
			Thumbnail.drawString(is, s, x, y, toFilePath);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
