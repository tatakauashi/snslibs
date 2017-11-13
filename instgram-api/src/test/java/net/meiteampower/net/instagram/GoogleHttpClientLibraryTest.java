package net.meiteampower.net.instagram;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.RequestLine;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.meiteampower.instagram.InstagramApi;
import net.meiteampower.instagram.entity.EdgeLikedBy;
import net.meiteampower.instagram.entity.PostPage;
import net.meiteampower.instagram.entity.ProfilePage;
import net.meiteampower.instagram.entity.QueryResponse;
import net.meiteampower.util.InstagramUtils;

public class GoogleHttpClientLibraryTest {

	private static final Logger logger = Logger.getLogger(GoogleHttpClientLibraryTest.class);

	private static HttpContext httpContext;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		if (httpContext != null) {
			logout(httpContext);
		}
		httpContext = null;
	}

	@Test
	public void test() {

		try {
			CookieStore cookies = new BasicCookieStore();
			httpContext = new BasicHttpContext();
			httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookies);

			// GET https://www.instagram.com/
			boolean actual = getFrontPage(httpContext);
			assertTrue(actual);

			// POST https://www.instagram.com/accounts/login/ajax/
			actual = postLogin(httpContext);
			assertTrue(actual);


			// POST shortcode like user
			String shortcode = "BagemEthin9";
			String endCursor = null;
			EdgeLikedBy edgeLikedBy = null;
			boolean likedByMeimei = false;
			int errorCounter = 0;
			do {
				QueryResponse response = getShortcodeLikeUsers(shortcode, httpContext, endCursor);

				if (response.getStatusCode() != 200) {

					if (response.getStatusCode() == 429) {
						// Rate Limit Errorの場合

					} else if (response.getStatusCode() == 502) {
						// Gateway Errorの場合
					}

					errorCounter++;
					if (errorCounter < 2) {
						continue;
					}
					fail();
				}

				String json = response.getJson();
				JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);

				edgeLikedBy = EdgeLikedBy.build(jsonObject);
				assertEquals("ok", edgeLikedBy.getStatus());

				endCursor = edgeLikedBy.getEndCursor();
				for (ProfilePage profile : edgeLikedBy.getEdges()) {
					if ("sakai__mei".equals(profile.getUsername())) {
						likedByMeimei = true;
						break;
					}
				}

//				break;
			} while (!likedByMeimei && (edgeLikedBy == null || edgeLikedBy.isHasNextPage()));

			//
			logger.info("＊＊＊＊ shortcode " + shortcode + " でめいめいのlike:" + likedByMeimei);

			if (likedByMeimei) {
				PostPage postPage = new InstagramApi().getPostPage(shortcode);
				for (String url : postPage.getDisplayUrls()) {
					logger.info(">>>> url: " + url);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	private static final int FIRST_SIZE = 100;

	private QueryResponse getShortcodeLikeUsers(String shortcode, HttpContext httpContext, String endCursor) throws Exception {

		Thread.sleep(2300);

		QueryResponse response = new QueryResponse();

		String url = "https://www.instagram.com/graphql/query/?query_id=" + InstagramUtils.getShortcodeLikeQueryId()
			+ "&variables=" + URLEncoder.encode(getVariablesJson(shortcode, endCursor), StandardCharsets.UTF_8.toString());
		HttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);

		setCommonHeader(httpGet);

		setXCsrfTokenHeader(httpGet, httpContext);

//		List <NameValuePair> paramList = new ArrayList <NameValuePair>();
//		paramList.add(new BasicNameValuePair("query_id", InstagramUtils.getShortcodeLikeQueryId()));
//		paramList.add(new BasicNameValuePair("variables", getVariablesJson(shortcode, endCursor)));
//		httpGet.setEntity(new UrlEncodedFormEntity(paramList));

		HttpResponse httpResponse = client.execute(httpGet, httpContext);
		printHeaders(httpGet, httpResponse);

		int statusCode = httpResponse.getStatusLine().getStatusCode();
		response.setStatusCode(statusCode);
		if (statusCode != 200) {
			return null;
		}

		HttpEntity entity = httpResponse.getEntity();
		InputStream is = entity.getContent();
		StringBuilder sb = new StringBuilder();
		String line = null;
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(is))) {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		}
		String json = sb.toString();
		logger.debug("@@@ content=" + json);
//		JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
		response.setJson(json);

		return response;
	}

	private String getVariablesJson(String shortcode, String endCursor) {

		int firstSize = FIRST_SIZE;
		//{\"shortcode\":\"%s\",\"first\":5000}
		StringBuilder sb = new StringBuilder();
//		sb.append(String.format("{\"shortcode\":\"%s\",\"first\":5000", shortcode));
		sb.append(String.format("{\"shortcode\":\"%s\",\"first\":" + firstSize, shortcode));
		if (endCursor != null) {
			sb.append(",");
			sb.append(String.format("\"after\":\"%s\"", endCursor));
		}
		sb.append("}");

		return sb.toString();
	}

	private boolean postLogin(HttpContext httpContext) throws Exception {

		Thread.sleep(1000);

		String url = "https://www.instagram.com/accounts/login/ajax/";
		HttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);

//		String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";
//		httpPost.setHeader("User-Agent", userAgent);
//		httpPost.setHeader("Referer", "https://www.instagram.com/");
		setCommonHeader(httpPost);

		//httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookies);
		setXCsrfTokenHeader(httpPost, httpContext);

		List <NameValuePair> paramList = new ArrayList <NameValuePair>();
		paramList.add(new BasicNameValuePair("username", InstagramUtils.getUsername()));
		paramList.add(new BasicNameValuePair("password", InstagramUtils.getPassword()));
		httpPost.setEntity(new UrlEncodedFormEntity(paramList));

		HttpResponse httpResponse = client.execute(httpPost, httpContext);
		printHeaders(httpPost, httpResponse);

		return httpResponse.getStatusLine().getStatusCode() == 200;
	}

	private void setXCsrfTokenHeader(HttpMessage httpMessage, HttpContext httpContext) throws Exception {

		Thread.sleep(1000);

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

	private void logout(HttpContext httpContext) throws Exception {

		String url = "https://www.instagram.com/accounts/logout/";
		HttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);

		setCommonHeader(httpPost);
		setXCsrfTokenHeader(httpPost, httpContext);

		HttpResponse httpResponse = client.execute(httpPost, httpContext);
		printHeaders(httpPost, httpResponse);
	}

	private boolean getFrontPage(HttpContext httpContext) throws Exception {

		String url = "https://www.instagram.com/";
		HttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);

//		String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";
//		httpGet.setHeader("User-Agent", userAgent);
		setCommonHeader(httpGet);

		HttpResponse httpResponse = client.execute(httpGet, httpContext);
		printHeaders(httpGet, httpResponse);

		return httpResponse.getStatusLine().getStatusCode() == 200;
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

	private void printHeaders(HttpRequestBase httpRequest, HttpResponse httpResponse) {

		logger.debug("------------------------------------------------");
		RequestLine requestLine = httpRequest.getRequestLine();
		logger.debug("@@@ requestLine.getMethod()=" + requestLine.getMethod());
		logger.debug("@@@ requestLine.getUri()=" + requestLine.getUri());
		printCommonHeaders(httpRequest);
		logger.debug("------------------------------------------------");
		StatusLine statusLine = httpResponse.getStatusLine();
		logger.debug("@@@ statusLine.getStatusCode()=" + statusLine.getStatusCode());
		logger.debug("@@@ statusLine.getReasonPhrase()=" + statusLine.getReasonPhrase());
		printCommonHeaders(httpResponse);
	}

	private void printCommonHeaders(HttpMessage httpMessage) {
		for (Header header : httpMessage.getAllHeaders()) {
			logger.debug("Header: " + header.getName() + "=" + header.getValue());
		}
	}

	public void test1() {

		try {
			String url = "https://www.instagram.com/accounts/login/ajax/";
//			CloseableHttpClient client = HttpClients.createDefault();
			HttpClient client = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(url);

			List <NameValuePair> paramList = new ArrayList <NameValuePair>();
			paramList.add(new BasicNameValuePair("username", "kiyoshimeiteam"));
			paramList.add(new BasicNameValuePair("password", "Juce4juCe4"));
			httpPost.setEntity(new UrlEncodedFormEntity(paramList));

			CookieStore cookies = new BasicCookieStore();
			BasicClientCookie cookie = new BasicClientCookie("sessionid", "null");
			cookie.setDomain("..instagram.com");
			cookie.setAttribute(ClientCookie.DOMAIN_ATTR, "true");
			cookies.addCookie(cookie);
//			cookies.addCookie(new BasicClientCookie("csrftoken", "null"));
			cookie = new BasicClientCookie("csrftoken", "null");
			cookie.setAttribute(ClientCookie.DOMAIN_ATTR, "true");
			cookie.setDomain("..instagram.com");
			cookies.addCookie(cookie);

			HttpContext httpContext = new BasicHttpContext();
			httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookies);

			String[] userAgentArray = new String[] {
					"Mozilla/5.0 (Windows NT 6.1; WOW64) ",
					"AppleWebKit/537.36 (KHTML, like Gecko) ",
					"Chrome/56.0.2924.87 Safari/537.36"
			};
			String userAgent = String.join("", userAgentArray);
			httpPost.setHeader("User-Agent", userAgent);
			httpPost.setHeader("Referer", "https://www.instagram.com/");
			httpPost.setHeader("x-csrftoken", "null");

			HttpResponse httpResponse = client.execute(httpPost, httpContext);
			StatusLine statusLine = httpResponse.getStatusLine();
			System.out.println("statusLine.getStatusCode()=" + statusLine.getStatusCode());
			System.out.println("statusLine.getReasonPhrase()=" + statusLine.getReasonPhrase());
			for (Header header : httpResponse.getAllHeaders()) {
				System.out.println("Header: " + header.getName() + "=" + header.getValue());
			}

			Thread.sleep(1000);

//			httpResponse = client.execute(httpPost, httpContext);
//			statusLine = httpResponse.getStatusLine();
//			System.out.println("statusLine.getStatusCode()=" + statusLine.getStatusCode());
//			System.out.println("statusLine.getReasonPhrase()=" + statusLine.getReasonPhrase());
//			for (Header header : httpResponse.getAllHeaders()) {
//				System.out.println("Header: " + header.getName() + "=" + header.getValue());
//			}

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
