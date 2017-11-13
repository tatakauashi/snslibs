package net.meiteampower.instagram.main;

import static org.junit.Assert.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.meiteampower.util.InstagramUtils;

public class LikeDiggerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testExecNewProcedure() {

		LikeDigger digger = new LikeDigger();
		try {
			digger.init();

			digger.execNewProcedure("3314481266", "oioikorea");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			try {
				digger.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testExecContinuousProcedure() {

		LikeDigger digger = new LikeDigger();
		try {
			digger.init();

			digger.execContinuousProcedure("oioikorea");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			try {
				digger.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testCheckLikedUpdate() {

		LikeDigger digger = new LikeDigger();
		try {
			digger.init();

			digger.checkLikedUpdate("4479380684", "oioikorea", "BaT4rxmBCOP", Instant.now(), null);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			try {
				digger.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testInstantParse() {

		try {
//			Instant actual = Instant.parse("2017-11-12");
//			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss", Locale.JAPAN);
//			long epochSecond = LocalDateTime.parse("20171113" + "000000", formatter)
////					.toEpochSecond(ZoneOffset.ofHours(9));
//					.toEpochSecond(ZoneOffset.ofHours(0));
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
			long epochSecond = LocalDate.parse("20171113", formatter)
					.toEpochDay() * 24 * 60 * 60;
			Instant actual = Instant.ofEpochSecond(epochSecond);
			System.out.println("Instant=" + actual.toString());
			System.out.println("Instant=" + InstagramUtils.getDateTimeString(actual));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
