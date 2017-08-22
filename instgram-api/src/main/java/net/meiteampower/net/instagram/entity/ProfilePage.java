package net.meiteampower.net.instagram.entity;

import java.io.Serializable;

public class ProfilePage implements Serializable {

	private String id;
	private String username;
	private String fullName;
	private String biography;
	private int followedBy;
	private int follows;

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
}
