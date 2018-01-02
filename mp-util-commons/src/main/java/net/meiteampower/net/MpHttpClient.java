package net.meiteampower.net;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

/**
 * @author kie
 *
 */
public class MpHttpClient {

	private static final String TARGET_HOST = "www2.ske48.co.jp";

	/** HTTPコンテキスト。HTTPセッションを保持する。 */
	private HttpContext httpContext;

	public MpHttpClient(String initializeUrl, String defaultCookie) throws IOException {

		CookieStore cookieStore = new BasicCookieStore();
		httpContext = new BasicHttpContext();
		httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookieStore);

//		// クッキーを設定するためにデフォルトのサイトにアクセスする
//		CloseableHttpClient client = HttpClients.createDefault();
//		HttpGet httpGet = new HttpGet(initializeUrl);
//		httpGet.addHeader("Cookie", defaultCookie);
//		client.execute(httpGet);

		String[] cookies = defaultCookie.split(";");
		for (String cookiePair : cookies) {
			String[] keyValue = cookiePair.trim().split("\\=", 2);
			String key = keyValue[0].trim();
			String value = keyValue[1].trim();
			BasicClientCookie bCookie = new BasicClientCookie(key, value);
	        bCookie.setDomain(TARGET_HOST);
	        bCookie.setPath("/");
	        cookieStore.addCookie(bCookie);
		}
	}

	public ResponseData get(String url) throws IOException {

		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		setCommonHeader(httpGet);

		HttpResponse httpResponse = client.execute(httpGet, httpContext);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		String reasonPhrase = httpResponse.getStatusLine().getReasonPhrase();
		String contentType = null;
		InputStream is = null;
		if (statusCode == 200) {
			Header[] headers = httpResponse.getHeaders("Content-type");
			if (headers != null && headers.length > 0) {
				contentType = headers[0].getValue();
			}
			is = httpResponse.getEntity().getContent();
		}

		return new ResponseData(statusCode, reasonPhrase, contentType, is);
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

		httpMessage.setHeader("Referer", TARGET_HOST);
//		httpMessage.setHeader("X-Requested-With", "XMLHttpRequest");
	}

}
