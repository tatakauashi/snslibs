/**
 *
 */
package net.meiteampower.api.test;

import java.io.Serializable;

/**
 * @author kie
 *
 */
public class TweetBean implements Serializable {

	private String oauthAccessToken;
	private String oauthAccessTokenSecret;
	private String template;
	public String getOauthAccessToken() {
		return oauthAccessToken;
	}
	public void setOauthAccessToken(String oauthAccessToken) {
		this.oauthAccessToken = oauthAccessToken;
	}
	public String getOauthAccessTokenSecret() {
		return oauthAccessTokenSecret;
	}
	public void setOauthAccessTokenSecret(String oauthAccessTokenSecret) {
		this.oauthAccessTokenSecret = oauthAccessTokenSecret;
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
}
