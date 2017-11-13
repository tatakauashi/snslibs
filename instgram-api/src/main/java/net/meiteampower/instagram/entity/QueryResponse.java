package net.meiteampower.instagram.entity;

import java.io.Serializable;

/**
 * @author kie
 */
public class QueryResponse implements Serializable {

//	private JsonObject jsonObject;
	private String json;
	private int statusCode;
	private String reasonPhrase;
	private long contentLength;

//	public final JsonObject getJsonObject() {
//		return jsonObject;
//	}
//	public final void setJsonObject(JsonObject jsonObject) {
//		this.jsonObject = jsonObject;
//	}
	public final int getStatusCode() {
		return statusCode;
	}
	public final void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public final String getReasonPhrase() {
		return reasonPhrase;
	}
	public final void setReasonPhrase(String reasonPhrase) {
		this.reasonPhrase = reasonPhrase;
	}
	public final long getContentLength() {
		return contentLength;
	}
	public final void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}
	public final String getJson() {
		return json;
	}
	public final void setJson(String json) {
		this.json = json;
	}
}
