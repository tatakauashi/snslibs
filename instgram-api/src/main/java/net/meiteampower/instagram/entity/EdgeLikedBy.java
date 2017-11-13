/**
 *
 */
package net.meiteampower.instagram.entity;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author kie
 *
 */
public class EdgeLikedBy implements Serializable {

	private boolean hasNextPage;
	private String endCursor;
	private List<ProfilePage> edges;
	private String status;

	public static EdgeLikedBy build(JsonObject jsonObject) {

		EdgeLikedBy info = new EdgeLikedBy();

		String status = "fail";
		if (jsonObject.has("status")) {
			status = jsonObject.get("status").getAsString();
		}
		info.setStatus(status);

		info.edges = new ArrayList<ProfilePage>();

		if (jsonObject.has("data") && jsonObject.get("data").isJsonObject()) {
			JsonObject data = jsonObject.get("data").getAsJsonObject();

			if (data.has("shortcode_media") && data.get("shortcode_media").isJsonObject()) {
				JsonObject shortcodeMedia = data.get("shortcode_media").getAsJsonObject();

				if (shortcodeMedia.has("edge_liked_by") && shortcodeMedia.get("edge_liked_by").isJsonObject()) {
					JsonObject edgeLikedBy = shortcodeMedia.get("edge_liked_by").getAsJsonObject();

					if (edgeLikedBy.has("page_info") && edgeLikedBy.get("page_info").isJsonObject()) {
						JsonObject pageInfo = edgeLikedBy.get("page_info").getAsJsonObject();

						if (pageInfo.has("has_next_page")) {
							info.setHasNextPage(pageInfo.get("has_next_page").getAsBoolean());
						}
						if (info.isHasNextPage() && pageInfo.has("end_cursor")) {
							info.setEndCursor(pageInfo.get("end_cursor").getAsString());
						}
					}

					if (edgeLikedBy.has("edges") && edgeLikedBy.get("edges").isJsonArray()) {
						for (JsonElement elem : edgeLikedBy.get("edges").getAsJsonArray()) {
							if (elem.isJsonObject()) {
								JsonObject obj = elem.getAsJsonObject();

								if (obj.has("node") && obj.get("node").isJsonObject()) {
									JsonObject node = obj.get("node").getAsJsonObject();

									ProfilePage profile = new ProfilePage();
									info.edges.add(profile);
									if (node.has("id")) {
										profile.setId(node.get("id").getAsString());
									}
									if (node.has("username")) {
										profile.setUsername(node.get("username").getAsString());
									}
									if (node.has("full_name")) {
										profile.setFullName(node.get("full_name").getAsString());
									}
								}
							}
						}
					}
				}
			}
		}

		return info;
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
	public final List<ProfilePage> getEdges() {
		return edges;
	}
	public final void setEdges(List<ProfilePage> edges) {
		this.edges = edges;
	}
	public final String getStatus() {
		return status;
	}
	public final void setStatus(String status) {
		this.status = status;
	}

}
