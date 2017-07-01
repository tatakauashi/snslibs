/**
 *
 */
package net.meiteampower.api.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.UploadedMedia;

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
					new File("C:\\Users\\kie\\Desktop\\２じゃないよ_2012年12月25日.png"));
			mediaIdList.add(uploadedMedia.getMediaId());
			uploadedMedia = twitter.uploadMedia(
					new File("C:\\Users\\kie\\Desktop\\２じゃないよ_2012年12月25日_2.png"));
			mediaIdList.add(uploadedMedia.getMediaId());

			// アップロードした写真を紐付けたツイートを行う。
			StatusUpdate lastUpdate = new StatusUpdate("テストツイートです。\n改行してみた。\n#twitter4j ハッシュタグ付けてみた。\n写真付けてみた :D");
			lastUpdate.setMediaIds(mediaIdList.get(0), mediaIdList.get(1));
			twitter.updateStatus(lastUpdate);

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
