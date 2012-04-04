/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.heeere.gsp.gstreamer.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

/**
 *
 */
public class ImageUtils {

    public static BufferedImage createOptimized(int w, int h, Color fillWith) {
        BufferedImage res = createOptimized(w, h);
        Graphics g = res.createGraphics();
        g.setColor(fillWith);
        g.fillRect(0, 0, w, h);
        return res;
    }

    public static BufferedImage createOptimized(int w, int h) {
        try {
            // TODO might have to check for headless and do something else
            return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(w, h);
        } catch (Exception e) {
            return new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
        }
    }
//    public void writeToFile(BufferedImage im, String filename) throws IOException {
//        write(im, new File(filename));
//    }
//    public void write(BufferedImage im, File out) throws IOException {
//        String format = FileUtils.getExtension(out);
//        ImageIO.write(im, format, out);
//    }
}
