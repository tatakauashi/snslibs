package net.meiteampower.blog.crawler.dao;

import java.io.Serializable;

/**
 * URLを解析し、保存先のパス、ファイル名、ブログページかどうかを保持する。
 *
 * @author kie
 */
public class UrlAnalysisResult implements Serializable {

	private final String detailPath;
	private final String fileName;

	public UrlAnalysisResult(String detailPath, String fileName) {
		this.detailPath = detailPath;
		this.fileName = fileName;
	}

	public final String getDetailPath() {
		return detailPath;
	}

	public final String getFileName() {
		return fileName;
	}

}
