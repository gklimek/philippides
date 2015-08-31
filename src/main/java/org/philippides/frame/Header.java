package org.philippides.frame;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import org.philippides.util.Identity;
import org.philippides.util.Validation;

public class Header implements Serializable {
    private static final String PROTOCOL_NAME = "AMQP";
    private static final int PROTOCOL_CODE_OFFSET = PROTOCOL_NAME.length();
    private static final int MAJOR_OFFSET = PROTOCOL_CODE_OFFSET + 1;
    private static final int MINOR_OFFSET = MAJOR_OFFSET + 1;
    private static final int REVISION_OFFSET = MINOR_OFFSET + 1;

    private static final long serialVersionUID = 1L;

    private static final int HASHCODE_PRIME = 31;
    static final int HEADER_SIZE = 8;
    private Type type;
    private int major;
    private int minor;
    private int revision;

    public Header(Type type, int major, int minor, int revision) {
        this.type = type;
        this.major = major;
        this.minor = minor;
        this.revision = revision;
    }

    public static Header fromStream(InputStream is) throws IOException {
        byte[] header = new byte[HEADER_SIZE];
        DataInputStream dis = new DataInputStream(is);
        dis.readFully(header);
        Validation.check(PROTOCOL_NAME.equals(new String(header, 0, PROTOCOL_NAME.length())),
                () -> new IllegalStateException("expected " + PROTOCOL_NAME));
        Type type = Type.fromProtocolCode(header[PROTOCOL_CODE_OFFSET]);
        return new Header(type, header[MAJOR_OFFSET], header[MINOR_OFFSET], header[REVISION_OFFSET]);
    }

    public Type getType() {
        return type;
    }

    public void write(OutputStream os) throws IOException {
        os.write('A');
        os.write('M');
        os.write('Q');
        os.write('P');
        os.write(type.toProtocolCode());
        os.write(major);
        os.write(minor);
        os.write(revision);
    }

    @Override
    public int hashCode() {
        final int prime = HASHCODE_PRIME;
        int result = 1;
        result = prime * result + major;
        result = prime * result + minor;
        result = prime * result + revision;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return Identity.isEqual(this, obj, (Header o) -> major == o.major && minor == o.minor && revision == o.revision && type == o.type);
    }

    @Override
    public String toString() {
        return "Header [type=" + type + ", major=" + major + ", minor="
                + minor + ", revision=" + revision + "]";
    }
}