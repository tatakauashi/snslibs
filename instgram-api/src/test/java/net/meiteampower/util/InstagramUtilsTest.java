package net.meiteampower.util;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

public class InstagramUtilsTest {

	private static final Logger logger = LoggerFactory.getLogger(InstagramUtilsTest.class);

	@Before
	public void setUp() throws Exception {
		logger.debug("*** テスト開始");
	}

	@After
	public void tearDown() throws Exception {
		logger.debug("*** テスト終了");
	}

	@Test
	public void test() {

		try {
			/*
	public static String getUsername() {
		return username;
	}

	public static String getPassword() {
		return password;
	}

	public static String getShortcodeLikeQueryId() {
		return shortcodeLikeQueryId;
	}

	public static String getPageNextQueryId() {
		return pageNextQueryId;
	}

				 *
				 */
			assertEquals("kiyoshimeiteam", InstagramUtils.getUsername());
			assertEquals("Juce4juCe4", InstagramUtils.getPassword());
			assertEquals("17864450716183058", InstagramUtils.getShortcodeLikeQueryId());
			assertEquals("17888483320059182", InstagramUtils.getPageNextQueryId());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testyyyyMMddToInstant() {

		try {
			Instant actual = InstagramUtils.yyyyMMddToInstant("20171213");
			assertEquals("2017-12-13 00:00:00", InstagramUtils.getDateTimeString(actual));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testJson() {

		try {
			JsonArray array = new Gson().fromJson("[\"http:\\/\\/www.hoge.com\\/\"]", JsonArray.class);
			assertEquals(1, array.size());
			assertEquals("http://www.hoge.com/", array.get(0).getAsString());

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
