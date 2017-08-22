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
			String fileName = desktop + "20838389_730783773789574_6843403389670260736_n.jpg";
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

}
