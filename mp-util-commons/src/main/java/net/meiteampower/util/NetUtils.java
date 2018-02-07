package net.meiteampower.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.ByteArrayBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kie
 *
 */
public class NetUtils {

	private static final Logger logger = LoggerFactory.getLogger(NetUtils.class);

	public static void download(String url, String path) throws Exception {

		HttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);

//		setCommonHeader(httpGet);

		HttpResponse httpResponse = client.execute(httpGet);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		String reasonPhrase = httpResponse.getStatusLine().getReasonPhrase();
		logger.debug("Response Status: " + statusCode + " " + reasonPhrase);

		// ディレクトリを作成する
		File fileDir = new File(new File(path).getParent());
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}

		int size = 0;
		byte[] buffer = new byte[1024 * 1024 * 10];
		try (InputStream is = httpResponse.getEntity().getContent();
				OutputStream os = new FileOutputStream(path)) {
			while ((size = is.read(buffer)) != -1) {
				if (size > 0) {
					os.write(buffer, 0, size);
				}
			}
		}
	}

	public static byte[] post(String url, String content) throws IOException {

		HttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);

		HttpEntity entity = new ByteArrayEntity(content.getBytes());
		httpPost.setEntity(entity);
//		httpPost.addHeader("content-length", "" + content.length());

		HttpResponse httpResponse = client.execute(httpPost);
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		String reasonPhrase = httpResponse.getStatusLine().getReasonPhrase();
		logger.debug("Response Status: " + statusCode + " " + reasonPhrase);

		if (statusCode == 200) {
			int size = 0;
			byte[] buffer = new byte[1024 * 1024 * 10];
			try (InputStream is = httpResponse.getEntity().getContent()) {
				while ((size = is.read(buffer)) != -1) {
					if (size > 0) {
						ByteArrayBuffer bab = new ByteArrayBuffer(size);
						bab.append(buffer, 0, size);
						return bab.buffer();
					}
				}
			}
		}

		return null;
	}
}
