package net.meiteampower.blog.crawler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.meiteampower.blog.crawler.dao.CrawlUrlEntity;
import net.meiteampower.blog.crawler.dao.CrawlerDao;
import net.meiteampower.blog.crawler.dao.UrlAnalysisResult;
import net.meiteampower.db.factory.DBFactory;
import net.meiteampower.net.MpHttpClient;
import net.meiteampower.net.ResponseData;
import net.meiteampower.net.UrlAnalyzer;
import net.meiteampower.net.UrlInfo;
import net.meiteampower.util.ElapsedTime;
import net.meiteampower.util.MPUtils;

/**
 * @author kie
 */
public class Main {

	/** ロガー */
	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	/** /id:nnnnnnnn/ にマッチする正規表現。 */
	private static final Pattern SlashEndsBlogUrlRegex = Pattern.compile(".*/id\\:\\d+/$");

	/**
	 * チェックしたいインスタグラムのアカウントを指定
	 */
	@Option(name = "-l", metaVar = "link", required = false, usage = "開始時のURLを指定する")
	private String link;

	/**
	 * チェックしたいインスタグラムのアカウントを指定
	 */
	@Option(name = "-i", metaVar = "intervalSec", required = true, usage = "クロール間隔")
	private int intervalSec;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		long startTime = System.currentTimeMillis();
		logger.info("[START] 処理を開始します。");

		try {
			Main main = new Main();
			CmdLineParser parser = new CmdLineParser(main);
			parser.parseArgument(args);

			main.execute();

		} catch (CmdLineException e) {
			logger.error("コマンドライン引数が不正です。", e);
		} catch (Exception e) {
			logger.error("処理に失敗しました。", e);
		}

		long endTime = System.currentTimeMillis();
		ElapsedTime elapsedTime = ElapsedTime.create(startTime, endTime);
		logger.info("[END] 処理が終了しました。{} 時間 {} 分 {} 秒掛かりました。",
				elapsedTime.getHours(), elapsedTime.getMinutes(), elapsedTime.getSeconds());
	}

	private void execute() throws SQLException, IOException {

		if (link != null) {
			String href = link.substring(link.indexOf("/blog/"));
			try (Connection connection = DBFactory.getConnection()) {
				CrawlerDao.add(connection, 0, href, link, link);
//				connection.commit();
			}
		}

		try (Connection connection = DBFactory.getConnection()) {
			CrawlUrlEntity entity = null;

			MpHttpClient httpClient = new MpHttpClient(
					BlogCrawlerConfig.getInitializeUrl(),
					BlogCrawlerConfig.getInitialCookie());

			while ((entity = CrawlerDao.getNext(connection)) != null) {

				try {
					int retVal = getOneUrl(connection, entity, httpClient);
					if (retVal != 1) {
						logger.warn("エラーが発生したため処理を終了します。id=[{}], retVal=[{}]",
								entity.getId(), retVal);
						break;
					}
				} finally {
//					connection.commit();
				}
			}
		}
	}

	private int getOneUrl(Connection connection, CrawlUrlEntity entity, MpHttpClient httpClient) throws SQLException {

		int retVal = -1;	// デフォルトはエラー終了

		String titleIncludesString = BlogCrawlerConfig.getTitleIncludesString();
		String contentsIncludesString = BlogCrawlerConfig.getContentsIncludesString();

		int id = entity.getId();
		String url = entity.getUrl();
		if (CrawlerDao.changeStatusToGetting(connection, id) == 1) {

			// /id:xxxxxxxxx/ の場合はリクエストもしないでスキップする
			Matcher matcher = SlashEndsBlogUrlRegex.matcher(url);
			if (matcher.matches()) {
				// リクエストせず、ファイルも保存せずにステータスを変更。
				CrawlerDao.changeStatusToWarning(connection, id, "Blog url is ends with /.", "text/html");
				return 1;

			} else {
				try {
					Thread.sleep(intervalSec * 1000);
				} catch (InterruptedException e) {
					logger.warn("sleepに失敗しました。", e);
				}

				try {
					ResponseData resData = httpClient.get(url);
					String contentType = resData.getContentType();

					if (resData.getStatusCode() == 200) {
						UrlAnalysisResult uar = null;
						if ((uar = CrawlerDao.analyzeUrl(url)) != null) {
							String savePath = BlogCrawlerConfig.getSaveDir() + uar.getDetailPath();
							Files.createDirectories(Paths.get(savePath));

							String fileName = uar.getFileName();
							String extension = MPUtils.getExtension(fileName, true);
							if (extension == null || extension.isEmpty()) {
								fileName += CrawlerDao.getExtension(contentType);
							}

							boolean hasLinks = false;
		                    if (fileName.endsWith(".html") || fileName.endsWith(".htm"))
		                    {
		                    	hasLinks = true;
		                    }
		                    else if (fileName.endsWith(".css"))
		                    {
		                    	hasLinks = true;
		                    }

		                    // ファイルを保存する
		                    try (InputStream is = resData.getInputStream();
		                    		OutputStream os = new FileOutputStream(savePath + fileName)) {
		                    	byte[] buffer = new byte[1024];
		                    	int length = -1;
		                    	while ((length = is.read(buffer)) != -1) {
		                    		os.write(buffer, 0, length);
		                    	}
		                    }

		                    if (hasLinks) {
		                    	// コンテンツチェック
		                    	int checkResult = CrawlerDao.checkBlogFile(savePath + fileName,
		                    			titleIncludesString, contentsIncludesString);

		                    	switch (checkResult) {
		                    		case 1:
		                    			// 探しているメンバーのブログではない場合
		                    			CrawlerDao.changeStatusToSkipped(connection, id,
		                    					savePath + fileName, contentType, "Blog is not Searching one.");

		                    			// 続行
		                    			return 1;

		                    		case 2:
		                    			// セッションが切れた
		                    			new File(savePath + fileName).delete();
		                    			throw new RuntimeException("id=" + id + ", Session is not allowed.");
		                    	}

								// 抽出したURLからフルURLを作成するため、元のURLを親URLとして整える。
								UrlInfo urlInfo = UrlAnalyzer.analyze(url);
								String parentUrl = urlInfo.getScheme() + "://" + urlInfo.getHostPort() + urlInfo.getPath();

								// URLを抽出する
								List<String> urlList = new ArrayList<String>();
								CrawlerDao.readUrl(savePath + fileName, urlList);

								for (String href : urlList) {
									String nextUrl = href;
									if (nextUrl.startsWith("/")) {
										nextUrl = "http://www2.ske48.co.jp" + nextUrl;
									}
									if (!CrawlerDao.exists(connection, nextUrl)) {
										String fullUrl = CrawlerDao.getFullUrl(parentUrl, nextUrl);
										CrawlerDao.add(connection, id, href, nextUrl, fullUrl);
									}
								}

								// 保存完了
								CrawlerDao.changeStatusToCompleted(connection, id, savePath + fileName, contentType);
								retVal = 1;

		                    } else {
		                    	// 保存完了（画像やJSなど、他のURLのリンクを含まないもの）
		                    	CrawlerDao.changeStatusToSkipped(connection, id, savePath + fileName,
		                    			contentType, "It is not a blog.");

								retVal = 1;	// 続行
		                    }

						} else {
							logger.error("URLの解析に失敗しました。id=[{}], url=[{}]", id, url);

							// 失敗扱いとして記録する
							CrawlerDao.changeStatusToFailed(connection, id, "URLの解析に失敗。URL=" + url);

							retVal = 1;	// 続行
						}

					} else {
						logger.error("HTTPアクセスが成功しませんでした。id=[{}], statusCode=[{}], message=[{}]",
								id, resData.getStatusCode(), resData.getMessage());

						// 失敗扱いとして記録する
						CrawlerDao.changeStatusToFailed(connection, id,
								resData.getStatusCode() + " - " + resData.getMessage());

						retVal = 1;	// 続行
					}

				} catch (IOException e) {
					logger.error("URLのリクエストに失敗しました。id=[{}], URL=[{}]", id, url, e);

					// 失敗扱いとして記録する
					CrawlerDao.changeStatusToFailed(connection, id, e.getMessage());

					retVal = 1;	// 続行
				}
			}
		}

		return retVal;
	}

}
