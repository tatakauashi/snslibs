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
import net.meiteampower.tweeter.db.DBAccessor;
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
	 * チェックする時間までのインターバルを指定
	 */
	@Option(name = "-i", metaVar = "interval", required = false, usage = "チェックする時間までのインターバルを指定")
	private String intervalStr;

	/**
	 * 実行した日時を指定する場合。
	 * 「yyyyMMddHHmm」形式。
	 */
	@Option(name = "-t", metaVar = "execTime", required = false, usage = "実行した日時を指定する場合")
	private String execTimeStr;

	/**
	 * ツイートするユーザーIDを指定
	 */
	@Option(name = "-c", metaVar = "customerId", required = true, usage = "ツイートするユーザーIDを指定")
	private String customerId;

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

		// ツイッターIDで初期化
		MyConfig.getInstance(Long.valueOf(customerId));

		// 実行した日時が指定されているか
		Instant execTime = Instant.now();
		if (execTimeStr != null) {
			execTime = LocalDateTime.parse(execTimeStr, DateTimeFormatter.ofPattern("yyyyMMddHHmm"))
				.toInstant(ZoneOffset.ofHours(9));
		}

		// ポスト時間のインターバルが指定されているか
		int interval = 0;
		if (intervalStr != null) {
			interval = Integer.parseInt(intervalStr.trim());
		}

		InstagramApi api = new InstagramApi();
		PostService postService = new PostService(api);
		List<PostPage> postList = postService.get(
				username, Integer.parseInt(minutes), execTime.minusSeconds(interval * 60L));

		String textTemplate = "{{TEXT}} {{URL}}";
		if (postList.size() <= 0) {
			logger.info("Instagramの投稿がありませんでした。");
		} else {
			// フォーマットを取得する
			String instagramAccountId = postList.get(0).getId();
			String t = DBAccessor.getTweetTemplate(customerId, instagramAccountId);
			if (t != null) {
				textTemplate = t;
			}
		}

		for (PostPage postPage : postList) {
			String shortcode = postPage.getShortcode();
			String instagramPostUrl = "https://www.instagram.com/p/" + shortcode + "/";

			// インスタグラムの投稿のテキストを、ツイート用に短くする。
			String text = MPUtils.modifyInstagramText(postPage.getText(), 80);

			// ツイート本文を作る
//			text = "#酒井萌衣 さん #Instagram" + "\n" + text + " " + instagramPostUrl;
//			text = "Mei SAKAI's Instagram" + "\n" + text + " " + instagramPostUrl;
			text = textTemplate
					.replaceAll("\\{\\{TEXT\\}\\}", text)
					.replaceAll("\\{\\{URL\\}\\}", instagramPostUrl)
					.replaceAll("\\{\\{LF\\}\\}", "\n");

			// サムネイルを作成する。
			ThumbnailService thumbnailService = new ThumbnailService(postPage);
			ThumbnailParameter param = new ThumbnailParameter();
			param.setShortcode(shortcode);
			param.setWritePath(MyConfig.getInstance().getTmpDir());
			ThumbnailData data = new ThumbnailData();
			thumbnailService.get(param, data);

			String thumbnailFilePath = data.getFilePath();
			try {
				if (thumbnailFilePath != null && new File(thumbnailFilePath).exists()) {
					StatusService statusService = new StatusService(
							MyConfig.getInstance().getMyAccessToken(),
							MyConfig.getInstance().getMyAccessTokenSecret());
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
