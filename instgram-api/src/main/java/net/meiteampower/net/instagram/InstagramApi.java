package net.meiteampower.net.instagram;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.meiteampower.net.instagram.entity.PostPage;
import net.meiteampower.net.instagram.entity.ProfilePage;

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
	 * プロフィールページにアクセスし、その内容をオブジェクトとして取得する。
	 */
	public ProfilePage getProfilePage(String screenName) throws IOException {

		String json = getProfilePageJson(screenName);

		ProfilePage page = getProfilePageData(json);

		return page;
	}

	/**
	 * プロフィールページのJSONからプロフィールページオブジェクトを生成する。
	 * @param json
	 * @return
	 */
	public ProfilePage getProfilePageData(String json) {

		JsonObject obj = new Gson().fromJson(json, JsonObject.class);
		JsonObject user = obj.get("entry_data").getAsJsonObject()
				.get("ProfilePage").getAsJsonArray()
				.get(0).getAsJsonObject()
				.get("user").getAsJsonObject();

		ProfilePage page = new ProfilePage();
		page.setId(user.get("id").getAsString());
		page.setUsername(user.get("username").getAsString());
		page.setFullName(user.get("full_name").getAsString());
		page.setBiography(user.get("biography").getAsString());
		page.setFollowedBy(user.get("followed_by").getAsJsonObject()
				.get("count").getAsInt());
		page.setFollows(user.get("follows").getAsJsonObject()
				.get("count").getAsInt());
		return page;
	}

	/**
	 * ポストページにアクセスし、その内容をオブジェクトとして取得する。
	 */
	public PostPage getPostPage(String shortcode) throws IOException {

		String json = getPostPageJson(shortcode);

		PostPage page = getPostPageData(json);

		return page;
	}

	/**
	 * ポストページのJSONからポストページオブジェクトを生成する。
	 * @param json
	 * @return
	 */
	public PostPage getPostPageData(String json) {

		JsonObject obj = new Gson().fromJson(json, JsonObject.class);
		JsonObject shortcodeMedia = obj.get("entry_data").getAsJsonObject()
				.get("PostPage").getAsJsonArray()
				.get(0).getAsJsonObject()
				.get("graphql").getAsJsonObject()
				.get("shortcode_media").getAsJsonObject();

		PostPage page = new PostPage();
		page.setText(shortcodeMedia.get("edge_media_to_caption").getAsJsonObject()
			.get("edges").getAsJsonArray()
			.get(0).getAsJsonObject()
			.get("node").getAsJsonObject()
			.get("text").getAsString());

		List<String> displayUrls = new ArrayList<String>();
		page.setDisplayUrls(displayUrls);
		if (shortcodeMedia.has("edge_sidecar_to_children")) {
			// 複数の写真あり
			JsonArray edges = shortcodeMedia.get("edge_sidecar_to_children").getAsJsonObject()
					.get("edges").getAsJsonArray();
			for (JsonElement elem : edges) {
				JsonObject node = elem.getAsJsonObject()
						.get("node").getAsJsonObject();
				displayUrls.add(node.get("display_url").getAsString());
			}
		} else {
			displayUrls.add(shortcodeMedia.get("display_url").getAsString());
		}

		return page;
	}

	/**
	 * InstagramのプロフィールページにあるJSON文字列を取得する。
	 * @param screenName ユーザ名
	 * @return json
	 * @throws IOException エラーが発生した場合
	 */
	public String getProfilePageJson(String screenName) throws IOException {

		String requestUrl = String.format("https://www.instagram.com/%s/", screenName);

		String json = getJson(requestUrl);

		return json;
	}

	/**
	 * InstagramのプロフィールページにあるJSON文字列を取得する。
	 * @param screenName ユーザ名
	 * @return json
	 * @throws IOException エラーが発生した場合
	 */
	public String getPostPageJson(String shortcode) throws IOException {

		String requestUrl = String.format("https://www.instagram.com/p/%s/", shortcode);

		String json = getJson(requestUrl);

		return json;
	}

	private String getJson(String requestUrl) throws IOException {

		String json = "";
//		BufferedReader br = null;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(
				new URL(requestUrl).openConnection().getInputStream()), CHARSET))) {

            String line = null;
            while ((line = reader.readLine()) != null) {
            	line = line.trim();
            	if (line.startsWith(JSON_START_STR)) {
            		json = line.replaceAll(JSON_START_STR, "");
            		json = json.substring(0, json.length() - JSON_END_STR.length());
            		break;
            	}
            }
		}

		return json;
	}

}
