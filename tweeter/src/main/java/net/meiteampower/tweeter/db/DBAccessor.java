package net.meiteampower.tweeter.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.meiteampower.db.factory.DBFactory;
import net.meiteampower.tweeter.MyConfig;

/**
 * DBにアクセスするクラス。
 *
 * @author kie
 */
public class DBAccessor {

	private static final Logger logger = LoggerFactory.getLogger(DBAccessor.class);

	/**
	 * アクセストークンとそのシークレットを取得する。
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public static String[] getAccessToken(final String id) throws Exception {

		String password = MyConfig.getInstance().getTweetPassword();

		try (Connection connection = DBFactory.getConnection(password)) {
			String sql = "SELECT AES_DECRYPT(UNHEX(access_token), ?) AS access_token,"
					+ " AES_DECRYPT(UNHEX(access_token_secret), ?) AS access_token_secret "
					+ " FROM customer WHERE tw_user_id = ? ";
			PreparedStatement ps = connection.prepareStatement(sql);
			String dbPassword = DBFactory.getPassword(String.valueOf(Long.valueOf(id) % 796571L) + password);
			ps.setString(1, dbPassword);
			ps.setString(2, dbPassword);
			ps.setString(3, id);
			ResultSet resultSet = ps.executeQuery();
			if (resultSet.next()) {
				String accessToken = resultSet.getString(1);
				String accessTokenSecret = resultSet.getString(2);

				return new String[] { accessToken, accessTokenSecret };
			}
		}

		return null;
	}

	/**
	 * ツイートフォーマットを取得する。
	 * @param id ツイッターのユーザーID
	 * @param instagramAccountId インスタグラムのアカウントID
	 * @return
	 * @throws Exception
	 */
	public static String getTweetTemplate(final String id, final String instagramAccountId) throws Exception {

		String password = MyConfig.getInstance().getTweetPassword();

		try (Connection connection = DBFactory.getConnection(password)) {
			String sql = "SELECT template "
					+ " FROM tweet_template WHERE tw_user_id = ? AND in_account_id = ? AND revision = 1 ";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, id);
			ps.setString(2, instagramAccountId);
			ResultSet resultSet = ps.executeQuery();
			if (resultSet.next()) {
				return resultSet.getString(1);
			}
		}

		return null;
	}

}
