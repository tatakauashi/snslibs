package net.meiteampower.net;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * URLを解析する。
 * @author kie
 */
public class UrlAnalyzer {

	private static final Pattern W3CRegex = Pattern.compile("^(([^:/?#]+):)?(//((([^/?#]*)@)?([^/?#:]*)(:([^/?#]*))?))?([^?#]*)(\\?([^#]*))?(#(.*))?");

	private UrlAnalyzer() {
	}

	public static UrlInfo analyze(String url) {

		UrlInfo result = null;
		Matcher matcher = W3CRegex.matcher(url);
		if (matcher.matches()) {
			String scheme = matcher.group(2);
			if ("http".equals(scheme) || "https".equals(scheme)) {
				String hostPort = matcher.group(4);
				String path = matcher.group(10);
				String query = matcher.group(12);
				String fragment = matcher.group(14);

				if (path == null || path.isEmpty()) {
					path = "/";
				}

				result = new UrlInfo(scheme, hostPort, path, query, fragment);
			}
		}

		return result;
	}
}
