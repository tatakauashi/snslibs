package net.meiteampower.instagram.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import net.meiteampower.db.factory.DBFactory;
import net.meiteampower.instagram.entity.PostPage;
import net.meiteampower.util.InstagramUtils;

/**
 * @author kie
 *
 */
public class InstagramDao {

	public static List<InstagramAccount> getAccounts() throws Exception {

		List<InstagramAccount> resultList = new ArrayList<InstagramAccount>();
		try (Connection connection = DBFactory.getConnection()) {

			try (ResultSet resultSet = connection.createStatement().executeQuery(
					"SELECT account_id, username, exclution_flag, insert_time "
					+ " FROM in_accounts")) {

				while (resultSet.next()) {
					InstagramAccount account = new InstagramAccount();
					account.setAccountId(resultSet.getString("account_id"));
					account.setUsername(resultSet.getString("username"));
					account.setExclutionFlag(resultSet.getInt("exclution_flag") == 1);
					account.setInsertTime(resultSet.getTimestamp("insert_time"));
					resultList.add(account);
				}
			}
		}

		return resultList;
	}

	/**
	 * いいね！がついているshortcodeを確認する。
	 * @param shortcode
	 */
	public static boolean existsLiked(String shortcode)
			throws Exception {

		boolean result = false;
		try (Connection connection = DBFactory.getConnection()) {
			PreparedStatement ps = connection.prepareStatement(
					"SELECT * FROM in_liked_shortcodes "
					+ " WHERE shortcode = ? AND deleted_flag = 0 ");

			int index = 0;
			ps.setString(++index, shortcode);

			try (ResultSet resultSet = ps.executeQuery()) {
				if (resultSet.next()) {
					result = true;
				}
			}
		}

		return result;
	}

	public static Set<String> getLikedShortcodes(String accountId) throws Exception {

		Set<String> shortcodeSet = new HashSet<String>();

		try (Connection connection = DBFactory.getConnection()) {

			String sql = "SELECT shortcode FROM in_liked_shortcodes WHERE deleted_flag = 0 "
					+ (accountId != null ? " AND account_id = ? " : "");
			PreparedStatement ps = connection.prepareStatement(sql);

			if (accountId != null) {
				ps.setString(1, accountId);
			}

			try (ResultSet resultSet = ps.executeQuery()) {

				while (resultSet.next()) {
					shortcodeSet.add(resultSet.getString("shortcode"));
				}
			}
		}

		return shortcodeSet;
	}

	public static boolean isRegistgerdShortcode(String shortcode) throws Exception {

		try (Connection connection = DBFactory.getConnection()) {

			String sql = "SELECT shortcode FROM in_post_info WHERE shortcode = ? ";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, shortcode);

			try (ResultSet resultSet = ps.executeQuery()) {

				while (resultSet.next()) {
					return true;
				}
			}
		}

		return false;
	}

	public static Map<String, InstagramLastLiked> getLastLiked(String accountId) throws Exception {

		Map<String, InstagramLastLiked> shortcodeMap = new HashMap<String, InstagramLastLiked>();

		try (Connection connection = DBFactory.getConnection()) {

			PreparedStatement ps = connection.prepareStatement(
					"SELECT shortcode, revision, account_id, liked_account_id, checked_time "
							+ " FROM in_last_liked_shortcodes_view WHERE account_id = ? ");
			ps.setString(1, accountId);

			try (ResultSet resultSet = ps.executeQuery()) {

				while (resultSet.next()) {
					InstagramLastLiked instance = new InstagramLastLiked();
					String shortcode = resultSet.getString(1);
					instance.setShortcode(shortcode);
					instance.setRevision(resultSet.getInt(2));
					instance.setAccountId(resultSet.getString(3));
					instance.setLikedAccountId(resultSet.getString(4));
					instance.setCheckedTime(resultSet.getTimestamp(5));
					shortcodeMap.put(shortcode, instance);
				}
			}
		}

		return shortcodeMap;
	}

	public static void registerLastLiked(String shortcode, String accountId, String likedAccountId, Date checkedTime) throws Exception {

		InstagramLastLiked instance = new InstagramLastLiked();
		instance.setShortcode(shortcode);
		instance.setRevision(0);
		instance.setAccountId(accountId);
		registerLastLiked(instance, likedAccountId, checkedTime);
	}

	public static void registerLastLiked(InstagramLastLiked old, String likedAccountId, Date checkedTime) throws Exception {

		try (Connection connection = DBFactory.getConnection()) {
			PreparedStatement ps = connection.prepareStatement(
					"INSERT INTO in_last_liked_shortcodes ("
					+ " shortcode, revision, account_id, liked_account_id, checked_time, insert_time)"
					+ " VALUES (?, ?, ?, ?, ?, NOW())");

			int index = 0;
			ps.setString(++index, old.getShortcode());
			ps.setInt(++index, old.getRevision() + 1);
			ps.setString(++index, old.getAccountId());
			ps.setString(++index, likedAccountId);
			ps.setString(++index, InstagramUtils.getDateTimeString(checkedTime));

			ps.executeUpdate();
		}
	}

	public static List<PostPage> getPostInfo(String accountId) throws Exception {

		List<PostPage> resultList = new ArrayList<PostPage>();

		try (Connection connection = DBFactory.getConnection()) {

			String sql = "SELECT shortcode, account_id, post_text, display_url_json "
					+ " FROM in_post_info "
					+ (accountId != null ? " WHERE account_id = ? " : "");

			PreparedStatement ps = connection.prepareStatement(sql);
			if (accountId != null) {
				ps.setString(1, accountId);
			}

			try (ResultSet resultSet = ps.executeQuery()) {

				while (resultSet.next()) {
					PostPage instance = new PostPage();
					String shortcode = resultSet.getString(1);
					instance.setShortcode(shortcode);
					instance.setId(resultSet.getString(2));
					instance.setText(resultSet.getString(3));

					List<String> displayUrlList = new ArrayList<String>();
					JsonArray array = new Gson().fromJson(resultSet.getString(4), JsonArray.class);
					for (JsonElement elem : array) {
						displayUrlList.add(elem.getAsString());
					}
					instance.setDisplayUrls(displayUrlList);
					resultList.add(instance);
				}
			}
		}

		return resultList;
	}

	/**
	 * 投稿の情報を保存する。
	 * @param shortcode
	 * @param text
	 * @param displayUrls
	 * @throws Exception
	 */
	public static void registerPostInfo(String shortcode, String text,
			List<String> displayUrls) throws Exception {

		try (Connection connection = DBFactory.getConnection()) {
			connection.prepareStatement("SET NAMES utf8mb4").executeQuery();

			PreparedStatement ps = connection.prepareStatement(
					"INSERT INTO in_post_info ("
					+ " shortcode, post_text, display_url_json, pic_count, insert_time)"
					+ " VALUES (?, ?, ?, ?, NOW())");

			JsonArray array = new JsonArray();
			for (String fileName : displayUrls) {
				array.add(fileName);
			}
			String displayUrlJson = array.toString();

			int index = 0;
			ps.setString(++index, shortcode);
			ps.setString(++index, text == null ? "" : text);
			ps.setString(++index, displayUrlJson);
			ps.setInt(++index, displayUrls.size());

			ps.executeUpdate();
		}
	}

	/**
	 * いいね！がついていたshortcodeを登録する。
	 * @param shortcode
	 */
	public static void registerLiked(String accountId, String shortcode, String takenAtTimeString)
			throws Exception {

		try (Connection connection = DBFactory.getConnection()) {
			PreparedStatement ps = connection.prepareStatement(
					"INSERT INTO in_liked_shortcodes ("
					+ " shortcode, account_id, taken_at_time, tweet_flag, tweet_time, insert_time, deleted_flag)"
					+ " VALUES (?, ?, ?, 0, null, NOW(), 0)");

			int index = 0;
			ps.setString(++index, shortcode);
			ps.setString(++index, accountId);
			ps.setString(++index, takenAtTimeString);

			ps.executeUpdate();
		}
	}

	public static void insertAccount(String accountId, String username, String profilePicUrl, String profilePicUrlHd)
			throws Exception {

		try (Connection connection = DBFactory.getConnection()) {
			PreparedStatement ps = connection.prepareStatement(
					"INSERT INTO in_accounts ("
					+ " account_id, username, profile_pic_url, profile_pic_url_hd, exclution_flag, insert_time, update_time)"
					+ " VALUES (?, ?, ?, ?, 0, NOW(), NOW())");

			int index = 0;
			ps.setString(++index, accountId);
			ps.setString(++index, username);
			ps.setString(++index, profilePicUrl);
			ps.setString(++index, profilePicUrlHd);

			ps.executeUpdate();
		}
	}

	public static void updateAccount(String accountId, String username, String profilePicUrl, String profilePicUrlHd)
			throws Exception {

		try (Connection connection = DBFactory.getConnection()) {
			PreparedStatement ps = connection.prepareStatement(
					"UPDATE in_accounts SET username = ?, profile_pic_url = ?, profile_pic_url_hd = ?, update_time = NOW() "
					+ " WHERE account_id = ? ");

			int index = 0;
			ps.setString(++index, username);
			ps.setString(++index, profilePicUrl);
			ps.setString(++index, profilePicUrlHd);
			ps.setString(++index, accountId);

			ps.executeUpdate();
		}
	}

	public static void insertPostAddInfo(String shortcode, List<String> list) throws Exception {

		try (Connection connection = DBFactory.getConnection()) {
			PreparedStatement ps = connection.prepareStatement(
					"INSERT INTO in_post_add_info ("
					+ " shortcode, revision, price, description, detail_url_json, insert_time)"
					+ " VALUES (?, 1, 0, '', ?, NOW())");

			JsonArray array = new JsonArray();
			for (String fileName : list) {
				array.add(fileName);
			}
			int index = 0;
			ps.setString(++index, shortcode);
			ps.setString(++index, array.toString());

			ps.executeUpdate();
		}
	}
}
