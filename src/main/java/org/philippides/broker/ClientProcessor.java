package org.philippides.broker;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.philippides.IBrokerContext;
import org.philippides.IClientProcessor;
import org.philippides.Layer;
import org.philippides.NotSuppportedProtocol;
import org.philippides.PhilippidesException;
import org.philippides.frame.Frame;
import org.philippides.frame.Header;
import org.philippides.frame.Type;
import org.philippides.layer.AmqpLayer;
import org.philippides.layer.SaslLayer;
import org.philippides.util.Validation;

public class ClientProcessor implements IClientProcessor {
    private static final Logger LOG = Logger.getLogger(ClientProcessor.class.getName());
    
    static final int VERSION_MAJOR = 1;
    static final int VERSION_MINOR = 0;
    static final int VERSION_REVISION = 0;
    
    private static final Map<Type, Class<? extends Layer>> LAYER_CLASS_BY_TYPE = new HashMap<>();
    
    static {
        LAYER_CLASS_BY_TYPE.put(Type.AMQP, AmqpLayer.class);
        LAYER_CLASS_BY_TYPE.put(Type.SASL, SaslLayer.class);
    }

    private IBrokerContext brokerContext;

    public ClientProcessor(IBrokerContext brokerContext) {
        this.brokerContext = brokerContext;
    }

    @Override
    public void process(InputStream inputStream, OutputStream outputStream) {
        try {
            doProcess(inputStream, outputStream);
        } catch (IOException | NotSuppportedProtocol e) {
            throw new PhilippidesException(e);
        }
    }

    private void doProcess(InputStream inputStream, OutputStream outputStream) throws IOException, NotSuppportedProtocol {
        Frame inboundFrame;

        Layer layer;
        do {
            layer = versionNegotiation(inputStream, outputStream);
            do {
                if (layer.expectFrame()) {
                    inboundFrame = Frame.read(inputStream);
                    LOG.info("Received: " + inboundFrame);
                    layer.onFrame(inboundFrame);
                }
                for(Frame frame : layer.toSend()) {
                    LOG.info("Sending: " + frame);
                    frame.write(outputStream);
                }
            } while (!layer.isDone());
        } while (layer.innerLayersExpected());

        LOG.info("Finished processing");
    }

    private Layer versionNegotiation(InputStream inputStream, OutputStream outputStream) throws IOException,
            NotSuppportedProtocol {
        Header header = Header.fromStream(inputStream);
        Type type = header.getType();
        Layer layer = instantiateLayer(type);
        Validation.check(null != layer, () -> new NotSuppportedProtocol(header));
        Header supportedHeader = new Header(type, VERSION_MAJOR, VERSION_MINOR, VERSION_REVISION);
        supportedHeader.write(outputStream);
        Validation.check(supportedHeader.equals(header), () -> new NotSuppportedProtocol(header));
        return layer;
    }

    private Layer instantiateLayer(Type type) {
        try {
            Constructor<? extends Layer> constructor = LAYER_CLASS_BY_TYPE.get(type).getConstructor(IBrokerContext.class);
            return constructor.newInstance(brokerContext);
        } catch (IllegalArgumentException | InvocationTargetException | SecurityException | NoSuchMethodException
                | InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Unsupported protocol layer: " + type, e);
        }
    }
}
