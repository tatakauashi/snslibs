package net.meiteampower.blog.crawler.dao;

/**
 * クロールするURLデータ。
 * @author kie
 */
public class CrawlUrlEntity {

	private final int id;
	private final String url;

	public CrawlUrlEntity(int id, String url) {
		this.id = id;
		this.url = url;
	}
	public final int getId() {
		return id;
	}
	public final String getUrl() {
		return url;
	}
}
