/**
 *
 */
package net.meiteampower.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author kie
 *
 */
public class InstagramUtils {

	private static String username = "";
	private static String password = "";
	private static String shortcodeLikeQueryId = "";
	private static String pageNextQueryId = "";
	private static String followerQueryId = "";
	private static String sakaiMeiAccountId = "";
	private static int newInitialCheckCount = 30;
	private static int newInitialCheckBackDays = 31;
	private static int continuousCheckBackDays = 20;
	private static long error502SleepTimeMillis = 300 * 1000;
	private static long error429SleepTimeMillis = 300 * 1000;
	private static long errorOthersSleepTimeMillis = 300 * 1000;

	static {

		ResourceBundle resourceBundle = ResourceBundle.getBundle(
				"instagramConfig", Locale.getDefault());

		if (resourceBundle.containsKey("instagram.username")) {
			username = resourceBundle.getString("instagram.username");
		}
		if (resourceBundle.containsKey("instagram.password")) {
			password = resourceBundle.getString("instagram.password");
		}
		if (resourceBundle.containsKey("shortcode.like.query_id")) {
			shortcodeLikeQueryId = resourceBundle.getString("shortcode.like.query_id");
		}
		if (resourceBundle.containsKey("page.next.query_id")) {
			pageNextQueryId = resourceBundle.getString("page.next.query_id");
		}
		if (resourceBundle.containsKey("follower.query_id")) {
			followerQueryId = resourceBundle.getString("follower.query_id");
		}
		if (resourceBundle.containsKey("sakai__mei.account_id")) {
			sakaiMeiAccountId = resourceBundle.getString("sakai__mei.account_id");
		}
		if (resourceBundle.containsKey("new.initial.check.count")) {
			newInitialCheckCount = Integer.parseInt(
					resourceBundle.getString("new.initial.check.count"));
		}
		if (resourceBundle.containsKey("new.initial.check.back.days")) {
			newInitialCheckBackDays = Integer.parseInt(
					resourceBundle.getString("new.initial.check.back.days"));
		}
		if (resourceBundle.containsKey("continuous.check.back.days")) {
			continuousCheckBackDays = Integer.parseInt(
					resourceBundle.getString("continuous.check.back.days"));
		}
		if (resourceBundle.containsKey("error.502.sleep.time.sec")) {
			error502SleepTimeMillis = Integer.parseInt(
					resourceBundle.getString("error.502.sleep.time.sec")) * 1000;
		}
		if (resourceBundle.containsKey("error.429.sleep.time.sec")) {
			error429SleepTimeMillis = Integer.parseInt(
					resourceBundle.getString("error.429.sleep.time.sec")) * 1000;
		}
		if (resourceBundle.containsKey("error.others.sleep.time.sec")) {
			errorOthersSleepTimeMillis = Integer.parseInt(
					resourceBundle.getString("error.others.sleep.time.sec")) * 1000;
		}

	}

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

	public static String getFollowerQueryId() {
		return followerQueryId;
	}

	public static String getSakaiMeiAccountId() {
		return sakaiMeiAccountId;
	}

	public static int getNewInitialCheckCount() {
		return newInitialCheckCount;
	}

	public static int getNewInitialCheckBackDays() {
		return newInitialCheckBackDays;
	}
	public static int getContinuousCheckBackDays() {
		return continuousCheckBackDays;
	}
	public static long getError502SleepTimeMillis() {
		return error502SleepTimeMillis;
	}
	public static long getError429SleepTimeMillis() {
		return error429SleepTimeMillis;
	}
	public static long getErrorOthersSleepTimeMillis() {
		return errorOthersSleepTimeMillis;
	}


	private static final DateTimeFormatter INSTANT_DATE_TIME_FORMATTER =
			DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	/**
	 * 指定した日時を日本時間の文字列にして返す。
	 * @param instant
	 * @return
	 */
	public static String getDateTimeString(Instant instant) {
		return LocalDateTime.ofInstant(instant, ZoneId.of("Asia/Tokyo"))
				.format(INSTANT_DATE_TIME_FORMATTER);
	}

	private static final SimpleDateFormat DATE_DATE_TIME_FORMATTER =
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 指定した日時を文字列にして返す。ロケーる変更は行わない。
	 * @param date
	 * @return
	 */
	public static String getDateTimeString(Date date) {
		return DATE_DATE_TIME_FORMATTER.format(date);
	}

	private static final DateTimeFormatter INSTANT_YYYYMMDD_FORMATTER =
			DateTimeFormatter.ofPattern("yyyyMMdd");
	/**
	 * 指定した「yyyyMMdd」形式の日付を日本時間のInstantに変換して返す。
	 * @param str yyyyMMdd 形式の日付
	 * @return
	 */
	public static Instant yyyyMMddToInstant(String str) {
		DateTimeFormatter formatter = INSTANT_YYYYMMDD_FORMATTER;
		long epochSecond = (LocalDate.parse(str, formatter)
				.toEpochDay() * 24 - 9) * 60 * 60;
		Instant result = Instant.ofEpochSecond(epochSecond);
		return result;
	}

}
