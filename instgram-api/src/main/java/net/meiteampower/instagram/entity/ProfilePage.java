package net.meiteampower.instagram.entity;

import java.io.Serializable;
import java.util.List;

public class ProfilePage implements Serializable {

	private String id;
	private String username;
	private String fullName;
	private String biography;
	private int followedBy;
	private int follows;
	private List<Update> updateList;
	private int updateCount;
	private boolean hasNextPage;
	private String endCursor;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getBiography() {
		return biography;
	}
	public void setBiography(String biography) {
		this.biography = biography;
	}
	public int getFollowedBy() {
		return followedBy;
	}
	public void setFollowedBy(int followedBy) {
		this.followedBy = followedBy;
	}
	public int getFollows() {
		return follows;
	}
	public void setFollows(int follows) {
		this.follows = follows;
	}
	public final List<Update> getUpdateList() {
		return updateList;
	}
	public final void setUpdateList(List<Update> updateList) {
		this.updateList = updateList;
	}
	public final int getUpdateCount() {
		return updateCount;
	}
	public final void setUpdateCount(int updateCount) {
		this.updateCount = updateCount;
	}
	public final boolean isHasNextPage() {
		return hasNextPage;
	}
	public final void setHasNextPage(boolean hasNextPage) {
		this.hasNextPage = hasNextPage;
	}
	public final String getEndCursor() {
		return endCursor;
	}
	public final void setEndCursor(String endCursor) {
		this.endCursor = endCursor;
	}
}
