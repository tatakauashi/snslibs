package net.meiteampower.instagram.service.thumbnail;

import java.io.Serializable;

/**
 * @author SATOH Kiyoshi
 */
public class ThumbnailParameter implements Serializable {

	/** ショートコード */
	private String shortcode;

	/** フッタの透明度。 */
	private float alphaValue = 0.7F;

	/**
	 * writeFilePathが指定されていない場合は、このパスにランダムなファイル名でサムネイルを保存する。
	 */
	private String writePath;

	/** サムネイルの幅 */
	private int width = 320;

	/** サムネイルの高さ */
	private int height = 320;

	/** サムネイル画像の変換前の画像に比較した縮尺。 */
	private double scale = 0.7;

	public final String getShortcode() {
		return shortcode;
	}

	public final void setShortcode(String shortcode) {
		this.shortcode = shortcode;
	}

	public final String getWritePath() {
		return writePath;
	}

	public final void setWritePath(String writePath) {
		this.writePath = writePath;
	}

	public final int getWidth() {
		return width;
	}

	public final void setWidth(int width) {
		this.width = width;
	}

	public final int getHeight() {
		return height;
	}

	public final void setHeight(int height) {
		this.height = height;
	}

	public final double getScale() {
		return scale;
	}

	public final void setScale(double scale) {
		this.scale = scale;
	}

	public final float getAlphaValue() {
		return alphaValue;
	}

	public final void setAlphaValue(float alphaValue) {
		this.alphaValue = alphaValue;
	}

}
