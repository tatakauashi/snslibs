package net.meiteampower.net.instagram.entity;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author kie
 */
public class PostPage implements Serializable {

	private String text;
	private List<String> displayUrls;

	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public List<String> getDisplayUrls() {
		return displayUrls;
	}
	public void setDisplayUrls(List<String> displayUrls) {
		this.displayUrls = displayUrls;
	}
}
