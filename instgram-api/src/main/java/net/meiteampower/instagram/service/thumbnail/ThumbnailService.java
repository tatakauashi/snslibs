package net.meiteampower.instagram.service.thumbnail;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import javax.imageio.ImageIO;

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

//	public void get(ThumbnailParameter param, ThumbnailData data) throws Exception {
//
//		// ポストデータを取得し、そこから写真ファイルのパスを取得する。
//		if (postPage != null && postPage.getDisplayUrls() != null && postPage.getDisplayUrls().size() > 0) {
//			String url = postPage.getDisplayUrls().get(0);
//
//			String writePath = param.getWritePath();
//			writePath += (writePath.endsWith("/") || writePath.endsWith("\\")) ? "" : "/";
//			long randomLong = RANDOM.nextLong();
//			String extension = MPUtils.getExtension(url, true);
//			String imageFilePath = writePath +  System.currentTimeMillis()
//					+ "_" + (randomLong < 0 ? randomLong * (-1) : randomLong) + extension;
//			NetUtils.download(url, imageFilePath);
//
//
//			// サムネイルを作成する。
//			String intermidiatePath = imageFilePath + ".inter" + extension;
//
//			Thumbnail.scaleImage(imageFilePath, intermidiatePath,
//					param.getScale(), param.getWidth(), param.getHeight(), extension);
//			new File(imageFilePath).delete();
//			if (!new File(intermidiatePath).exists()) {
//				return;
//			}
//
//
//			// レイヤー画像を作成する。
//
//			// ベースとなる画像
//			InputStream baseFileStream = null;
//			if (System.getProperties().containsKey("layer.file.path")) {
//				String baseFilePath = System.getProperty("layer.file.path");
//				baseFileStream = new FileInputStream(baseFilePath);
//			} else {
//				baseFileStream = this.getClass().getResourceAsStream("/images/instagram_base.png");
//			}
//
//			String username = postPage.getUsername();
//			if (username.length() > 18) {
//				username = username.substring(0, 15) + "...";
//			}
//			String s = "instagram.com/" + username;
//			String layerFilePath = imageFilePath + ".layer.png";
//			Thumbnail.drawString(baseFileStream, s, 52, 28, layerFilePath);
//
//			String toPath = imageFilePath + ".layered" + extension;
//			Thumbnail.layer(intermidiatePath, layerFilePath, toPath, param.getAlphaValue());
//			new File(intermidiatePath).delete();
//			new File(layerFilePath).delete();
//
//			if (new File(toPath).exists() && data != null) {
//				data.setFilePath(toPath);
//			}
//		}
//	}

	public void get(ThumbnailParameter param, ThumbnailData data) throws Exception {

		// ポストデータを取得し、そこから写真ファイルのパスを取得する。
		if (postPage != null && postPage.getDisplayUrls() != null && postPage.getDisplayUrls().size() > 0) {

			// レイヤーイメージを作成する。
			String username = postPage.getUsername();
			if (username.length() > 18) {
				username = username.substring(0, 15) + "...";
			}
			String s = "instagram.com/" + username;
			BufferedImage bandImg = makeInstagramUserBand(s);

			// 写真をダウンロードする。
			String url = postPage.getDisplayUrls().get(0);
			String writePath = param.getWritePath();
			writePath += writePath.endsWith("/") ? "" : "/";
			long randomLong = RANDOM.nextLong();
			String extension = MPUtils.getExtension(url, true);
			String imageFilePath = writePath +  System.currentTimeMillis()
					+ "_" + (randomLong < 0 ? randomLong * (-1) : randomLong) + extension;
			NetUtils.download(url, imageFilePath);

			// 写真をスケールし、そこにレイヤーイメージを重ねて出力する。
//			createThumbnail(param, data, bandImg, extension, imageFilePath);
			createThumbnail(param, data, bandImg, "jpg", imageFilePath);

			// 元の画像を削除する
			new File(imageFilePath).delete();
		}
	}

	public void createThumbnail(ThumbnailParameter param, ThumbnailData data, BufferedImage bandImg,
			String extension, String imageFilePath) throws IOException {

		// 写真をスケールし、そこにレイヤーイメージを重ねて出力する。
		String toPath = imageFilePath + ".layered." + extension;
		BufferedImage dst = Thumbnail.layer(imageFilePath, bandImg,
				param.getScale(), param.getWidth(), param.getHeight(),
				param.getAlphaValue());

		if (dst != null) {// ファイルを出力する
//		    ImageIO.write(dst, extension, new File(toPath));
			Thumbnail.writeImageJpeg(dst, toPath, 0.95F);

			data.setFilePath(toPath);
		}
	}

	public BufferedImage makeInstagramUserBand(String s) throws IOException {

		// ベースとなる帯
		int width = 320;
		int height = 40;
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gr = img.createGraphics();
		gr.setColor(new Color(247, 247, 247));
		gr.fillRect(0, 0, width, height);

		// Instagramのカメラのマーク
		try (InputStream is = ThumbnailService.class.getResourceAsStream("/images/6479accd8aa6.png")) {
			BufferedImage instMark = ImageIO.read(is);
			// カメラのマークを帯の左端に出力する
	//		gr.setComposite(
	//				AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8F)
	//		);
			gr.drawImage(instMark, 5, 4, 37, 36, 72, 27, 104, 59, null);

			// 文字を出力する。
			Font font = new Font("Meiryo UI", Font.PLAIN, 15);
			gr.setFont(font);
			gr.setColor(new Color(38, 38, 38));
			gr.setRenderingHint(
					RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR);
			gr.drawString(s, 50, 26);
			gr.dispose();

	//		ImageIO.write(img, "png", new File("test.png"));
			return img;
		}
	}

}
