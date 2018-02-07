/**
 *
 */
package net.meiteampower.tweeter.db;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.meiteampower.db.factory.DBFactory;

/**
 * @author kie
 *
 */
public class DBAccessorTest {

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
	 * {@link net.meiteampower.tweeter.db.DBAccessor#getAccessToken(java.lang.String)} のためのテスト・メソッド。
	 */
	@Test
	public void testGetAccessToken() {

		String id = "796313650621317120";
		try {
			String[] actual = DBAccessor.getAccessToken(id);
			assertEquals("796313650621317120-QLM5A6TVUbAS0OB1lO5GxTA5QblRfE4", actual[0]);
			assertEquals("fSX6OQTvCs0bRC3saZdrzxjWyhZCKRafxC61P8OXctZLV", actual[1]);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testGetPassword() {

		String p = "9pdj4iJD08s";
		try {
			String password = DBFactory.getPassword(String.valueOf(Long.valueOf("14896486") % 796571L) + p);
			System.out.println("passowrd=" + password);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
