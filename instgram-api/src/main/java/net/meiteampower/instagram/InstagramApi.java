package net.meiteampower.instagram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.meiteampower.instagram.entity.FreqController;
import net.meiteampower.instagram.entity.PostPage;
import net.meiteampower.instagram.entity.ProfilePage;
import net.meiteampower.instagram.entity.QueryResponse;
import net.meiteampower.instagram.entity.Update;
import net.meiteampower.util.InstagramUtils;

/**
 * @author kie
 *
 */
public class InstagramApi {

	private static final Logger logger = Logger.getLogger(InstagramApi.class);

//	private static final String CHARSET = "UTF-8";
	private static final String JSON_START_STR =
			"<script type=\"text/javascript\">window._sharedData = ";
	private static final String JSON_END_STR =
			";</script>";

	/** HTTPコンテキスト。HTTPセッションを保持する。 */
	private HttpContext httpContext;

	/** HTTPクライアントオブジェクト。 */
	private HttpClient client;

	public InstagramApi() {
		CookieStore cookies = new BasicCookieStore();
		httpContext = new BasicHttpContext();
		httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookies);
	}

	/**
	 * プロフィールページにアクセスし、その内容をオブジェクトとして取得する。
	 */
	public ProfilePage getProfilePage(String screenName, Instant lowerDateTime) throws Exception {

		String json = getProfilePageJson(screenName);
//		logger.debug("@@@@@@@@ ProfilePage(json)=" + json);

		ProfilePage page = getProfilePageData(json);

		// 指定した日付までのデータが取得できていない場合、投稿がある限り繰り返し取得する
		while (page.isHasNextPage() && page.getUpdateList() != null && page.getUpdateList().size() > 0
				&& page.getUpdateList().get(page.getUpdateList().size() - 1)
					.getTakenAtTimestamp().isAfter(lowerDateTime)) {

			// 2ページ目以降
			getMoreProfilePages(page, 30);
		}

		return page;
	}

	/**
	 * プロフィールページからさらにページを取得する。
	 * @param page
	 * @param firstSize さらに読み込むページ数
	 * @throws Exception
	 */
	public void getMoreProfilePages(ProfilePage page, int firstSize) throws Exception {

		int totalSize = page.getUpdateList().size() + firstSize;

		while (page.isHasNextPage() && page.getUpdateList().size() < totalSize) {
			FreqController freqCon = new FreqController();
			if (firstSize > 0) {
				freqCon.setFirstSize(firstSize);
			} else {
				freqCon.setFirstSize(100);
			}
			QueryResponse response = new QueryResponse();
			getPageNext(page.getId(), page.getEndCursor(), freqCon, response);
			String json = response.getJson();
			if (json != null) {
				ProfilePage page2 = getProfileNextPageData(json);
				page.getUpdateList().addAll(page2.getUpdateList());
				page.setEndCursor(page2.getEndCursor());
				page.setHasNextPage(page2.isHasNextPage());
			}
		}
	}

	/**
	 * プロフィールページのJSONからプロフィールページオブジェクトを生成する。
	 * @param json
	 * @return
	 */
	public ProfilePage getProfileNextPageData(String json) throws Exception {

		ProfilePage page = new ProfilePage();
		// 投稿一覧を取得する
		List<Update> updateList = new ArrayList<Update>();
		page.setUpdateList(updateList);
		try {
			JsonObject obj = new Gson().fromJson(json, JsonObject.class);
			JsonObject media = obj.get("data").getAsJsonObject()
					.get("user").getAsJsonObject()
					.get("edge_owner_to_timeline_media").getAsJsonObject();
			JsonArray edges = media.get("edges").getAsJsonArray();

			for (JsonElement edge : edges) {
				JsonObject nodeObject = edge.getAsJsonObject().get("node").getAsJsonObject();
				Update update = new Update();
				update.setShortcode(nodeObject.get("shortcode").getAsString());
				update.setTakenAtTimestamp(Instant.ofEpochSecond(nodeObject.get("taken_at_timestamp").getAsLong()));
				update.setVideo(nodeObject.get("is_video").getAsBoolean());
				update.setDisplaySrc(nodeObject.get("display_url").getAsString());

				// caption
//				update.setCaption(nodeObject.get("caption").getAsString());
				JsonArray captionEdges = nodeObject.get("edge_media_to_caption").getAsJsonObject()
						.get("edges").getAsJsonArray();
				if (captionEdges.size() > 0) {
					update.setCaption(captionEdges.get(0).getAsJsonObject()
							.get("node").getAsJsonObject()
							.get("text").getAsString());
				} else {
					update.setCaption("");
				}
				updateList.add(update);
			}

			// ページ情報
			page.setUpdateCount(media.get("count").getAsInt());
			page.setHasNextPage(media.get("page_info").getAsJsonObject()
					.get("has_next_page").getAsBoolean());
			page.setEndCursor(media.get("page_info").getAsJsonObject()
					.get("end_cursor").getAsString());

		} catch (Exception e) {
			throw new Exception("ProfileNextPageの変換に失敗しました。username=" + page.getUsername(), e);
		}

		return page;
	}

	/**
	 * プロフィールページのJSONからプロフィールページオブジェクトを生成する。
	 * @param json
	 * @return
	 */
	public ProfilePage getProfilePageData(String json) throws Exception {

		JsonObject obj = new Gson().fromJson(json, JsonObject.class);
		JsonObject user = obj.get("entry_data").getAsJsonObject()
				.get("ProfilePage").getAsJsonArray()
				.get(0).getAsJsonObject()
				.get("user").getAsJsonObject();

		ProfilePage page = new ProfilePage();
		page.setId(user.get("id").getAsString());
		page.setUsername(user.get("username").getAsString());
		page.setFullName(user.get("full_name").isJsonNull() ? "" : user.get("full_name").getAsString());
		page.setBiography(user.get("biography").isJsonNull() ? "" : user.get("biography").getAsString());
		page.setFollowedBy(user.get("followed_by").getAsJsonObject()
				.get("count").getAsInt());
		page.setFollows(user.get("follows").getAsJsonObject()
				.get("count").getAsInt());

		// 投稿一覧を取得する
		List<Update> updateList = new ArrayList<Update>();
		page.setUpdateList(updateList);
		try {
			JsonObject media = user.getAsJsonObject("media");
			if (media.has("nodes") && media.get("nodes").isJsonArray()) {
				for (JsonElement node : media.getAsJsonArray("nodes")) {
					JsonObject nodeObject = node.getAsJsonObject();
					Update update = new Update();
					update.setShortcode(nodeObject.get("code").getAsString());
					update.setTakenAtTimestamp(Instant.ofEpochSecond(nodeObject.get("date").getAsLong()));
					update.setVideo(nodeObject.get("is_video").getAsBoolean());
					update.setDisplaySrc(nodeObject.get("display_src").getAsString());
					if (nodeObject.has("caption") && !nodeObject.get("caption").isJsonNull()) {
						update.setCaption(nodeObject.get("caption").getAsString());
					} else {
						update.setCaption("");
					}
					updateList.add(update);
				}
			}

			// ページ情報
			page.setUpdateCount(media.get("count").getAsInt());
			page.setHasNextPage(media.get("page_info").getAsJsonObject()
					.get("has_next_page").getAsBoolean());
			page.setEndCursor(media.get("page_info").getAsJsonObject()
					.get("end_cursor").getAsString());

		} catch (Exception e) {
			throw new Exception("ProfilePageの変換に失敗しました。username=" + page.getUsername(), e);
		}

		return page;
	}

	/**
	 * ポストページにアクセスし、その内容をオブジェクトとして取得する。
	 */
	public PostPage getPostPage(String shortcode) throws Exception {

		logger.debug("PostPage: sortcode=" + shortcode);

		String json = getPostPageJson(shortcode);
//		logger.debug("@@@@@@@@ PostPage(json)=" + json);

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
		page.setShortcode(shortcodeMedia.get("shortcode").getAsString());
		JsonArray captionEdges = shortcodeMedia.get("edge_media_to_caption").getAsJsonObject()
				.get("edges").getAsJsonArray();
		if (captionEdges.size() > 0) {
			page.setText(captionEdges.get(0).getAsJsonObject()
					.get("node").getAsJsonObject()
					.get("text").getAsString());
		} else {
			page.setText("");
		}

		// likeの数
		page.setLikeCount(shortcodeMedia.get("edge_media_preview_like").getAsJsonObject()
			.get("count").getAsInt());

		// 投稿日時
		page.setTakenAtTimestamp(Instant.ofEpochSecond(
				shortcodeMedia.get("taken_at_timestamp").getAsBigInteger().longValue()));

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

		// owner
		page.setId(shortcodeMedia.get("owner").getAsJsonObject().get("id").getAsString());
		page.setUsername(shortcodeMedia.get("owner").getAsJsonObject().get("username").getAsString());

		return page;
	}

	/**
	 * InstagramのプロフィールページにあるJSON文字列を取得する。
	 * @param screenName ユーザ名
	 * @return json
	 * @throws IOException エラーが発生した場合
	 */
	public String getProfilePageJson(String screenName) throws Exception {

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
	public String getPostPageJson(String shortcode) throws Exception {

		String requestUrl = String.format("https://www.instagram.com/p/%s/", shortcode);

		String json = getJson(requestUrl);

		return json;
	}

	private String getJson(String requestUrl) throws Exception {

		client = getClientInstance();
		HttpGet httpGet = new HttpGet(requestUrl);

		setCommonHeader(httpGet);

		setXCsrfTokenHeader(httpGet, httpContext);

		HttpResponse httpResponse = client.execute(httpGet, httpContext);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		String reasonPhrase = httpResponse.getStatusLine().getReasonPhrase();
		logger.debug("Response Status: " + statusCode + " " + reasonPhrase);

		String line = null;
		String json = "";
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(httpResponse.getEntity().getContent()))) {
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

	public void getFollowers(String accountId, String endCursor, FreqController freqCon,
			QueryResponse response) throws Exception {
		logger.debug(String.format("Exec Query[Followers]: accountId=[%s], first=[%d], endCursor=[%s]",
				accountId, freqCon.getFirstSize(), endCursor == null ? "なし" : "あり"));
		getByQuery(InstagramUtils.getFollowerQueryId(), "id", accountId, endCursor, freqCon, response);
	}

	public void getPageNext(String accountId, String endCursor, FreqController freqCon,
			QueryResponse response) throws Exception {
		if (freqCon.getFirstSize() <= 0) {
			freqCon.setFirstSize(100);
		}
		logger.debug(String.format("Exec Query[PageNext]: accountId=[%s], first=[%d], endCursor=[%s]",
				accountId, freqCon.getFirstSize(), endCursor == null ? "なし" : "あり"));
		getByQuery(InstagramUtils.getPageNextQueryId(), "id", accountId, endCursor, freqCon, response);
	}

	public void getShortcodeLikeUsers(String shortcode, String endCursor, FreqController freqCon,
			QueryResponse response) throws Exception {
		logger.debug(String.format("Exec Query[like]: sortcode=[%s], first=[%d], endCursor=[%s]",
				shortcode, freqCon.getFirstSize(), endCursor == null ? "なし" : "あり"));
		getByQuery(InstagramUtils.getShortcodeLikeQueryId(), "shortcode", shortcode, endCursor, freqCon,
				response);
	}

	public void getByQuery(String queryId, String keyName,
			String queryValue, String endCursor, FreqController freqCon, QueryResponse response) throws Exception {
		getByQuery(queryId, keyName, queryValue, endCursor, freqCon, response, httpContext);
	}

	public void getByQuery(String queryId, String keyName,
			String queryValue, String endCursor, FreqController freqCon,
			QueryResponse response, HttpContext httpContext) throws Exception {

		long sleepTime = freqCon.getSleepTimeMillis();
		logger.debug(String.format("Start Query: queryId=[%s] [%s]=[%s] sleepTime=[%d]",
				queryId, keyName, queryValue, sleepTime));

		Thread.sleep(sleepTime);

		String url = "https://www.instagram.com/graphql/query/?query_id=" + queryId
				+ "&variables=" + URLEncoder.encode(
						getVariablesJson(keyName, queryValue, freqCon.getFirstSize(), endCursor),
						StandardCharsets.UTF_8.toString());
		client = getClientInstance();
		HttpGet httpGet = new HttpGet(url);

		setCommonHeader(httpGet);

		setXCsrfTokenHeader(httpGet, httpContext);

		HttpResponse httpResponse = client.execute(httpGet, httpContext);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		String reasonPhrase = httpResponse.getStatusLine().getReasonPhrase();
		logger.debug("Response Status: " + statusCode + " " + reasonPhrase);

		response.setStatusCode(statusCode);
		response.setReasonPhrase(reasonPhrase);

		Header contentLengthHeader = httpResponse.getFirstHeader("Content-Length");
		long contentLength = 0;
		if (contentLengthHeader != null) {
			contentLength = Long.parseLong(contentLengthHeader.getValue());
		}
		response.setContentLength(contentLength);

//		if (statusCode != 200) {
//			return response;
//		}

		HttpEntity entity = httpResponse.getEntity();
//		InputStream is = entity.getContent();
		StringBuilder sb = new StringBuilder();
//		String line = null;
//		try (BufferedReader reader = new BufferedReader(
//				new InputStreamReader(is))) {
//			while ((line = reader.readLine()) != null) {
//				sb.append(line);
//			}
//		}
		char[] cbuf = new char[640000];
		try (InputStreamReader reader = new InputStreamReader(entity.getContent())) {
			int size = -1;
			while ((size = reader.read(cbuf)) != -1) {
				sb.append(Arrays.copyOf(cbuf, size));
			}
		}
		String json = sb.toString();
//		logger.debug("Response JSON=" + json);

		if (json != null) {
			response.setJson(json);
//			JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
//			response.setJsonObject(jsonObject);

			if (contentLengthHeader == null) {
				response.setContentLength(json.length());
			}
		}
	}

	public String getVariablesJson(String keyName, String shortcode, int firstSize, String endCursor) {

		StringBuilder sb = new StringBuilder();
		sb.append(String.format("{\"%s\":\"%s\",\"first\":%d", keyName, shortcode, firstSize));
		if (endCursor != null) {
			sb.append(",");
			sb.append(String.format("\"after\":\"%s\"", endCursor));
		}
		sb.append("}");

		return sb.toString();
	}

	public boolean logout() throws Exception {
		return logout(httpContext);
	}

	public boolean logout(HttpContext httpContext) throws Exception {

		logger.debug(String.format("Exec logout"));

		String url = "https://www.instagram.com/accounts/logout/";
		client = getClientInstance();
		HttpPost httpPost = new HttpPost(url);

		setCommonHeader(httpPost);
		setXCsrfTokenHeader(httpPost, httpContext);

		HttpResponse httpResponse = client.execute(httpPost, httpContext);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		String reasonPhrase = httpResponse.getStatusLine().getReasonPhrase();
		logger.debug("Response Status: " + statusCode + " " + reasonPhrase);

		return statusCode == 200;
	}

	public boolean login() throws Exception {
		return login(httpContext);
	}

	public boolean login(HttpContext httpContext) throws Exception {

		logger.debug(String.format("Exec login"));

		Thread.sleep(1000);

		String url = "https://www.instagram.com/accounts/login/ajax/";
		client = getClientInstance();
		HttpPost httpPost = new HttpPost(url);

		setCommonHeader(httpPost);

		setXCsrfTokenHeader(httpPost, httpContext);

		List <NameValuePair> paramList = new ArrayList <NameValuePair>();
		paramList.add(new BasicNameValuePair("username", InstagramUtils.getUsername()));
		paramList.add(new BasicNameValuePair("password", InstagramUtils.getPassword()));
		httpPost.setEntity(new UrlEncodedFormEntity(paramList));

		HttpResponse httpResponse = client.execute(httpPost, httpContext);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		String reasonPhrase = httpResponse.getStatusLine().getReasonPhrase();
		logger.debug("Response Status: " + statusCode + " " + reasonPhrase);

		return statusCode == 200;
	}

	public boolean getFrontPage() throws Exception {
		return getFrontPage(httpContext);
	}

	public boolean getFrontPage(HttpContext httpContext) throws Exception {

		logger.debug(String.format("Get FrontPage"));

		String url = "https://www.instagram.com/";
		client = getClientInstance();
		HttpGet httpGet = new HttpGet(url);

		setCommonHeader(httpGet);

		HttpResponse httpResponse = client.execute(httpGet, httpContext);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		String reasonPhrase = httpResponse.getStatusLine().getReasonPhrase();
		logger.debug("Response Status: " + statusCode + " " + reasonPhrase);

		return statusCode == 200;
	}

	/**
	 * HTTPクライアントインスタンスを取得する。
	 * 初回は生成して返す。
	 * @return HTTPクライアントインスタンス
	 */
	private HttpClient getClientInstance() {
//		if (client == null) {
			client = HttpClients.createDefault();
//		}
		return client;
	}

	private void setXCsrfTokenHeader(HttpMessage httpMessage, HttpContext httpContext) throws Exception {

//		Thread.sleep(1000);

		CookieStore cookieStore = (CookieStore)httpContext.getAttribute(HttpClientContext.COOKIE_STORE);
		for (Cookie cookie : cookieStore.getCookies()) {
			String name = cookie.getName();
			String value = cookie.getValue();
			if (name.toLowerCase().equals("csrftoken")) {
				httpMessage.setHeader("X-CSRFToken", value);
				break;
			}
		}
	}

	private void setCommonHeader(HttpMessage httpMessage) {

		httpMessage.setHeader("Accept", "*/*");
		httpMessage.setHeader("Accept-Encoding", "gzip");
		httpMessage.setHeader("Accept-Language", "ja-JP,ja;q=0.8,en-US;q=0.6,en;q=0.4");
		httpMessage.setHeader("Cache-Control", "no-cache");
		httpMessage.setHeader("DNT", "1");
		httpMessage.setHeader("Pragma", "no-cache");

		String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";
//							"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36";
		httpMessage.setHeader("User-Agent", userAgent);

		httpMessage.setHeader("Referer", "https://www.instagram.com/");
		httpMessage.setHeader("X-Requested-With", "XMLHttpRequest");
	}

}
