package net.meiteampower.instagram.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

/**
 *
 * @author kie
 */
public class PostPage implements Serializable {

	private String id;
	private String username;
	private String text;
	private String shortcode;
	private List<String> displayUrls;
	private int likeCount;
	private Instant takenAtTimestamp;

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public List<String> getDisplayUrls() {
		return displayUrls;
	}
	public void setDisplayUrls(List<String> displayUrls) {
		this.displayUrls = displayUrls;
	}
	public final int getLikeCount() {
		return likeCount;
	}
	public final void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}
	public final Instant getTakenAtTimestamp() {
		return takenAtTimestamp;
	}
	public final void setTakenAtTimestamp(Instant takenAtTimestamp) {
		this.takenAtTimestamp = takenAtTimestamp;
////		this.takenAtTime = new Date(takenAtTimestamp * 1000);
//		this.takenAtTime = LocalDateTime.ofEpochSecond(takenAtTimestamp, 0, ZoneOffset.ofHours(0)); //new Date(takenAtTimestamp * 1000);
	}
//	public final LocalDateTime getTakenAtTime() {
//		return takenAtTime;
//	}
//	public final void setTakenAtTime(Date takenAtTime) {
//		setTakenAtTimestamp(takenAtTime.getTime() / 1000);
//	}
	public final String getId() {
		return id;
	}
	public final void setId(String id) {
		this.id = id;
	}
	public final String getUsername() {
		return username;
	}
	public final void setUsername(String username) {
		this.username = username;
	}
	public final String getShortcode() {
		return shortcode;
	}
	public final void setShortcode(String shortcode) {
		this.shortcode = shortcode;
	}
}
