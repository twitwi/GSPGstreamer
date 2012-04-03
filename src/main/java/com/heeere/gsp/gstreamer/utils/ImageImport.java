/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.heeere.gsp.gstreamer.utils;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;

/**
 *
 * @author remonet
 */
public class ImageImport {

    public static BufferedImage createBufferedImage(int w, int h, int lineStep, int pixelStep, int[] indices, ByteBuffer imageData) {
        ComponentSampleModel sampleModel = new ComponentSampleModel(DataBuffer.TYPE_BYTE, w, h, pixelStep, lineStep, indices);
        ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
        ComponentColorModel colorModel = new ComponentColorModel(colorSpace, false, false, ColorModel.OPAQUE, DataBuffer.TYPE_BYTE);
        WritableRaster raster = Raster.createWritableRaster(sampleModel, new ByteBufferDataBuffer(imageData), null);
        //raster = colorModel.createCompatibleWritableRaster(w, h);
        BufferedImage bufferedImage = new BufferedImage(colorModel, raster, false, new java.util.Hashtable<String, String>());
        return bufferedImage;
    }
}
