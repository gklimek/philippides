package org.philippides.endpoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.philippides.IBrokerContext;
import org.philippides.data.Begin;
import org.philippides.data.Close;
import org.philippides.data.IValue;
import org.philippides.data.Open;
import org.philippides.data.Uint;
import org.philippides.frame.Content;
import org.philippides.frame.Frame;

public class Connection {
    private static final Logger LOG = Logger.getLogger(Connection.class.getName());
    
    enum State {
        START,
        HDR_RCVD,
        HDR_SENT,
        HDR_EXCH,
        OPEN_PIPE,
        OPEN_RCVD,
        OPEN_SENT,
        OPENED,
        OC_PIPE,
        CLOSE_PIPE,
        CLOSE_RCVD,
        CLOSE_SENT,
        END
    }
    
    private Map<Integer, Session> sessionsByRemoteChannel = new HashMap<>();
    private Map<Integer, Session> sessionsByLocalChannel = new HashMap<>();
    private List<Session> sessions = new ArrayList<>();
    
    private State state;
    private IBrokerContext brokerContext;
    
    public Connection(IBrokerContext brokerContext) {
        this.brokerContext = brokerContext;
        this.state = State.HDR_EXCH;
    }

    public boolean expectInput() {
        return State.HDR_EXCH.equals(state) || State.OPENED.equals(state);
    }

    public void send(ChannelSender channelSender) {
        int maxFrameSize = brokerContext.getConfiguration().getMaxFrameSize();
        switch (state) {
        case HDR_EXCH :
        case OPEN_RCVD :
            sendOpen(channelSender, maxFrameSize);
            state = State.HDR_EXCH.equals(state) ? State.OPEN_SENT : State.OPENED;
            break;
        default:
            break;
        }
        switch (state) {
        case OPEN_RCVD :
        case OPENED:
            sendSessions(channelSender, maxFrameSize);
            break;
        default:
            break;
        }
        if (state == State.CLOSE_RCVD) {
            channelSender.send(0, new Content(new Close(null)));
            state = State.END;
        }
    }

    private void sendOpen(ChannelSender channelSender, int maxFrameSize) {
        channelSender.send(0, new Content(new Open(
                new org.philippides.data.String("1"),
                new org.philippides.data.String(brokerContext.getConfiguration().getHostName()),
                new Uint(maxFrameSize), null, null, null, null, null, null, null)));
    }

    private void sendSessions(ChannelSender channelSender, int maxFrameSize) {
        for(Session session : sessions) {
            int maxBodySize = maxFrameSize - Frame.MAX_HEADER_SIZE;
            session.send(channelSender, maxBodySize);
        }
    }

    public void onOpen() {
        switch (state) {
        case HDR_EXCH :
            state = State.OPEN_RCVD;
            break;
        case OPEN_SENT :
            state = State.OPENED;
            break;
        default:
            throw new IllegalStateException("Unexpected open at connection state: " + state);
        }
    }

    private void onClose() {
        if (state == State.OPENED) {
            state = State.CLOSE_RCVD;
        } else {
            throw new IllegalStateException("Unexpected close at connection state: " + state);
        }
    }

    public void onContent(int remoteChannel, Content content) throws IOException {
        IValue performative = content.getPerformative();
        LOG.info("Performative on channel " + remoteChannel + ": " + performative);
        if (performative instanceof Open) {
            onOpen();
        } else if (performative instanceof Begin) {
            onBegin(remoteChannel);
        } else if (performative instanceof Close) {
            onClose();
        } else {
            sessionsByRemoteChannel.get(remoteChannel).onContent(content);
        }
    }

    private void onBegin(int remoteChannel) {
        int localChannel = findUnusedLocalChannel();
        Session session = new Session(brokerContext, localChannel, remoteChannel);
        sessions.add(session);
        sessionsByRemoteChannel.put(remoteChannel, session);
        sessionsByLocalChannel.put(localChannel, session);
        session.onBegin();
    }

    private int findUnusedLocalChannel() {
        return 1;
    }

    public boolean isDone() {
        return State.END.equals(state);
    }
}
