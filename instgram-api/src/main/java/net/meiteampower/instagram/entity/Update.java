package net.meiteampower.instagram.entity;

import java.io.Serializable;

/**
 * プロフィールページにある投稿一覧の情報。
 *
 * @author tatakauashi
 */
public class Update implements Serializable {

	private String shortcode;
	private long time;
	private boolean isVideo;
	private String displaySrc;
	private String caption;

	public final String getShortcode() {
		return shortcode;
	}
	public final void setShortcode(String shortcode) {
		this.shortcode = shortcode;
	}
	public final long getTime() {
		return time;
	}
	public final void setTime(long time) {
		this.time = time;
	}
	public final boolean isVideo() {
		return isVideo;
	}
	public final void setVideo(boolean isVideo) {
		this.isVideo = isVideo;
	}
	public final String getDisplaySrc() {
		return displaySrc;
	}
	public final void setDisplaySrc(String displaySrc) {
		this.displaySrc = displaySrc;
	}
	public final String getCaption() {
		return caption;
	}
	public final void setCaption(String caption) {
		this.caption = caption;
	}
}
