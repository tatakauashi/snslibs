/**
 *
 */
package net.meiteampower.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kie
 *
 */
public class CryptoTest {

	private static final Logger logger = LoggerFactory.getLogger(CryptoTest.class);

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
	 * {@link net.meiteampower.util.Crypto#encrypt(java.lang.String, java.lang.String)} のためのテスト・メソッド。
	 */
	@Test
	public void testEncrypt() {

		String plain = "ブラックピンク！";
		try {
			String encrypted = Crypto.encrypt(plain, "ABCDE");
			logger.debug("encrypted=[{}]", encrypted);

			String decrypted = Crypto.decrypt(encrypted, "ABCDE");
			assertEquals(plain, decrypted);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
