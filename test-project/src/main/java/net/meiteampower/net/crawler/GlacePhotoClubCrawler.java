package net.meiteampower.net.crawler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.log4j.Logger;

/**
 * @author kie
 *
 */
public class GlacePhotoClubCrawler {

	private static final Logger logger = Logger.getLogger(GlacePhotoClubCrawler.class);

	private static final String CheckUrl =
			"http://glace-p.com/details.php?filming_id=%s";

	public static void main(String[] args) {

		logger.info("処理開始");

		GlacePhotoClubCrawler crawler = new GlacePhotoClubCrawler();

		if (crawler.checkUntil() && crawler.crawl("1201")) {
			logger.info("5部の満枠崩れを検知しました。メールを送信します。");

			// メールを送信する
			if (crawler.sendMail()) {
				logger.info("メールの送信に成功しました。");
			}
		}

		logger.info("処理終了");
	}

	/**
	 * クローラの期限前かどうかを確認する。
	 * @return
	 */
	private boolean checkUntil() {

		boolean checkResult = false;
		try {
			checkResult = new Date().before(DateTimeFormat.parse("2017/07/09 00:00:00"));
		} catch (ParseException e) {
			e.printStackTrace();
			logger.error("日付のパース例外が発生しました。", e);
		}

		return checkResult;
	}

	private Map<String, String> headers = new HashMap<String, String>(){

		private static final long serialVersionUID = 1L;
		{
			put("Content-Transfer-Encoding", "base64");
		}
	};

	public boolean sendMail() {

		boolean isSuccess = false;
	    Email email = new SimpleEmail();

	    try {
	      email.setHostName("localhost");
	      email.setSmtpPort(25);
	      email.setCharset("UTF-8");
	      email.setHeaders(headers);
//	      email.setAuthenticator(new DefaultAuthenticator(username, password));
	      email.setStartTLSEnabled(false);
	      email.setFrom("crawler@meiteam-power.net");
	      email.addTo("tatakauashi@gmail.com");
	      email.setSubject("【空き枠検知！】" + getDateTimeString());
	      email.setMsg("5部の空き枠を検知しました！");
	      email.setDebug(true);

	      email.send();
	      isSuccess = true;

	    } catch (EmailException e) {
	      e.printStackTrace();
	      logger.error("メールの送信に失敗しました！", e);
	    }

	    return isSuccess;
	}

	private static final SimpleDateFormat DateTimeFormat =
			new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	private String getDateTimeString() {
		Date time = new Date();
		return DateTimeFormat.format(time);
	}

	/**
	 * グラッセふぉとくらぶさんの満枠崩れチェック。
	 * @param filmingId
	 * @return
	 */
	public boolean crawl(String filmingId) {

		boolean result = false;
        HttpURLConnection connection = null;

        String resourceUrl = String.format(CheckUrl, filmingId);
		try {
	        URL url = new URL(resourceUrl);
	        connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("GET");

	        logger.info("リクエストを送信します。url=" + resourceUrl);

	        // リクエストを送る
	        connection.connect();

	        // レスポンスコード
	        int responseCode = connection.getResponseCode();
	        if (responseCode == 200) {
	        	BufferedReader reader = null;
	        	try {
	        		reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

	                String response;
	                boolean checkPoint = false;
	                while ((response = reader.readLine()) != null) {
	                	if (!checkPoint) {
	                		if (response.indexOf("<td>5部</td>") >= 0) {
	                			checkPoint = true;
	                			result = true;
	                		}
	                	} else {
	                		if (response.indexOf("<td>10 / 10</td>") >= 0) {
	                			result = false;
	                			break;
	                		}
	                	}
	                }

	        	} catch (Exception e) {
	        		e.printStackTrace();
	    			logger.error("レスポンスの読み込みでエラーが発生しました。", e);
	    			result = false;
	        	} finally {
	        		if (reader != null) {
	        			reader.close();
	        		}
	        	}
	        }

	        logger.info("リクエストの送受信が正常に終了しました。");

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("クロールでエラーが発生しました。", e);
			result = false;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

		return result;
	}
}
