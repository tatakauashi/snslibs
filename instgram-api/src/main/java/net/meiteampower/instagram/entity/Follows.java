package net.meiteampower.instagram.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * フォローしているアカウントを保持する。
 * @author kie
 */
public class Follows implements Serializable {

	private String status;
	private int count;
	private boolean hasNextPage;
	private String endCursor;
	private List<ProfilePage> profileList;

	public final int getCount() {
		return count;
	}
	public final void setCount(int count) {
		this.count = count;
	}
	public final List<ProfilePage> getProfileList() {
		return profileList;
	}
	public final void setProfileList(List<ProfilePage> profileList) {
		this.profileList = profileList;
	}
	public final String getStatus() {
		return status;
	}
	public final void setStatus(String status) {
		this.status = status;
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

	public static Follows build(JsonObject jsonObject) {

		Follows follows = new Follows();
		follows.profileList = new ArrayList<ProfilePage>();

		String status = "fail";
		if (jsonObject.has("status")) {
			status = jsonObject.get("status").getAsString();
		}
		follows.setStatus(status);

		if (jsonObject.has("data") && jsonObject.get("data").isJsonObject()) {
			JsonObject data = jsonObject.get("data").getAsJsonObject();
			if (data.has("user") && data.get("user").isJsonObject()) {
				JsonObject user = data.get("user").getAsJsonObject();
				if (user.has("edge_follow") && user.get("edge_follow").isJsonObject()) {
					JsonObject edgeFollow = user.get("edge_follow").getAsJsonObject();
					if (edgeFollow.has("count") && edgeFollow.get("count").isJsonPrimitive()) {
						follows.count = edgeFollow.get("count").getAsInt();
					}
					if (edgeFollow.has("page_info") && edgeFollow.get("page_info").isJsonObject()) {
						JsonObject pageInfo = edgeFollow.get("page_info").getAsJsonObject();
						if (pageInfo.has("has_next_page") && pageInfo.get("has_next_page").isJsonPrimitive()) {
							follows.hasNextPage = pageInfo.get("has_next_page").getAsBoolean();
						}
						if (pageInfo.has("end_cursor") && pageInfo.get("end_cursor").isJsonPrimitive()) {
							follows.endCursor = pageInfo.get("end_cursor").getAsString();
						}
					}
					if (edgeFollow.has("edges") && edgeFollow.get("edges").isJsonArray()) {
						JsonArray edges = edgeFollow.get("edges").getAsJsonArray();
						for (JsonElement edge : edges) {
							if (edge.isJsonObject()) {
								JsonObject edgeObject = edge.getAsJsonObject();
								if (edgeObject.has("node") && edgeObject.get("node").isJsonObject()) {
									JsonObject node = edgeObject.get("node").getAsJsonObject();
									ProfilePage p = new ProfilePage();
									if (node.has("id")) {
										p.setId(node.get("id").getAsString());
									}
									if (node.has("username")) {
										p.setUsername(node.get("username").getAsString());
									}
									follows.profileList.add(p);
								}
							}
						}
					}
				}
			}
		}

		return follows;
	}
}
