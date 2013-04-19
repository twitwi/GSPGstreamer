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
public class AppUseBothModulesWithoutPipeline {

    public static void main(String[] args) throws InterruptedException {
        ImageSource source = new ImageSource();
        source.uri = "/home/twilight/doc/PublicationsAndPresentations/2012-04-gsp-group-meeting/smartkom_small.avi";
        //source.uri = "file:///home/media/010-Zinzins_L'extraterrestre-xvid.avi";
        //source.uri = "http://o-o.preferred.google-vie1.v7.lscache4.c.youtube.com/videoplayback?upn=BNnEhcyFA40&sparams=algorithm%2Cburst%2Ccp%2Cfactor%2Cid%2Cip%2Cipbits%2Citag%2Csource%2Cupn%2Cexpire&fexp=905024%2C910206%2C914102%2C908617&algorithm=throttle-factor&itag=34&ip=213.0.0.0&burst=40&sver=3&signature=4A15D78EE918D35CA09FD6589F8030841CCA4C83.7C595A15E48DD9430FE2C9044938171B97D87727&source=youtube&expire=1334936501&key=yt1&ipbits=8&factor=1.25&cp=U0hSSVlOVF9FS0NOMl9RSFhGOkpaVGdlLWw3U1VK&id=889f70353db573fb";
        //source.ur i= "camera:0"; // or camera://0 or camera:/dev/video0 or camera:///dev/video0
        
        //source.width = 640;
        //source.width = 640; source.preserveAspectRatio = false;
        //source.width = 200; source.height = 400; source.preserveAspectRatio = false;
        // UNHANDLED/ERROR: source.height = 400; // <- alone

        //source.uri = "camera:0"; source.width = 320;
        if (args.length > 0) {
            source.uri = args[0];
        }
        source.skip = 4;
        source.skipAtInit = 25 * 10; // skip 10 seconds
        source.skip = source.skipAtInit = 0;

        VideoEncoder encoder = new VideoEncoder();
        encoder.uri = "/media/ramdisk/encoded.webm";
        encoder.fps = 100;
        encoder.quality = 9;
        
        encoder.initEncoder();
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
                encoder.end();
                System.err.println("last i: " + i);
                break;
            }
            {
                BufferedImage disp = source.getLastOutput(); // there is no copy right here but there was already a copy in the ImageSource
                Graphics2D g = disp.createGraphics();
                g.drawImage(source.getLastOutput(), 0, 0, null);
                g.setColor(Color.GREEN);
                g.translate(200., 100.);
                g.rotate(i * .1);
                g.scale(2., 2.);
                g.drawString("Encoded " + i, -20f, 0f);
                g.dispose();
                imageIcon.setImage(disp);
                System.err.println(disp.getWidth() + " " + disp.getHeight());
                encoder.input(disp);
            }
            imageView.repaint();
            if (i % 50 == 0) {
                System.err.println(i);
            }
            //Thread.sleep(20);
        }
        System.err.println("duration: " + (System.currentTimeMillis() - t) + "ms");
    }
}
