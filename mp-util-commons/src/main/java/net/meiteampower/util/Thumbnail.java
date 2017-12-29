package net.meiteampower.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

/**
 * @see http://qiita.com/tool-taro/items/1f414424b31a86e97446#comment-76a41ab4e55db252b7f6
 * @see http://d.hatena.ne.jp/nacookan/20140308/1394210262
 * @see http://kyle-in-jp.blogspot.jp/2008/08/java2d.html
 * @author kie
 */
public class Thumbnail {

	/**
	 * 画像の下部に帯を重ねる。
	 * @param backFilePath 対象の画像ファイルのパス
	 * @param frontFilePath 帯の画像ファイルのパス
	 * @param toFilePath 出力する画像ファイルのパス
	 * @param alpha 重ね合わせ時のアルファ値。0～1.0
	 * @throws Exception
	 */
	public static void layer(String backFilePath, String frontFilePath, String toFilePath, float alpha)
			throws Exception {

		BufferedImage img = ImageIO.read(new File(backFilePath));
		BufferedImage img2 = ImageIO.read(new File(frontFilePath));
		int height = img.getHeight();
		int height2 = img2.getHeight();
		Graphics2D gr = img.createGraphics();
		gr.setComposite(
				AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha)
		);
		gr.drawImage(img2, 0, height - height2, null);
		gr.dispose();

        // ファイル形式を決定する
        String toExt = getExt(toFilePath);

        // ファイルを出力する
        ImageIO.write(img, toExt, new File(toFilePath));
	}

	public static void drawString(InputStream baseFileStream, String s, int x, int y, String toFilePath) throws IOException {

		BufferedImage img = ImageIO.read(baseFileStream);
        BufferedImage dst = new BufferedImage(
        		img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D gr = dst.createGraphics();
		gr.drawImage(img, 0, 0, null);
		Font font = new Font("Meiryo UI", Font.PLAIN, 15);
		gr.setFont(font);
		gr.setColor(Color.BLACK);
		gr.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR);
		gr.drawString(s, x, y);
		gr.dispose();

		ImageIO.write(dst, "png", new File(toFilePath));
	}

	/**
	 * 指定した画像を指定したサイズに切り取る。その際、短辺に対してスケールを与え、
	 * 切り取る元の画像を拡大・縮小する。
	 * @param fromFilePath 元の画像
	 * @param toFilePath 切り取った画像
	 * @param scale 短辺に対するスケール
	 * @param toWidth 切り取る際の幅
	 * @param toHeight 切り取る際の高さ
	 * @param ext 拡張子
	 * @throws IOException
	 */
    public static void scaleImage(String fromFilePath, String toFilePath, double scale,
    		int toWidth, int toHeight, String ext) throws IOException {

    	try (InputStream is = new FileInputStream(fromFilePath);
    			OutputStream os = new FileOutputStream(toFilePath)) {

    		// 変換もとの画像オブジェクト
        	BufferedImage org = ImageIO.read(is);
        	// 変換元の画像の幅と高さ
        	int fromWidth = org.getWidth();
        	int fromHeight = org.getHeight();

        	// 変換後の画像の幅と高さとの比を出し、短辺に対してscaleの大きさを切り取るように、
        	// 元画像の拡大・縮小するときの倍率（fromScale）を取得する
            double widthRate = ((double)toWidth) / fromWidth;
            double heightRate = ((double)toHeight) / fromHeight;
            double fromScale = 1.0;
            if (widthRate > heightRate) {
            	fromScale = widthRate / scale;
            } else {
            	fromScale = heightRate / scale;
            }

            // 元画像を拡大・縮小したもの
            ImageFilter filter = new AreaAveragingScaleFilter(
                (int)(fromWidth * fromScale), (int)(fromHeight * fromScale));
            ImageProducer p = new FilteredImageSource(org.getSource(), filter);
            java.awt.Image dstImage = Toolkit.getDefaultToolkit().createImage(p);

            // 拡大・縮小後の幅と高さ
            int width = dstImage.getWidth(null);
            int height = dstImage.getHeight(null);
            BufferedImage dst = new BufferedImage(
            		width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = dst.createGraphics();
            g.drawImage(dstImage, 0, 0, null);
            g.dispose();

            // 中心を切り取る際の左上の座標を取得する
            int x = (int)((width - toWidth) / 2.);
            int y = (int)((height - toHeight) / 2.);
            dst = dst.getSubimage(x, y, toWidth, toHeight);

            // ファイル形式を決定する
            String toExt = getExt(toFilePath);
            if (ext != null) {
            	toExt = ext;
            	toExt = toExt.startsWith(".") ? toExt.substring(1) : toExt;
            } else if (toExt == null) {
            	toExt = "jpeg";
            }

            // ファイルを出力する
            ImageIO.write(dst, toExt, os);
    	}
    }

    /**
     * 指定した倍率で画像を拡大・縮小する。
     * @param fromFilePath
     * @param toFilePath
     * @param scale
     * @param ext
     * @throws IOException
     */
    public static void scaleImage(String fromFilePath, String toFilePath, double scale,
    		String ext) throws IOException {

    	try (InputStream is = new FileInputStream(fromFilePath);
    			OutputStream os = new FileOutputStream(toFilePath)) {

    		// 変換もとの画像オブジェクト
        	BufferedImage org = ImageIO.read(is);
        	// 変換元の画像の幅と高さ
        	int fromWidth = org.getWidth();
        	int fromHeight = org.getHeight();
        	int toWidth = (int)(org.getWidth() * scale);
        	int toHeight = (int)(org.getHeight() * scale);

            // 元画像を拡大・縮小したもの
            ImageFilter filter = new AreaAveragingScaleFilter(
                (int)(fromWidth * scale), (int)(fromHeight * scale));
            ImageProducer p = new FilteredImageSource(org.getSource(), filter);
            java.awt.Image dstImage = Toolkit.getDefaultToolkit().createImage(p);

            // 拡大・縮小後の幅と高さ
            int width = dstImage.getWidth(null);
            int height = dstImage.getHeight(null);
            BufferedImage dst = new BufferedImage(
            		width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = dst.createGraphics();
            g.drawImage(dstImage, 0, 0, null);
            g.dispose();

            dst = dst.getSubimage(0, 0, toWidth, toHeight);

            // ファイル形式を決定する
            String toExt = getExt(toFilePath);
            if (ext != null) {
            	toExt = ext;
            } else if (toExt == null) {
            	toExt = "jpeg";
            }

            // ファイルを出力する
            ImageIO.write(dst, toExt, os);
    	}
    }

    /**
     * ファイルパスから拡張子を取得する。取得できなかった場合はnullを返す。
     * @param filePath 拡張子を取得するファイルパス
     * @return 拡張子。「.」は含まない。
     */
    private static String getExt(String filePath) {
    	int index = filePath.lastIndexOf(".");
    	if (index >= 0 && filePath.length() > (index + 1)) {
    		return filePath.substring(index + 1);
    	}
    	return null;
    }

    public static void scaleImage(File in, File out, double scale) throws IOException {
        BufferedImage org = ImageIO.read(in);

        scale = 640. / org.getWidth();

        ImageFilter filter = new AreaAveragingScaleFilter(
            (int)(org.getWidth() * scale), (int)(org.getHeight() * scale));
        ImageProducer p = new FilteredImageSource(org.getSource(), filter);
        java.awt.Image dstImage = Toolkit.getDefaultToolkit().createImage(p);

        int width = dstImage.getWidth(null);
        int height = dstImage.getHeight(null);
        BufferedImage dst = new BufferedImage(
        		width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = dst.createGraphics();
        g.drawImage(dstImage, 0, 0, null);
        g.dispose();

        int width2 = 360;
        int height2 = (int)(height * ((double)width2 / width));
        int x = (int)((width - width2) / 2.);
        int y = (int)((height - height2) / 2.);
        dst = dst.getSubimage(x, y, width2, height2);

        ImageIO.write(dst, "jpeg", out);
    }
}
