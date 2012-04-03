package com.heeere.gsp.gstreamer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Hello world!
 *
 */
public class AppUseModuleWithoutPipeline {

    public static void main(String[] args) throws InterruptedException {
        ImageSource source = new ImageSource();
        //source.uri = "/home/twilight/doc/PublicationsAndPresentations/2012-04-gsp-group-meeting/smartkom_small.avi";
        source.uri = "/home/media/010-Zinzins_L'extraterrestre-xvid.avi";
        source.skip = 4;
        source.initSource();

        JFrame f = new JFrame("View With Overlay");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ImageIcon imageIcon = new ImageIcon();
        JLabel imageView = new JLabel(imageIcon);
        f.getContentPane().add(imageView);
        imageView.setPreferredSize(new Dimension(800, 600));
        f.pack();
        f.setVisible(true);

        long t = System.currentTimeMillis();
        for (int i = 0; i < 180000; i++) {
            source.input();
            if (source.getLastOutput() == null) {
                System.err.println("last i: " + i);
                break;
            }
            {
                BufferedImage disp = source.getLastOutput(); // there is no copy here but there was already a copy in the ImageSource
                disp = new BufferedImage(disp.getWidth(), disp.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
                Graphics2D g = disp.createGraphics();
                g.drawImage(source.getLastOutput(), 0, 0, null);
                g.setColor(Color.RED);
                g.translate(200., 100.);
                g.rotate(i*.1);
                g.scale(2., 2.);
                g.drawString("Frame "+i, -20f, 0f);
                imageIcon.setImage(disp);
            }
            imageView.repaint();
            if (i % 100 == 0) {
                System.err.println(i);
            }
            //Thread.sleep(20);
        }
        System.err.println("duration: " + (System.currentTimeMillis() - t) + "ms");
    }
}
