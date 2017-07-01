/**
 *
 */
package net.meiteampower.twitterapi.data;

import java.io.Serializable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * @author tatak
 *
 */
public class User implements Serializable {

	private String userId;
	private String name;
	private String screenName;
	private String description;
	private int followersCount;
	private int friendsCount;
	private int listedCount;
	private int statusesCount;
	public final String getUserId() {
		return userId;
	}
	public final String getName() {
		return name;
	}
	public final String getScreenName() {
		return screenName;
	}
	public final String getDescription() {
		return description;
	}
	public final int getFollowersCount() {
		return followersCount;
	}
	public final int getFriendsCount() {
		return friendsCount;
	}
	public final int getListedCount() {
		return listedCount;
	}
	public final int getStatusesCount() {
		return statusesCount;
	}

	public User(String json) {
		Gson gson = new Gson();
		JsonObject object = gson.fromJson(json, JsonObject.class);

		if (object.has("id")) {
			userId = object.get("id").getAsString();
		}
		if (object.has("name")) {
			name = object.get("name").getAsString();
		}
		if (object.has("screen_name")) {
			screenName = object.get("screen_name").getAsString();
		}
		if (object.has("description")) {
			description = object.get("description").getAsString();
		}
		if (object.has("followers_count")) {
			followersCount = object.get("followers_count").getAsInt();
		}
		if (object.has("friends_count")) {
			friendsCount = object.get("friends_count").getAsInt();
		}
		if (object.has("listed_count")) {
			listedCount = object.get("listed_count").getAsInt();
		}
		if (object.has("statuses_count")) {
			statusesCount = object.get("statuses_count").getAsInt();
		}
	}
}
