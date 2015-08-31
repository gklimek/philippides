package org.philippides.frame;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.philippides.util.Bytes;
import org.philippides.util.Streams;

public class Frame {
    private static final int DATA_OFFSET_WORD_SIZE = 4;
    private static final int REGULAR_HEADER_SIZE = 8;
    private static final int MAX_EXTENDED_HEADER_SIZE = 0;
    
    public static final int MAX_HEADER_SIZE = REGULAR_HEADER_SIZE + MAX_EXTENDED_HEADER_SIZE;

    private Type type;
    private int channel;
    private byte[] extendedHeaderBytes;
    private byte[] bodyBytes;

    private Frame(byte[] extendedHeader, byte[] body, Type type, int channel) {
        this.extendedHeaderBytes = extendedHeader;
        this.bodyBytes = body;
        this.type = type;
        this.channel = channel;
    }
    
    public Type getType() {
        return type;
    }

    public int getChannel() {
        return channel;
    }

    public void write(OutputStream os) throws IOException {
        Bytes.write4ByteInteger(os, REGULAR_HEADER_SIZE + extendedHeaderBytes.length + bodyBytes.length);
        os.write((REGULAR_HEADER_SIZE + extendedHeaderBytes.length) / DATA_OFFSET_WORD_SIZE);
        os.write(type.toCode());
        Bytes.write2ByteInteger(os, channel);
        os.write(extendedHeaderBytes);
        os.write(bodyBytes);
    }

    public static Frame read(InputStream is) throws IOException {
        long size = Bytes.read4ByteInteger(is);
        int doff = Bytes.read1ByteInteger(is);
        Type type = Type.fromCode(Bytes.read1ByteInteger(is));
        int channel = Bytes.read2ByteInteger(is);
        byte[] extendedHeader = new byte[doff * DATA_OFFSET_WORD_SIZE - Header.HEADER_SIZE];
        Streams.read(is, 0, extendedHeader.length, extendedHeader);
        byte[] body = new byte[(int) (size - doff * DATA_OFFSET_WORD_SIZE)];
        Streams.read(is, 0, body.length, body);
        return new Frame(extendedHeader, body, type, channel);
    }
    
    public static Frame fromBytes(byte[] extendedHeaderBytes, byte[] bodyBytes, Type type, int channel) {
        return new Frame(Arrays.copyOf(extendedHeaderBytes, extendedHeaderBytes.length),
                Arrays.copyOf(bodyBytes, bodyBytes.length), type, channel);
    }
    
    public ByteArrayInputStream getBodyInputStream(int offset) {
        int tmp = offset;
        if (tmp < 0) {
            tmp = bodyBytes.length - (-tmp);
        }
        return new ByteArrayInputStream(bodyBytes, tmp, bodyBytes.length - tmp);
    }

    public ByteArrayInputStream getBodyInputStream() {
        return getBodyInputStream(0);
    }

    @Override
    public String toString() {
        return "Frame [type=" + type + ", channel=" + channel + ", extendedHeaderSize=" + extendedHeaderBytes.length
                + ", bodySize=" + bodyBytes.length + "]";
    }
}
