package org.philippides.layer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.philippides.IBrokerContext;
import org.philippides.Layer;
import org.philippides.endpoint.ChannelSender;
import org.philippides.endpoint.Connection;
import org.philippides.frame.Content;
import org.philippides.frame.Frame;
import org.philippides.frame.Reader;
import org.philippides.util.Streams;

public class AmqpLayer implements Layer {
    private static final Logger LOG = Logger.getLogger(AmqpLayer.class.getName());
    
    private Connection connection;
    private IBrokerContext brokerContext;
    
    public AmqpLayer(IBrokerContext brokerContext) {
        this.brokerContext = brokerContext;
        this.connection = new Connection(this.brokerContext);
    }

    @Override
    public boolean isDone() {
        return connection.isDone();
    }

    @Override
    public void onFrame(Frame frame) throws IOException {
        Content content = new Reader().read(frame);
        int channel = frame.getChannel();
        connection.onContent(channel, content);
    }

    @Override
    public boolean expectFrame() {
        return connection.expectInput();
    }

    @Override
    public List<Frame> toSend() {
        List<Frame> toSend = new ArrayList<>();
        connection.send(new ChannelSender() {
            @Override
            public void send(int remoteChannel, Content content) {
                ByteArrayOutputStream frameBytes = new ByteArrayOutputStream();
                try {
                    content.getPerformative().write(frameBytes);
                    if (null != content.getPayloadStream()) {
                        Streams.copyStream(content.getPayloadStream(), frameBytes);
                    }
                } catch (IOException e) {
                    throw new IllegalStateException("Unexpected exception on wrote to bytes buffer", e);
                }
                byte[] bytes = frameBytes.toByteArray();
                Frame frame = Frame.fromBytes(new byte[0], bytes, org.philippides.frame.Type.AMQP, remoteChannel);
                toSend.add(frame);
                LOG.info("Sending to channel " + remoteChannel + ": " + content);
            }
        });
        return toSend;
    }

    @Override
    public boolean innerLayersExpected() {
        return false;
    }
}
