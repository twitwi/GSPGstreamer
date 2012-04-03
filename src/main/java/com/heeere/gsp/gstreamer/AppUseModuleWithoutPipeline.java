package com.heeere.gsp.gstreamer;

import java.awt.Dimension;
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
        source.uri = "/home/twilight/doc/PublicationsAndPresentations/2012-04-gsp-group-meeting/smartkom_small.avi";
        //source.uri = "/home/media/010-Zinzins_L'extraterrestre-xvid.avi";
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
            imageIcon.setImage(source.getLastOutput());
            imageView.repaint();
            if (i % 100 == 0) {
                System.err.println(i);
            }
            Thread.sleep(50);
        }
        System.err.println("duration: " + (System.currentTimeMillis() - t) + "ms");
    }
}
