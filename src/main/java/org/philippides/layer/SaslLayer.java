package org.philippides.layer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.philippides.IBrokerContext;
import org.philippides.Layer;
import org.philippides.data.IValue;
import org.philippides.data.SaslCode;
import org.philippides.data.SaslInit;
import org.philippides.data.SaslOutcome;
import org.philippides.data.Ubyte;
import org.philippides.frame.Content;
import org.philippides.frame.Frame;
import org.philippides.frame.Reader;
import org.philippides.frame.Writer;
import org.philippides.util.Streams;

public class SaslLayer implements Layer {
    private static final Logger LOG = Logger.getLogger(SaslLayer.class.getName());

    enum State {
        START,
        INIT_RCVD,
        OK_SENT
    }
    
    private State state;
    private IBrokerContext brokerContext;

    public SaslLayer(IBrokerContext brokerContext) {
        this.brokerContext = brokerContext;
        state = State.START;
    }
    
    @Override
    public boolean isDone() {
        return State.OK_SENT.equals(state);
    }

    @Override
    public void onFrame(Frame frame) throws IOException {
        Content content = new Reader().read(frame);
        IValue value = content.getPerformative();
        LOG.info("Value: " + value);
        if (value instanceof SaslInit) {
            state = State.INIT_RCVD;
        } else {
            LOG.warning("Ignoring unexpected value: " + value);
        }
    }

    @Override
    public boolean expectFrame() {
        return State.START.equals(state);
    }

    @Override
    public List<Frame> toSend() throws IOException {
        if (State.INIT_RCVD.equals(state)) {
            state = State.OK_SENT;
            return Arrays.asList(new Writer().write(0, org.philippides.frame.Type.SASL,
                    new Content(new SaslOutcome(new SaslCode(new Ubyte(0)), null), () -> Streams.EMPTY_INPUT_STREAM)));
        }
        return Collections.emptyList();
    }

    @Override
    public boolean innerLayersExpected() {
        return true;
    }
}
