package net.meiteampower.net;

import java.io.Serializable;

/**
 * URLを解析した結果。
 * @author kie
 */
public class UrlInfo implements Serializable {

	private final String scheme;
	private final String hostPort;
	private final String path;
	private final String query;
	private final String fragment;

	public UrlInfo(String scheme, String hostPort, String path, String query, String fragment) {
		this.scheme = scheme;
		this.hostPort = hostPort;
		this.path = path;
		this.query = query;
		this.fragment = fragment;
	}

	public final String getScheme() {
		return scheme;
	}

	public final String getHostPort() {
		return hostPort;
	}

	public final String getPath() {
		return path;
	}

	public final String getQuery() {
		return query;
	}

	public final String getFragment() {
		return fragment;
	}

}
