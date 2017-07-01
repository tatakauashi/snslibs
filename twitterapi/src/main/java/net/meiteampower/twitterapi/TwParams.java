/**
 *
 */
package net.meiteampower.twitterapi;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tatak
 *
 */
public class TwParams implements Serializable {

	private String accountId;
	private String oauthAccessToken;
	private String oauthAccessTokenSecret;
	private Map<String, String> queryParams = new HashMap<String, String>();
	private Map<String, String> requestHeaders = new HashMap<String, String>();

	public final String getAccountId() {
		return accountId;
	}
	public final void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public final String getOauthAccessToken() {
		return oauthAccessToken;
	}
	public final void setOauthAccessToken(String oauthAccessToken) {
		this.oauthAccessToken = oauthAccessToken;
	}
	public final String getOauthAccessTokenSecret() {
		return oauthAccessTokenSecret;
	}
	public final void setOauthAccessTokenSecret(String oauthAccessTokenSecret) {
		this.oauthAccessTokenSecret = oauthAccessTokenSecret;
	}
	public final Map<String, String> getQueryParams() {
		return queryParams;
	}
	public final boolean hasQueryParams() {
		return !queryParams.isEmpty();
	}
	public final void addQueryParam(String key, String value) {
		queryParams.put(key, value);
	}
	public final Map<String, String> getRequestHeaders() {
		return requestHeaders;
	}
	public final boolean hasRequestHeaders() {
		return !requestHeaders.isEmpty();
	}
	public final void addRequestHeader(String key, String value) {
		requestHeaders.put(key, value);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("accountId=[%s], ", accountId));
		sb.append(String.format("oauthAccessToken=[%s], ", oauthAccessToken));
		sb.append("queryParams=[");
		for (Map.Entry<String, String> entry : queryParams.entrySet()) {
			sb.append(String.format("(%s, %s)", entry.getKey(), entry.getValue()));
		}
		sb.append("], ");
		sb.append("requestHeaders=[");
		for (Map.Entry<String, String> entry : requestHeaders.entrySet()) {
			sb.append(String.format("(%s, %s)", entry.getKey(), entry.getValue()));
		}
		sb.append("]");
		return sb.toString();
	}
}
