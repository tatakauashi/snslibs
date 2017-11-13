package net.meiteampower.instagram.db;

import java.io.Serializable;
import java.util.Date;

/**
 * ある投稿（Shortcode）に対する最新のいいね！の情報。
 *
 * @author kie
 */
public class InstagramLastLiked implements Serializable {

	private String shortcode;
	private int revision;
	private String accountId;
	private String likedAccountId;
	private Date checkedTime;

	public final String getShortcode() {
		return shortcode;
	}
	public final void setShortcode(String shortcode) {
		this.shortcode = shortcode;
	}
	public final int getRevision() {
		return revision;
	}
	public final void setRevision(int revision) {
		this.revision = revision;
	}
	public final String getAccountId() {
		return accountId;
	}
	public final void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public final Date getCheckedTime() {
		return checkedTime;
	}
	public final void setCheckedTime(Date checkedTime) {
		this.checkedTime = checkedTime;
	}
	public final String getLikedAccountId() {
		return likedAccountId;
	}
	public final void setLikedAccountId(String likedAccountId) {
		this.likedAccountId = likedAccountId;
	}
}
