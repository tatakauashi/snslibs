/**
 *
 */
package net.meiteampower.twitterapi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author tatak
 *
 */
public class TwData implements Serializable {

	private String method;
	private String resourceUrl;
	private Map<String, List<String>> requestHeaders = new HashMap<String, List<String>>();
	private Map<String, List<String>> responseHeaders = new HashMap<String, List<String>>();
	private String postContent;
	private int responseCode;
	private int rateLimitLimit;
	private int rateLimitRemaining;
	private Date rateLimitReset;
	private String content;
	private Exception exception;
	public final String getMethod() {
		return method;
	}
	public final void setMethod(String method) {
		this.method = method;
	}
	public final String getResourceUrl() {
		return resourceUrl;
	}
	public final void setResourceUrl(String resourceUrl) {
		this.resourceUrl = resourceUrl;
	}
	public final Map<String, List<String>> getResponseHeaders() {
		return responseHeaders;
	}
	public final void setResponseHeaders(Map<String, List<String>> responseHeaders) {
		this.responseHeaders = responseHeaders;
	}
	public final void addResponseHeader(String key, List<String> value) {
		if(!responseHeaders.containsKey(key)) {
			responseHeaders.put(key, new ArrayList<String>());
		}
		responseHeaders.get(key).addAll(value);
	}
	public final String getPostContent() {
		return postContent;
	}
	public final void setPostContent(String postContent) {
		this.postContent = postContent;
	}
	public final int getResponseCode() {
		return responseCode;
	}
	public final void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public final int getRateLimitLimit() {
		return rateLimitLimit;
	}
	public final void setRateLimitLimit(int rateLimitLimit) {
		this.rateLimitLimit = rateLimitLimit;
	}
	public final int getRateLimitRemaining() {
		return rateLimitRemaining;
	}
	public final void setRateLimitRemaining(int rateLimitRemaining) {
		this.rateLimitRemaining = rateLimitRemaining;
	}
	public final Date getRateLimitReset() {
		return rateLimitReset;
	}
	public final void setRateLimitReset(Date rateLimitReset) {
		this.rateLimitReset = rateLimitReset;
	}
	public final String getContent() {
		return content;
	}
	public final void setContent(String content) {
		this.content = content;
	}
	public final Exception getException() {
		return exception;
	}
	public final void setException(Exception exception) {
		this.exception = exception;
	}
	public final Map<String, List<String>> getRequestHeaders() {
		return requestHeaders;
	}
	public final void setRequestHeaders(Map<String, List<String>> requestHeaders) {
		this.requestHeaders = requestHeaders;
	}
	public final void addRequestHeader(String key, List<String> value) {
		if(!requestHeaders.containsKey(key)) {
			requestHeaders.put(key, new ArrayList<String>());
		}
		requestHeaders.get(key).addAll(value);
	}
}
