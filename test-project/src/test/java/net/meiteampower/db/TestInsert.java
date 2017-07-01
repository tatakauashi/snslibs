package net.meiteampower.db;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.meiteampower.db.factory.DBFactory;
import net.meiteampower.util.CharCodeUtils;

/**
 * @author kie
 *
 */
public class TestInsert {

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
	public void testInsertWithUnicodeEmoji() {

		Connection connection = null;
		try {
			connection = DBFactory.getConnection();

			PreparedStatement ps = connection.prepareStatement(
					"INSERT INTO tweet_template (check_account_id, tw_user_id, template, "
					+ " regist_time, update_time ) "
					+ " values ( ?, ?, ?, NOW(), NOW() ) ");

			int index = 0;
			ps.setInt(++index, 1);
			ps.setString(++index, "4594089260");
//			ps.setString(++index, "#酒井萌衣 さん #Instagram 更新\u0001\uF603\n%{link_url}%\n※This photo is a part of it! See her post!");
//			ps.setString(++index, "#酒井萌衣 さん #Instagram 更新" + "\uD83D\uDE03" + "\n%{link_url}%\n※This photo is a part of it! See her post!");
//			ps.setString(++index, "#酒井萌衣 さん #Instagram 更新♪ \n%{link_url}%\n※This photo is a part of it! See her post!");
			ps.setString(++index, "#酒井萌衣 さん #Instagram 更新" + CharCodeUtils.toSurrogatePair("U+1F603") + "\n%{link_url}%\n※This photo is a part of it! See her post!");
//			ps.setString(++index, "テストです " + CharCodeUtils.toSurrogatePair("U+1F60D") + "");


			ps.executeUpdate();

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {

			}
		}
	}

}
