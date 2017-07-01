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
//			File in = new File("17265487_809689225851815_4672782864515858432_n.jpg");
//			File out = new File("17265487_809689225851815_4672782864515858432_n_p50.jpg");
			File in = new File("IMG_4540.JPG");
			File out = new File("IMG_4540_thumb.JPG");
			Thumbnail.scaleImage(in, out, .7);

			assertTrue(out.exists());

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
