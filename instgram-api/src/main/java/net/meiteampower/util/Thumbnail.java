package net.meiteampower.util;

import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * @see http://qiita.com/tool-taro/items/1f414424b31a86e97446#comment-76a41ab4e55db252b7f6
 * @see http://d.hatena.ne.jp/nacookan/20140308/1394210262
 * @author kie
 */
public class Thumbnail {

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
