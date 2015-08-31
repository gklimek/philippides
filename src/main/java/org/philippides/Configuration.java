package org.philippides;

import java.net.InetAddress;
import java.util.Properties;

public class Configuration {
    private static final String DEFAULT_HOSTNAME = InetAddress.getLoopbackAddress().getHostAddress();
    private static final int DEFAULT_PORT = 5672;
    private static final int MAX_FRAME_SIZE = 512;
    
    private Properties properties;

    public Configuration(Properties properties) {
        this.properties = properties;
    }
    
    public int getPort() {
        return Integer.parseInt(properties.getProperty("philippides.port", Integer.toString(DEFAULT_PORT)));
    }

    public int getMaxFrameSize() {
        return Integer.parseInt(properties.getProperty("philippides.frame.maxSize", Integer.toString(MAX_FRAME_SIZE)));
    }

    public String getHostName() {
        return properties.getProperty("philippides.hostname", DEFAULT_HOSTNAME);
    }
}
