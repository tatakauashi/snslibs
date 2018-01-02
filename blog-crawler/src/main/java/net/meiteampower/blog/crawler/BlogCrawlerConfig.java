package net.meiteampower.blog.crawler;

import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kie
 */
public class BlogCrawlerConfig {

	private static final Logger loggger = LoggerFactory.getLogger(BlogCrawlerConfig.class);

	private static String saveDir;
	private static String titleIncludesString;
	private static String contentsIncludesString;
	private static String initializeUrl;
	private static String initialCookie;

	private BlogCrawlerConfig() {
	}

	static {
		ResourceBundle bundle = ResourceBundle.getBundle("blog-crawler");
		if (bundle.containsKey("save.dir")) {
			saveDir = bundle.getString("save.dir");
			saveDir += saveDir.endsWith("/") ? "" : "/";
		} else {
			loggger.warn("Can't find property 'save.dir'.");
		}
		if (bundle.containsKey("titleIncludes")) {
			titleIncludesString = bundle.getString("titleIncludes");
		} else {
			loggger.warn("Can't find property 'titleIncludes'.");
		}
		if (bundle.containsKey("contentsIncludes")) {
			contentsIncludesString = bundle.getString("contentsIncludes");
		} else {
			loggger.warn("Can't find property 'contentsIncludes'.");
		}
		if (bundle.containsKey("initializeUrl")) {
			initializeUrl = bundle.getString("initializeUrl");
		} else {
			loggger.warn("Can't find property 'initializeUrl'.");
		}
		if (bundle.containsKey("initialCookie")) {
			initialCookie = bundle.getString("initialCookie");
		} else {
			loggger.warn("Can't find property 'initialCookie'.");
		}
	}

	public static String getSaveDir() {
		return saveDir;
	}

	public static final String getTitleIncludesString() {
		return titleIncludesString;
	}

	public static final String getContentsIncludesString() {
		return contentsIncludesString;
	}

	public static final String getInitialCookie() {
		return initialCookie;
	}

	public static final String getInitializeUrl() {
		return initializeUrl;
	}

}
