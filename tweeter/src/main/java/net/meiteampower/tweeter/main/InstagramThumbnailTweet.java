package net.meiteampower.tweeter.main;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.meiteampower.instagram.InstagramApi;
import net.meiteampower.instagram.entity.PostPage;
import net.meiteampower.instagram.service.post.PostService;
import net.meiteampower.instagram.service.thumbnail.ThumbnailData;
import net.meiteampower.instagram.service.thumbnail.ThumbnailParameter;
import net.meiteampower.instagram.service.thumbnail.ThumbnailService;
import net.meiteampower.tweeter.MyConfig;
import net.meiteampower.twitterapi.service.status.StatusService;
import net.meiteampower.util.MPUtils;

/**
 * @author kie
 *
 */
public class InstagramThumbnailTweet {

	/** ロガー */
	private static final Logger logger = LoggerFactory.getLogger(InstagramThumbnailTweet.class);

	/**
	 * チェックしたいインスタグラムのアカウントを指定
	 */
	@Option(name = "-u", metaVar = "username", required = true, usage = "チェックしたいインスタグラムのアカウントを指定")
	private String username;

	/**
	 * チェックする時間の範囲を指定
	 */
	@Option(name = "-m", metaVar = "minutes", required = true, usage = "チェックする時間の範囲を指定")
	private String minutes;

	/**
	 * 実行した日時を指定する場合。
	 * 「yyyyMMddHHmm」形式。
	 */
	@Option(name = "-t", metaVar = "execTime", required = false, usage = "実行した日時を指定する場合")
	private String execTimeStr;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		logger.info("[START] 処理を開始します。");

		try {
			InstagramThumbnailTweet main = new InstagramThumbnailTweet();
			CmdLineParser parser = new CmdLineParser(main);
			parser.parseArgument(args);

			main.execute();

		} catch (CmdLineException e) {
			logger.error("コマンドライン引数が不正です。", e);
		} catch (Exception e) {
			logger.error("処理に失敗しました。", e);
		}

		logger.info("[END] 処理を終了します。");
	}

	/**
	 * メイン処理。
	 */
	private void execute() throws Exception {

		// 実行した日時が指定されているか
		Instant execTime = Instant.now();
		if (execTimeStr != null) {
			execTime = LocalDateTime.parse(execTimeStr, DateTimeFormatter.ofPattern("yyyyMMddHHmm"))
				.toInstant(ZoneOffset.ofHours(9));
		}

		InstagramApi api = new InstagramApi();
		PostService postService = new PostService(api);
		List<PostPage> postList = postService.get(username, Integer.parseInt(minutes), execTime);

		if (postList.size() <= 0) {
			logger.info("Instagramの投稿がありませんでした。");
		}

		for (PostPage postPage : postList) {
			String shortcode = postPage.getShortcode();
			String instagramPostUrl = "https://www.instagram.com/p/" + shortcode + "/";

			// インスタグラムの投稿のテキストを、ツイート用に短くする。
			String text = MPUtils.modifyInstagramText(postPage.getText(), 80);

			// ツイート本文を作る
			text = "#酒井萌衣 さん #Instagram" + "\n" + text + " " + instagramPostUrl;

			// サムネイルを作成する。
			ThumbnailService thumbnailService = new ThumbnailService(postPage);
			ThumbnailParameter param = new ThumbnailParameter();
			param.setShortcode(shortcode);
			param.setWritePath(MyConfig.getTmpDir());
			ThumbnailData data = new ThumbnailData();
			thumbnailService.get(param, data);

			String thumbnailFilePath = data.getFilePath();
			try {
				if (thumbnailFilePath != null && new File(thumbnailFilePath).exists()) {
					StatusService statusService = new StatusService(
							MyConfig.getMyAccessToken(), MyConfig.getMyAccessTokenSecret());
					List<String> filePathList = new ArrayList<String>();
					filePathList.add(thumbnailFilePath);

					// ツイートする。
					statusService.tweet(text, filePathList);

					logger.info("ツイートしました。tweet=[{}]", text);
				}
			} catch (Exception e) {
				logger.error("ツイートに失敗しました。tweet=[{}]", text);
				throw e;
			} finally {
				if (thumbnailFilePath != null) {
					new File(thumbnailFilePath).delete();
				}
			}
		}
	}


}
