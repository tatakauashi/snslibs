package net.meiteampower.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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

	/**
	 * 画像ファイルから顔の位置を認識する。
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static double[] detectFaces(final String filePath) throws IOException {

		double[] result = null;
		String imageData = MPUtils.getBase64ImageData(filePath);
		if (imageData != null) {
			byte[] content = NetUtils.post(
					"https://tf-face-detector.herokuapp.com/api/detect", imageData);

			if (content != null) {
				String json = new String(content);
				JsonObject obj = new Gson().fromJson(json, JsonObject.class);

				JsonArray face = null;
				int faceCount = 0;
				for (JsonElement elem : obj.get("results").getAsJsonArray()) {
					obj = elem.getAsJsonObject();
					int clazz = obj.get("class").getAsJsonPrimitive().getAsInt();
					if (clazz == 1) {
						face = obj.get("bbox").getAsJsonArray();
						faceCount++;
					}
				}

				// 顔がひとつのみ認識された場合のみその位置を返す。
				if (faceCount == 1 && face.size() == 4) {
					result = new double[4];
					result[0] = face.get(0).getAsDouble();
					result[1] = face.get(1).getAsDouble();
					result[2] = face.get(2).getAsDouble();
					result[3] = face.get(3).getAsDouble();
				}
			}
		}

		return result;
	}

	public static String getBase64ImageData(final String filePath) throws IOException {

		File f = new File(filePath);
		long fileLength = f.length();
		try (FileInputStream fis = new FileInputStream(f)) {
			byte[] b = new byte[(int)fileLength];
			fis.read(b);
			ImageType type = getImageType(b);

			if (type != null) {
				String encoded = "data:image/" + type.name + ";base64," + Base64.getEncoder().encodeToString(b);
				return encoded;
			} else {
				throw new IllegalStateException("画像ファイルの種別を判定できませんでした。 filePath=" + filePath);
			}
		}
	}

	/**
	 * 画像ファイルの種別を判定する。
	 * @param img がそうファイルのデータ
	 * @return ファイルの種別。判定できなかった場合はnull
	 */
	public static ImageType getImageType(byte[] img) {

		if (img == null || img.length < 4) {
			return null;
		}

		final ImageType[] types = new ImageType[] { ImageType.JPEG, ImageType.PNG, ImageType.GIF };
		for (ImageType type : types) {
			boolean flag = true;
			for (int i = 0; i < type.data.length; i++) {
				if (type.data[i] != img[i]) {
					flag = false;
					break;
				}
			}
			if (flag) {
				return type;
			}
		}

		return null;
	}

}
