package net.meiteampower.instagram.service.thumbnail;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Random;

import net.meiteampower.instagram.entity.PostPage;
import net.meiteampower.util.MPUtils;
import net.meiteampower.util.NetUtils;
import net.meiteampower.util.Thumbnail;

/**
 * Shortcodeからサムネイルを作成する。
 * @author SATOH Kiyoshi
 */
public class ThumbnailService {

	private final PostPage postPage;
	private static final Random RANDOM = new Random();

	public ThumbnailService(PostPage postPage) {
		this.postPage = postPage;
	}

	public void get(ThumbnailParameter param, ThumbnailData data) throws Exception {

		// ポストデータを取得し、そこから写真ファイルのパスを取得する。
		if (postPage != null && postPage.getDisplayUrls() != null && postPage.getDisplayUrls().size() > 0) {
			String url = postPage.getDisplayUrls().get(0);

			String writePath = param.getWritePath();
			writePath += (writePath.endsWith("/") || writePath.endsWith("\\")) ? "" : "/";
			long randomLong = RANDOM.nextLong();
			String extension = MPUtils.getExtension(url, true);
			String imageFilePath = writePath +  System.currentTimeMillis()
					+ "_" + (randomLong < 0 ? randomLong * (-1) : randomLong) + extension;
			NetUtils.download(url, imageFilePath);


			// サムネイルを作成する。
			String intermidiatePath = imageFilePath + ".inter" + extension;

			Thumbnail.scaleImage(imageFilePath, intermidiatePath,
					param.getScale(), param.getWidth(), param.getHeight(), extension);
			new File(imageFilePath).delete();
			if (!new File(intermidiatePath).exists()) {
				return;
			}


			// レイヤー画像を作成する。

			// ベースとなる画像
			InputStream baseFileStream = null;
			if (System.getProperties().containsKey("layer.file.path")) {
				String baseFilePath = System.getProperty("layer.file.path");
				baseFileStream = new FileInputStream(baseFilePath);
			} else {
				baseFileStream = this.getClass().getResourceAsStream("/images/instagram_base.png");
			}

			String username = postPage.getUsername();
			if (username.length() > 18) {
				username = username.substring(0, 15) + "...";
			}
			String s = "instagram.com/" + username;
			String layerFilePath = imageFilePath + ".layer.png";
			Thumbnail.drawString(baseFileStream, s, 52, 28, layerFilePath);

			String toPath = imageFilePath + ".layered" + extension;
			Thumbnail.layer(intermidiatePath, layerFilePath, toPath, param.getAlphaValue());
			new File(intermidiatePath).delete();
			new File(layerFilePath).delete();

			if (new File(toPath).exists() && data != null) {
				data.setFilePath(toPath);
			}
		}
	}
}
