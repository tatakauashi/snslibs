package net.meiteampower.util;

import java.util.regex.Pattern;

/**
 * 文字コードに関するユーティリティ
 * @author kie
 * @see https://ja.wikipedia.org/wiki/Unicode
 * @see https://ja.wikipedia.org/wiki/Unicode6.0%E3%81%AE%E6%90%BA%E5%B8%AF%E9%9B%BB%E8%A9%B1%E3%81%AE%E7%B5%B5%E6%96%87%E5%AD%97%E3%81%AE%E4%B8%80%E8%A6%A7
 */
public final class CharCodeUtils {

	/** ユニコード文字列のパターン */
	private static final Pattern UnicodePattern = Pattern.compile("^([Uu]\\+)?\\w+$");

	/**
	 *
	 * @param str
	 * @return
	 */
	public static String toSurrogatePair(String str) {

		String result = "";
		if (str != null && UnicodePattern.matcher(str).find()) {
			String uni = str.toLowerCase();
			String hex = uni.startsWith("u+") ? uni.substring(2) : uni;

			int dec = Integer.parseInt(hex, 16);

			char hi = (char)((dec - 0x10000) / 0x400 + 0xD800);
			char lo = (char)((dec - 0x10000) % 0x400 + 0xDC00);

			if (hi != 0) {
				result = String.valueOf(hi);
			}
			result += String.valueOf(lo);
		}

		return result;
	}
}
