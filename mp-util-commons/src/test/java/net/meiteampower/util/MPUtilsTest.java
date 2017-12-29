package net.meiteampower.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MPUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetExtension() {
		fail("まだ実装されていません");
	}

	@Test
	public void testModifyInstagramText() {

		String text = "12345678901234567890";
		try {
			String actual = MPUtils.modifyInstagramText(text, 20);
			assertEquals("12345678901234567890", actual);

			text = "123456789012345678901";
			actual = MPUtils.modifyInstagramText(text, 20);
			assertEquals("1234567890123456 ...", actual);

			text = "12345678 @hogehoge 901234567890";
			actual = MPUtils.modifyInstagramText(text, 20);
			assertEquals("12345678 9012345 ...", actual);

			text = "12345678 @hogehoge 90123 @beebee 4567890";
			actual = MPUtils.modifyInstagramText(text, 20);
			assertEquals("12345678 90123 4 ...", actual);

			text = "12345678 @hogehoge 90123456789";
			actual = MPUtils.modifyInstagramText(text, 20);
			assertEquals("12345678 90123456789", actual);

			text = "12345678\n@hogehoge\n90123\n@beebee\n4567890";
			actual = MPUtils.modifyInstagramText(text, 20);
			assertEquals("12345678 90123 4 ...", actual);

			text = "1234567890 #123456789";
			actual = MPUtils.modifyInstagramText(text, 20);
			assertEquals("1234567890 ...", actual);

			text = "1234567890 \n #123456789";
			actual = MPUtils.modifyInstagramText(text, 20);
			assertEquals("1234567890 ...", actual);

			text = "12345678901234 #67890";
			actual = MPUtils.modifyInstagramText(text, 20);
			assertEquals("12345678901234 ...", actual);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
