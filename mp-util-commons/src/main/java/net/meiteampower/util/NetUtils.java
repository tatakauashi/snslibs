package net.meiteampower.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
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
}
