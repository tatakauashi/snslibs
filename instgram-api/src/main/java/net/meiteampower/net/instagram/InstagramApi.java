package net.meiteampower.net.instagram;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author kie
 *
 */
public class InstagramApi {

	private static final String CHARSET = "UTF-8";
	private static final String JSON_START_STR =
			"<script type=\"text/javascript\">window._sharedData = ";
	private static final String JSON_END_STR =
			";</script>";

	/**
	 * InstagramのプロフィールページにあるJSON文字列を取得する。
	 * @param screenName ユーザ名
	 * @return json
	 * @throws IOException エラーが発生した場合
	 */
	public String getProfileJson(String screenName) throws IOException {

		String json = "";
		String requestUrl = String.format("https://www.instagram.com/%s/", screenName);

		BufferedReader br = null;
		try {
			URL url = new URL(requestUrl);
			URLConnection connection = url.openConnection();
            // HTMLを読み込む
            BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
            br = new BufferedReader(new InputStreamReader(bis, CHARSET));

            String line = null;
            while ((line = br.readLine()) != null) {
            	line = line.trim();
            	if (line.startsWith(JSON_START_STR)) {
            		json = line.replaceAll(JSON_START_STR, "");
            		json = json.substring(0, json.length() - JSON_END_STR.length());
            		break;
            	}
            }
		} finally {
			if (br != null) {
				br.close();
			}
		}

		return json;
	}
}
