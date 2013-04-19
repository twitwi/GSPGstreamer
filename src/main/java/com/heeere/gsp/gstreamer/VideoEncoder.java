/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.heeere.gsp.gstreamer;

import com.heeere.gsp.gstreamer.utils.ImageImport;
import fr.prima.gsp.framework.ModuleParameter;
import fr.prima.gsp.framework.spi.AbstractModuleEnablable;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.gstreamer.Buffer;
import org.gstreamer.Caps;
import org.gstreamer.ClockTime;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Format;
import org.gstreamer.Gst;
import org.gstreamer.Pipeline;
import org.gstreamer.State;
import org.gstreamer.elements.AppSrc;
import org.gstreamer.elements.FileSink;

/**
 *
 * @author twilight
 */
public class VideoEncoder extends AbstractModuleEnablable {

    @ModuleParameter
    public String uri = null;
    @ModuleParameter
    public int fps = 25;
    @ModuleParameter
    public int quality = 5;
    @ModuleParameter
    public int speed = 0;

//
    private int width = -1;
    private int height = -1;
    private boolean isInited = false;
    private AppSrc appsrc;
    private Pipeline pipeline;

    @Override
    protected void initModule() {
        Gst.init("VideoEncoderModule", new String[]{});
    }

    private void initIfNeeded(BufferedImage image) {
        if (isInited) {
            return;
        }
        width = image.getWidth();
        height = image.getHeight();

        //System.err.println(width + "×" + height);

        pipeline = new Pipeline("pipeline");
        appsrc = (AppSrc) ElementFactory.make("appsrc", "source");
        final Element srcfilter = ElementFactory.make("capsfilter", "srcfilter");
        Caps fltcaps = new Caps("video/x-raw-rgb, framerate=" + fps + "/1" + ", width=" + width + ", height=" + height + ", bpp=24, depth=24" + ", pixel-aspect-ratio=1/1"
                + ", blue_mask=(int)" + 0xFF0000 + ", green_mask=(int)" + 0xFF00 + ", red_mask=(int)" + 0xFF);
        srcfilter.setCaps(fltcaps);
        appsrc.setFormat(Format.BUFFERS);
        appsrc.set("block", "true");

        // decodebin name=demux ! queue ! ffmpegcolorspace ! vp8enc ! webmmux name=mux ! filesink location=newfile.webm 
        final Element queue = ElementFactory.make("queue", "queue");
        final Element color = ElementFactory.make("colorspace", "colorspace");
        final Element rate = ElementFactory.make("videorate", "videorate");
        final Element vp8 = ElementFactory.make("vp8enc", "vp8enc");
        final Element webm = ElementFactory.make("webmmux", "webmmux");
        final Element filesink = (FileSink) ElementFactory.make("filesink", "filesink");
        filesink.set("location", uri);
        vp8.set("quality", ""+quality);
        vp8.set("speed", ""+speed);

        pipeline.addMany(appsrc, queue, srcfilter, color, rate, vp8, webm, filesink);
        Element.linkMany(appsrc, queue, srcfilter, color, rate, vp8, webm, filesink);

        appsrc.set("emit-signals", true);
        appsrc.connect(new AppSrc.NEED_DATA() {
            byte color = 0;
            byte[] data = new byte[width * height * 3];

            public void needData(AppSrc elem, int size) {
                //System.out.println("NEED_DATA: Element=" + elem.getNativeAddress() + " size=" + size);
            }
        });
        appsrc.connect(new AppSrc.ENOUGH_DATA() {
            public void enoughData(AppSrc elem) {
                //System.out.println("ENOUGH_DATA: Element=" + elem.getNativeAddress());
            }
        });

        pipeline.setState(State.PLAYING);
        isInited = true;
    }

    public void input(BufferedImage image) {
        if (!isEnabled()) {
            return;
        }
        initIfNeeded(image);
        byte color = 0;
        byte[] data = new byte[width * height * 3];

        Buffer buffer = new Buffer(data.length);

        BufferedImage bim = ImageImport.createBufferedImage(width, height, width * 3, 3, new int[]{2, 1, 0}, buffer.getByteBuffer());
        Graphics2D g = bim.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        buffer.setDuration(ClockTime.fromMicros(1000000 / fps));
        //System.err.println("PUSHING " + buffer.getSize());
        appsrc.pushBuffer(buffer);
    }

    public void end() {
        appsrc.endOfStream();
        pipeline.setState(State.NULL);
    }

    @Override
    protected void stopModule() {
        // seems ok to potentially free in end and stopModule
        appsrc.endOfStream();
        pipeline.setState(State.NULL);
    }

    // non-module API
    public void initEncoder() {
        initModule();
    }
}
