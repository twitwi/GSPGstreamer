/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.heeere.gsp.gstreamer.utils;

import java.awt.image.DataBuffer;
import java.nio.ByteBuffer;

/**
 *
 * @author emonet
 */
public class ByteBufferDataBuffer extends DataBuffer {

    private ByteBuffer b;

    public ByteBufferDataBuffer(ByteBuffer b) {
        super(DataBuffer.TYPE_BYTE, b.capacity());
        this.b = b;
    }

    @Override
    public int getElem(int bank, int i) {
        return b.get(i);
    }

    @Override
    public void setElem(int bank, int i, int val) {
        b.put(i, (byte) val);
        //throw new UnsupportedOperationException("Won't write to a ByteBuffer based DataBuffer");
    }

}
