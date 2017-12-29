package net.meiteampower.instagram.service.thumbnail;

import java.io.Serializable;

/**
 * @author SATOH Kiyoshi
 */
public class ThumbnailData implements Serializable {

	/** 作成したサムネイルのファイルパス。 */
	private String filePath;

	public final String getFilePath() {
		return filePath;
	}

	public final void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}
