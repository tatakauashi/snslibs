package net.meiteampower.net;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * HTTPレスポンスデータ。
 *
 * @author kie
 */
public class ResponseData implements Serializable, Closeable {

	private final int statusCode;
	private final String message;
	private final String contentType;
	private final InputStream is;

	public ResponseData(int statusCode, String message, String contentType, InputStream is) {
		this.statusCode = statusCode;
		this.message = message;
		this.contentType = contentType;
		this.is = is;
	}

	public final int getStatusCode() {
		return statusCode;
	}

	public final String getMessage() {
		return message;
	}

	public final String getContentType() {
		return contentType;
	}

	public final InputStream getInputStream() {
		return is;
	}

	@Override
	public void close() throws IOException {
		if (is != null) {
			is.close();
		}
	}

}
