/**
 *
 */
package net.meiteampower.api.test;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.meiteampower.db.factory.DBFactory;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.UploadedMedia;
import twitter4j.auth.AccessToken;

/**
 * @author kie
 *
 */
public class TweetTest {

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

	@Test
	public void test() {

		try {
			List<Long> mediaIdList = new ArrayList<Long>();

			Twitter twitter = TwitterFactory.getSingleton();

			// 写真を２枚アップロードする
			UploadedMedia uploadedMedia = twitter.uploadMedia(
					new File("C:\\Users\\kie\\Desktop\\19534792_110693139557611_3099140528394993664_n.jpg.thumb.jpg"));
			mediaIdList.add(uploadedMedia.getMediaId());
//			uploadedMedia = twitter.uploadMedia(
//					new File("C:\\Users\\kie\\Desktop\\２じゃないよ_2012年12月25日_2.png"));
//			mediaIdList.add(uploadedMedia.getMediaId());

			// アップロードした写真を紐付けたツイートを行う。
			StatusUpdate lastUpdate = new StatusUpdate("テストツイートです。\n改行してみた。\n#twitter4j ハッシュタグ付けてみた。\n写真付けてみた :D");
			lastUpdate.setMediaIds(mediaIdList.get(0), mediaIdList.get(1));
			twitter.updateStatus(lastUpdate);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testInstagram() {

		String linkUrl = "https://www.instagram.com/p/BX5V6lVDbS9/";
		String desktop = "C:\\Users\\kie\\Desktop\\";
		String[] medias = new String[] {
				desktop + "20838389_730783773789574_6843403389670260736_n.jpg.thumb.jpg"
		};
		Long[] mediaIds = new Long[medias.length];
		try {
			// 14896486		tatakauashi
			// 4594089260	kiyoshimeiteam
//			TweetBean bean = getTweetTemplate("4594089260");	// kiyoshimeiteam
			TweetBean bean = getTweetTemplate("14896486");	// tatakauashi
//			String template = "#酒井萌衣 さん #Instagram 更新 " + "\u0001" + "\u00f6" + "\u0003" + " \n%{link_url}%\n※This photo is a part of it! See her post!";
//			String template = new String("#酒井萌衣 さん #Instagram 更新" + "\uD83D\uDE03" + "\n%{link_url}%\n※This photo is a part of it! See her post!");
			String template = bean.getTemplate().replace("%{link_url}%", linkUrl);

			AccessToken accessToken = new AccessToken(bean.getOauthAccessToken(), bean.getOauthAccessTokenSecret());
			TwitterFactory factory = new TwitterFactory();
			Twitter twitter = factory.getInstance(accessToken);

			// 写真を２枚アップロードする
			int index = 0;
			for (String mediaFilePath : medias) {
				UploadedMedia uploadedMedia = twitter.uploadMedia(
						new File(mediaFilePath));
				mediaIds[index++] = uploadedMedia.getMediaId();
			}
//			uploadedMedia = twitter.uploadMedia(
//					new File("C:\\Users\\kie\\Desktop\\２じゃないよ_2012年12月25日_2.png"));
//			mediaIdList.add(uploadedMedia.getMediaId());

			// アップロードした写真を紐付けたツイートを行う。
			StatusUpdate lastUpdate = new StatusUpdate(template);
			if (medias.length == 1) {
				lastUpdate.setMediaIds(mediaIds[0]);
			} else if (medias.length == 2) {
				lastUpdate.setMediaIds(mediaIds[0], mediaIds[1]);
			}
			twitter.updateStatus(lastUpdate);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	private TweetBean getTweetTemplate(String twUserId) throws SQLException {

		TweetBean bean = new TweetBean();
		Connection connection = null;
		try {
			connection = DBFactory.getConnection();

			String sql = "SELECT t.template, c.access_token, c.access_token_secret "
					+ " FROM tweet_template t JOIN costomer c ON (t.tw_user_id = c.tw_user_id) "
					+ " where t.check_account_id = ? AND t.tw_user_id = ? ";

			PreparedStatement ps = connection.prepareStatement(sql);
			int index = 0;
			ps.setInt(++index, 1);
			ps.setString(++index, twUserId);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
//				template = rs.getString(1);
				bean.setTemplate(rs.getString(1));
				bean.setOauthAccessToken(rs.getString(2));
				bean.setOauthAccessTokenSecret(rs.getString(3));
			}

		} finally {
			if (connection != null) {
				connection.close();
			}
		}

		return bean;
	}

}
