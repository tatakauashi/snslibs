/**
 *
 */
package net.meiteampower.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author kie
 *
 */
public class NetUtilsTest {

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
	 * {@link net.meiteampower.util.NetUtils#download(java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	@Test
	public void testDownload() {

		String url = "https://video.twimg.com/ext_tw_video/930402778693996544/pu/vid/640x360/LF3xZBrZCr7iRZaD.mp4";
		String path = "C:/tmp/LF3xZBrZCr7iRZaD.mp4";
		try {
			NetUtils.download(url, path);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
