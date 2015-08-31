package org.philippides;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.philippides.data.Data;
import org.philippides.data.IValue;
import org.philippides.data.Properties;
import org.philippides.data.ValueUtil;

public class Message {
    private Properties properties;
    private Data data;

    public Message(Properties properties, Data data) {
        this.properties = properties;
        this.data = data;
    }

    public Properties getProperties() {
        return properties;
    }

    public Data getData() {
        return data;
    }

    public static Message fromBytes(byte[] payload) {
        Properties properties = null;
        Data data = null;

        InputStream inputStream = new ByteArrayInputStream(payload);
        IValue currentValue = readQuiet(inputStream);

        if (currentValue instanceof Properties) {
            properties = (Properties) currentValue;
            currentValue = readQuiet(inputStream);
        }
        if (currentValue instanceof Data) {
            data = (Data) currentValue;
            currentValue = readQuiet(inputStream);
        }
        
        if (null != currentValue) {
            throw new IllegalStateException("Unexpected value when parsing message: " + currentValue);
        }
        return new Message(properties, data);
    }

    private static IValue readQuiet(InputStream inputStream) {
        IValue value;
        try {
            value = ValueUtil.fromStream(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException("Unexpected IO excpetion while reading in-memory byte array", e);
        }
        return value;
    }

    @Override
    public String toString() {
        return "Message [properties=" + properties + ", data=" + data + "]";
    }

    public byte[] toBytes() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            if (properties != null) {
                properties.write(os);
            }
            if (null != data) {
                data.write(os);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unexpected IO excpetion while writing to in-memory byte array", e);
        }
        return os.toByteArray();
    }
}
