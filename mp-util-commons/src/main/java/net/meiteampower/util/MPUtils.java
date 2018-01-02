package net.meiteampower.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 全体を通したユーティリティを定義する。
 * @author SATOH Kiyoshi
 */
public class MPUtils {

	private static final Logger logger = LoggerFactory.getLogger(MPUtils.class);

	/**
	 * URLまたはファイルパスから、そのファイルの拡張子を取得する。
	 * @param path ファイルまたはURLのパス
	 * @param hasDot ドットから取得する場合はtrue
	 * @return 拡張子
	 */
	public static String getExtension(String path, boolean hasDot) {

		String extension = null;
		if (path != null && path.indexOf(".") >= 0) {
			int index = path.lastIndexOf(".");
			if (!hasDot) {
				index += 1;
			}
			extension = path.substring(index);
		}

		return extension;
	}

	/**
	 * インスタグラムの投稿テキストを指定した長さ以下に短くする。
	 * <ol>
	 * <li>「@」で始まるインスタグラムのユーザー名を削除する。
	 * <li>その上で指定した長さに収まらない場合は、「 ...」を末尾に付与するため、指定した長さ-4文字まで短くする。
	 * <li>短くした際、ハッシュタグを中途半端に削っていた場合は、そのハッシュ宅の先頭の「#」まで削除する。
	 * <li>以上の加工でできた文字列の末尾に「 ...」を付与して返す。
	 * </ol>
	 * @param origText
	 * @param length
	 * @return
	 */
	public static String modifyInstagramText(String origText, int length) {

		logger.debug("modifyInstagramText[START] origText=[{}]", origText);

		String[] array = origText.split("\\s*@\\w+\\s*");
		String text2 = String.join(" ", array);
		if (text2.length() <= length) {
			return text2;
		}

		text2 = text2.substring(0, length - 4);
		logger.debug("[before] text2=[{}]", text2);

		Pattern pattern = Pattern.compile("^(.*?[^\\s])\\s+#(\\w+)?$");
		Matcher matcher = pattern.matcher(text2);
		logger.debug("matcher.matches()=[{}]", matcher.matches());
		if (matcher.matches()) {
			text2 = matcher.group(1);
		}
		logger.debug("[after ] text2=[{}]", text2);

		return text2 + " ...";
	}

	public static String replaceWindowsSafetyChar(String s) {
		return replaceWindowsSafetyChar(s, false);
	}

	public static String replaceWindowsSafetyChar(String s, boolean replaceFull) {

        String wRetVal = s;
        // :
        wRetVal = wRetVal.replaceAll("\\:", "[[co]]");
        // *
        wRetVal = wRetVal.replaceAll("\\*", "[[as]]");
        // ?
        wRetVal = wRetVal.replaceAll("\\?", "[[qe]]");
        // "
        wRetVal = wRetVal.replaceAll("\"", "[[qo]]");
        // <
        wRetVal = wRetVal.replaceAll("<", "[[lt]]");
        // >
        wRetVal = wRetVal.replaceAll(">", "[[gt]]");
        // |
        wRetVal = wRetVal.replaceAll("\\|", "[[pi]]");
        if (replaceFull)
        {
            // \
            wRetVal = wRetVal.replaceAll("\\", "[[ye]]");
            // |
            wRetVal = wRetVal.replaceAll("/", "[[sl]]");
        }
        return wRetVal;
	}
}
