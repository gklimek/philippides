package org.philippides.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Streams {
    public static final InputStream EMPTY_INPUT_STREAM = new ByteArrayInputStream(new byte[0]);
    
    private Streams() {
    }
    
    public static void copyStream(InputStream is, OutputStream os) throws IOException {
        int read = is.read();
        while (read >= 0) {
            os.write(read);
            read = is.read();
        }
    }
    
    public static int read(InputStream is, int offset, int len, byte[] bytes) throws IOException {
        int readSoFar = 0;
        while (readSoFar < len) {
            int read = is.read(bytes, offset + readSoFar, len - readSoFar);
            Validation.check(-1 != read, () -> new IOException("Unexpected end of stream"));
            readSoFar += read;
        }
        return readSoFar;
    }

    public static byte[] readFully(InputStream payloadStream) throws IOException {
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        int read;
        byte[] buffer = new byte[1024];
        while ((read = payloadStream.read(buffer)) != -1) {
            tmp.write(buffer, 0, read);
        }
        tmp.flush();
        return tmp.toByteArray();
    }
}
