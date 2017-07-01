/**
 *
 */
package net.meiteampower.twbatch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import net.meiteampower.db.factory.DBFactory;
import net.meiteampower.twitterapi.TwData;
import net.meiteampower.twitterapi.TwParams;
import net.meiteampower.twitterapi.TwitterApiAccessor;
import net.meiteampower.twitterapi.data.User;

/**
 * @author tatak
 *
 */
public class UserRegister {

	private static final Logger logger = Logger.getLogger(UserRegister.class);
	private static final java.util.Random random = new java.util.Random();

	public static void main(String[] args) {
		logger.info("[user/show] 取得処理 開始");
		try {
			new UserRegister().execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("[user/show] 取得処理 終了");
	}

	public UserRegister() {
	}

	public void execute() throws Exception {

		Connection connection = null;
		try {
			connection = DBFactory.getConnection();
			Map<String, String> userMap = getUserIds(connection);

			for (Map.Entry<String, String> entry : userMap.entrySet()) {
				logger.debug("[user/show] TwitterAPI 開始 screen_name=" + entry.getValue());
				User user = getUserById(entry.getKey());
				logger.debug("[user/show] TwitterAPI 成功 screen_name=" + entry.getValue());

				logger.debug("[user/show] 保存処理 開始 screen_name=" + user.getScreenName());

				saveToTable(connection, user);
				logger.debug("[user/show] 保存処理 成功 screen_name=" + user.getScreenName());

				Thread.sleep(1000L + random.nextInt(2000));
			}

		} catch (Exception e) {
			logger.error("[user/show]の取得・登録処理に失敗しました。", e);
			throw e;
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
			}
		}
//		List<String> screenNameList = new ArrayList<String>();
//		screenNameList.add("mei__sakai");
//		screenNameList.add("komatsuhina");
//		screenNameList.add("ic_sora721");
//		screenNameList.add("PV__marika");
//		screenNameList.add("Hinano_fitone");
//		screenNameList.add("Nana_Owada728");
//		screenNameList.add("Ishikawa__Mai");
//		screenNameList.add("ymmt_mitsuki");
//		screenNameList.add("_hoshi1127");
//		screenNameList.add("ayuayukkk69");
//		screenNameList.add("Kyueens_Mizuki");
//		screenNameList.add("T_1113_Hono");
//		screenNameList.add("karen_koizumi");
//		screenNameList.add("isobaaaaaa");
//		screenNameList.add("rina_marucon");
//		screenNameList.add("CHIE_Pimms");
//		screenNameList.add("naaboudoufu");
//		screenNameList.add("mitsuki_saori");
//		screenNameList.add("yano_toko");
//		screenNameList.add("AKI_WISMALU");
//		screenNameList.add("ayapi25");
//		screenNameList.add("umino_hasegawa");
//		screenNameList.add("_miyu_araki_");
//		screenNameList.add("yuntammmm");
//		screenNameList.add("asukyo_kyoka");
//		screenNameList.add("harumizu");
//		screenNameList.add("rikattikoni4");
//
//		try {
//			for (String screenName : screenNameList) {
//				System.out.print("screen_name=" + screenName);
//				User user = getUserByScreenName(screenName);
//
//				saveToTable(user);
//				System.out.println(" -- Success!!");
//
//				Thread.sleep(1000L + random.nextInt(2000));
//			}
//		} catch (Exception e) {
//			System.out.println(" -- Failure..");
//			e.printStackTrace();
//		}

	}

	private Map<String, String> getUserIds(Connection connection) throws SQLException {

		Map<String, String> resultMap = new HashMap<String, String>();
		String sql = "SELECT user_id, screen_name FROM user";
		ResultSet rs = connection.prepareStatement(sql).executeQuery();

		while (rs.next()) {
			resultMap.put(rs.getString(1), rs.getString(2));
		}

		return resultMap;
	}

	private void saveToTable(Connection connection, User user) throws SQLException {

		PreparedStatement ps = connection.prepareStatement("insert into user_history (user_id, name, screen_name, description, "
				+ " followers_count, friends_count, listed_count, statuses_count, time ) "
				+ " values ( ?, ?, ?, ?, ?, ?, ?, ?, NOW() ) ");

		int index = 0;
		ps.setString(++index, user.getUserId());
		ps.setString(++index, user.getName());
		ps.setString(++index, user.getScreenName());
		ps.setString(++index, user.getDescription());
		ps.setInt(++index, user.getFollowersCount());
		ps.setInt(++index, user.getFriendsCount());
		ps.setInt(++index, user.getListedCount());
		ps.setInt(++index, user.getStatusesCount());

		ps.executeUpdate();
	}

	public User getUserByScreenName(String screenName) throws Exception {

		TwParams apiParams = new TwParams();
		apiParams.addQueryParam("screen_name", screenName);
		return getUser(apiParams);
	}

	public User getUserById(String userId) throws Exception {

		TwParams apiParams = new TwParams();
		apiParams.addQueryParam("id", userId);
		return getUser(apiParams);
	}

	public User getUser(TwParams apiParams) throws Exception {

		String method = "GET";
		String resourceUrl = "https://api.twitter.com/1.1/users/show.json";
		TwData apiData = new TwData();

		boolean result = TwitterApiAccessor.getInstance().execute(method, resourceUrl, apiParams, apiData);

		User user = null;
		if (result) {
			user = new User(apiData.getContent());
		} else {
			apiData.getException().printStackTrace();
			throw apiData.getException();
		}

		return user;
	}
}
