package net.meiteampower.instagram.entity;

import java.io.Serializable;
import java.time.Instant;

/**
 * プロフィールページにある投稿一覧の情報。
 *
 * @author tatakauashi
 */
public class Update implements Serializable {

	private String shortcode;
//	private long time;
	private boolean isVideo;
	private String displaySrc;
	private String caption;
	private Instant takenAtTimestamp;
//	private LocalDateTime takenAtTime;

	public final String getShortcode() {
		return shortcode;
	}
	public final void setShortcode(String shortcode) {
		this.shortcode = shortcode;
	}
//	public final long getTime() {
//		return time;
//	}
//	public final void setTime(long time) {
//		this.time = time;
//	}
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
	public final Instant getTakenAtTimestamp() {
		return takenAtTimestamp;
	}
	public final void setTakenAtTimestamp(Instant takenAtTimestamp) {
		this.takenAtTimestamp = takenAtTimestamp;
//		this.takenAtTime = LocalDateTime.ofEpochSecond(takenAtTimestamp, 0, ZoneOffset.ofHours(0)); //new Date(takenAtTimestamp * 1000);
	}
//	public final LocalDateTime getTakenAtTime() {
//		return takenAtTime;
//	}
//	public final void setTakenAtTime(Date takenAtTime) {
//		setTakenAtTimestamp(takenAtTime.getTime() / 1000);
//	}
}
