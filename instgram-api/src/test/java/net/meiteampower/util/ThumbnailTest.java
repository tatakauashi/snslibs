/**
 *
 */
package net.meiteampower.util;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author kie
 *
 */
public class ThumbnailTest {

	private static final String IMAGE_IN_PATH = "src/test/images/in/";
	private static final String IMAGE_OUT_PATH = "src/test/images/out/";

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
	 * {@link net.meiteampower.util.Thumbnail#scaleImage(java.io.File, java.io.File, double)} のためのテスト・メソッド。
	 */
	@Test
	public void testScaleImage() {

		try {
			String desktop = "C:\\Users\\kie\\Desktop\\";
//			String fileName = desktop + "20170711_large.jpg";//20838389_730783773789574_6843403389670260736_n.jpg
			String fileName = desktop + "21910919_750140235195564_3632004240202792960_n.jpg";
			String thumbnailName = fileName + ".thumb.jpg";
			File in = new File(fileName);
			File out = new File(thumbnailName);
			Thumbnail.scaleImage(in, out, .7);

			assertTrue(out.exists());

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testLayer() {

		try {
//			File imagesToDir = new File(IMAGE_OUT_PATH);
//			for (File f : imagesToDir.listFiles()) {
//				if (f.isFile()) {
//					f.delete();
//				}
//			}

			String fileName = "25018115_533421133690209_120412436095303680_n.jpg";
			String fromPath = IMAGE_IN_PATH + fileName;
//			String layerPath = "src/main/images/instagram_sakai__mei.png";
			String layerPath = "src/main/images/instagram_saki_tkhs.png";
			String intermidiatePath = IMAGE_OUT_PATH + fileName + ".inter.png";
//			String instaLogoPath = IMAGE_IN_PATH + "insta_logo.png";
			String toPath = IMAGE_OUT_PATH + fileName + ".layered.png";

			Thumbnail.scaleImage(fromPath, intermidiatePath, 1.0, 320, 320, null);
			Thumbnail.layer(intermidiatePath, layerPath, toPath, 0.70F);
//			Thumbnail.layer(layerPath, instaLogoPath, toPath, 1.0F);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
