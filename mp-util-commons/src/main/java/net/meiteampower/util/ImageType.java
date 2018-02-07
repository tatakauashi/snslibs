package net.meiteampower.util;

/**
 * イメージファイルの種類を定義する。
 *
 * @author kie
 */
public enum ImageType {

	JPEG("jpeg", new byte[] {(byte)0xff, (byte)0xd8}),
	PNG("png", new byte[] {(byte)0x89, (byte)0x50, (byte)0x4e, (byte)0x47}),
	GIF("gif", new byte[] {(byte)0x47, (byte)0x49, (byte)0x46, (byte)0x38});

	public final String name;
	byte[] data;

	private ImageType(String name, byte[] data) {
		this.name = name;
		this.data = data;
	}
}
