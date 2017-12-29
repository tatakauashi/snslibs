package net.meiteampower.twitterapi.service.status;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.meiteampower.twitterapi.MyTwitterFactory;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.UploadedMedia;

/**
 * @author kie
 */
public class StatusService {

	private Twitter twitter;

	public StatusService(String oauthAccessToken, String oauthAccessTokenSecret) {
		twitter = MyTwitterFactory.getInstance(oauthAccessToken, oauthAccessTokenSecret);
	}

	public Status tweet(String text, List<String> pictureFilePathList) throws TwitterException {

		// 写真をアップロードする
		List<Long> mediaIdList = new ArrayList<Long>();
		int count = 0;
		for (String filePath : pictureFilePathList) {
			if (count++ < 4) {
				UploadedMedia uploadedMedia = twitter.uploadMedia(
						new File(filePath));
				mediaIdList.add(uploadedMedia.getMediaId());
			} else {
				break;
			}
		}

		// アップロードした写真を紐付けたツイートを行う。
		StatusUpdate lastUpdate = new StatusUpdate(text);
		long[] idArray = new long[mediaIdList.size()];
		int i = 0;
		for (long id : mediaIdList) {
			idArray[i++] = id;
		}

		lastUpdate.setMediaIds(idArray);
		return twitter.updateStatus(lastUpdate);
	}
}
