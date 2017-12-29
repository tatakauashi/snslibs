package net.meiteampower.instagram.main;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.meiteampower.instagram.InstagramApi;
import net.meiteampower.instagram.db.InstagramDao;
import net.meiteampower.instagram.entity.PostPage;

/**
 * @author kie
 */
public class PicSaver {

	private static final Logger logger = LoggerFactory.getLogger(PicSaver.class);

	public static void main(String[] args) {

		try {
			if (args.length > 0) {
				getLikedShortcode(args[0]);
			} else {
				getAllLikedShortcodes();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void getLikedShortcode(String shortcode) throws Exception {

		if (!InstagramDao.isRegistgerdShortcode(shortcode)) {
			InstagramApi api = new InstagramApi();
			PostPage postPage = api.getPostPage(shortcode);

			if (postPage != null) {
				LikeDigger.savePics(postPage);
				logger.info(String.format("shortcode=[%s]を登録しました。", shortcode));
			} else {
				logger.error(String.format("shortcode=[%s]の投稿を取得できませんでした。", shortcode));
			}
		} else {
			logger.warn(String.format("shortcode=[%s]は既に登録されています。", shortcode));
		}
	}

	private static void getAllLikedShortcodes() throws Exception {

		Set<String> likedShortcodes = InstagramDao.getLikedShortcodes(null);

		for (String shortcode : likedShortcodes) {
			getLikedShortcode(shortcode);
		}
	}
}
