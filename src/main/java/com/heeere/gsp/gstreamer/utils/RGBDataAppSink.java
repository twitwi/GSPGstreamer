/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.heeere.gsp.gstreamer.utils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.gstreamer.*;
import org.gstreamer.elements.AppSink;
import org.gstreamer.elements.BaseSink;
import org.gstreamer.lowlevel.GstBinAPI;
import org.gstreamer.lowlevel.GstNative;

/**
 *
 * @author twilight
 */
public class RGBDataAppSink extends Bin {

    private static final GstBinAPI gst = GstNative.load(GstBinAPI.class);
    private boolean passDirectBuffer = false;
    private final Listener listener;
    private final AppSink sink;

    public static interface Listener {

        //void rgbFrame(int width, int height, IntBuffer rgb);
        void rgbFrame(int width, int height, ByteBuffer rgb);
    }

    public RGBDataAppSink(String name, int widthOrMinusOne, int heightOrMinusOne, boolean preserveAspectRatio, boolean useRGBInsteadOfBGR, Listener listener) {
        super(initializer(gst.ptr_gst_bin_new(name)));
        this.listener = listener;

        sink = (AppSink) ElementFactory.make("appsink", "VideoSink");
        sink.set("emit-signals", true);
        sink.set("sync", false);
        sink.set("max-buffers", 10);
        sink.set("drop", false);
        sink.connect(new AppSinkNewBufferListener());

        //
        String supplementaryFormatCaps = "";
        if (widthOrMinusOne != -1) {
            supplementaryFormatCaps += ", width=" + widthOrMinusOne;
        }
        if (heightOrMinusOne != -1) {
            supplementaryFormatCaps += ", height=" + heightOrMinusOne;
        }
        if (preserveAspectRatio) {
            supplementaryFormatCaps += ", pixel-aspect-ratio=1/1";
        }
        if (!useRGBInsteadOfBGR) {
            // setting to BGR
            supplementaryFormatCaps += ", blue_mask=(int)" + 0xFF0000 + ", green_mask=(int)" + 0xFF00 + ", red_mask=(int)" + 0xFF;
        }
        //
        // Convert the input into 32bit RGB so it can be fed directly to a BufferedImage
        //
        Element conv = ElementFactory.make("ffmpegcolorspace", "ColorConverter");
        Element videofilter = ElementFactory.make("capsfilter", "ColorFilter");
        videofilter.setCaps(new Caps("video/x-raw-rgb, bpp=24, depth=24" + supplementaryFormatCaps));
        addMany(conv, videofilter, sink);

        Element.linkMany(conv, videofilter, sink);

        //
        // Link the ghost pads on the bin to the sink pad on the convertor
        //
        addPad(new GhostPad("sink", conv.getStaticPad("sink")));
    }

    /**
     * Indicate whether the {@link RGBDataAppSink} should pass the native {@link java.nio.IntBuffer}
     * to the listener, or should copy it to a heap buffer. The default is to
     * pass a heap {@link java.nio.IntBuffer} copy of the data
     *
     * @param passThru If true, pass through the native IntBuffer instead of
     * copying it to a heap IntBuffer.
     */
    public void setPassDirectBuffer(boolean passThru) {
        this.passDirectBuffer = passThru;
    }

    /**
     * Gets the actual gstreamer sink element.
     *
     * @return a AppSink
     */
    public BaseSink getSinkElement() {
        return sink;
    }

    /**
     * A listener class that handles the new-buffer signal from the AppSink
     * element.
     *
     */
    class AppSinkNewBufferListener implements AppSink.NEW_BUFFER {

        @Override
        public void newBuffer(AppSink as) {
            Buffer buffer = sink.pullBuffer();

            Caps caps = buffer.getCaps();
            Structure struct = caps.getStructure(0);

            int width = struct.getInteger("width");
            int height = struct.getInteger("height");
            if (width < 1 || height < 1) {
                return;
            }
            //IntBuffer rgb;
            ByteBuffer rgb;
            if (passDirectBuffer) {
                rgb = buffer.getByteBuffer();//.asIntBuffer();
            } else {
                rgb = ByteBuffer.allocate(buffer.getSize());//IntBuffer.allocate(width * height);
                rgb.put(buffer.getByteBuffer()).flip();//.asIntBuffer()).flip();
            }
            listener.rgbFrame(width, height, rgb);

            //
            // Dispose of the gstreamer buffer immediately to avoid more being
            // allocated before the java GC kicks in
            //
            buffer.dispose();
        }
    }
}
