package net.meiteampower.instagram.db;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class InstagramDaoTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetAccounts() {

		try {
			List<InstagramAccount> accounts = InstagramDao.getAccounts();
			for (InstagramAccount account : accounts) {
				System.out.println("------------------------------");
				System.out.println("account_id=" + account.getAccountId());
				System.out.println("username=" + account.getUsername());
				System.out.println("exclution_flag=" + account.isExclutionFlag());
				System.out.println("insert_time=" + account.getInsertTime());
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testLastLikedInfo() {

		String accountId = "123454321";
		Map<String, InstagramLastLiked> lastLikedMap = null;
		try {
//			lastLikedMap = InstagramDao.getLastLiked(accountId);
//			assertEquals(0, lastLikedMap.size());
//			InstagramDao.registerLastLiked("shortcode1", accountId, "last-1", new Date());

			lastLikedMap = InstagramDao.getLastLiked(accountId);
			assertTrue(lastLikedMap.containsKey("shortcode1"));
			InstagramLastLiked instagramLastLiked = lastLikedMap.get("shortcode1");

			InstagramDao.registerLastLiked(instagramLastLiked, "last-1", new Date());
			lastLikedMap = InstagramDao.getLastLiked(accountId);
			assertTrue(lastLikedMap.containsKey("shortcode1"));

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testRegisterPostInfo() {

		try {



//			PostInfo postInfo = new PostInfo();
//			postInfo.setShortcode("skdjw5241");
//			postInfo.setAccountId("1123334455");
//			postInfo.setText("テキスト！");
//
//			List<String> list = new ArrayList<String>();
//			list.add("https://www.com/hoge.jpg");
//			list.add("http://twitter.com/mei__sakai");
//			postInfo.setDisplayUrlList(list);
//
//			InstagramDao.registerPostInfo(postInfo);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
}
