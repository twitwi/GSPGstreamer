package com.heeere.gsp.gstreamer;

/**
 * Hello world!
 *
 */
public class AppUseModuleWithoutPipeline {

    public static void main(String[] args) {
        ImageSource source = new ImageSource();
        source.uri = "/home/twilight/doc/PublicationsAndPresentations/2012-04-gsp-group-meeting/smartkom_small.avi";
        //source.uri = "/home/media/010-Zinzins_L'extraterrestre-xvid.avi";
        source.initSource();
        long t = System.currentTimeMillis();
        for (int i = 0; i < 180000; i++) {
            source.input();
            if (source.getLastOutput() == null) {
                System.err.println("last i: " + i);
                break;
            }
            if (i % 100 == 0) {
                System.err.println(i);
            }
        }
        System.err.println("duration: " + (System.currentTimeMillis() - t) + "ms");
    }
}
