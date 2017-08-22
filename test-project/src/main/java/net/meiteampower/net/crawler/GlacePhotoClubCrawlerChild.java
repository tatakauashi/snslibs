/**
 *
 */
package net.meiteampower.net.crawler;

import org.apache.log4j.Logger;

/**
 * @author kie
 *
 */
public class GlacePhotoClubCrawlerChild extends GlacePhotoClubCrawler {

	private static final Logger logger = Logger.getLogger(GlacePhotoClubCrawlerChild.class);

	public static void main(String[] args) {

		GlacePhotoClubCrawlerChild me = new GlacePhotoClubCrawlerChild();

		logger.info("メールの送信処理を開始します。");
		me.sendMail();
		logger.info("メールの送信処理が終了しました。");
	}
}
