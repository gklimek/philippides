package org.philippides.endpoint;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.philippides.IBrokerContext;
import org.philippides.data.Attach;
import org.philippides.data.Begin;
import org.philippides.data.Handle;
import org.philippides.data.IValue;
import org.philippides.data.Transfer;
import org.philippides.data.TransferNumber;
import org.philippides.data.Uint;
import org.philippides.data.Ushort;
import org.philippides.frame.Content;
import org.philippides.util.Streams;

public class Session {
    private static final Logger LOG = Logger.getLogger(Session.class.getName());

    enum State {
        UNMAPPED,
        BEGIN_SENT,
        BEGIN_RCVD,
        MAPPED,
        END_SENT,
        END_RCVD,
        DISCARDING
    }
    
    private State state;
    private IBrokerContext brokerContext;
    private int localChannel;
    private int remoteChannel;
    private Map<Long, Link> linksByHandle = new HashMap<>();
    private AtomicInteger nextDeliveryId = new AtomicInteger();

    public Session(IBrokerContext brokerContext, int localChannel, int remoteChannel) {
        this.brokerContext = brokerContext;
        this.localChannel = localChannel;
        this.remoteChannel = remoteChannel;
        this.state = State.UNMAPPED;
    }
    
    public int getLocalChannel() {
        return localChannel;
    }

    public int getRemoteChannel() {
        return remoteChannel;
    }

    public int getNextDeliveryId() {
        return nextDeliveryId.getAndIncrement();
    }

    public void onBegin() {
        state = State.BEGIN_RCVD;
        LOG.info("Begin received, localchannel: " + localChannel + ", remoteChannel: " + remoteChannel);
    }

    public void send(ChannelSender channelSender, int maxBodySize) {
        if (state == State.BEGIN_RCVD) {
            channelSender.send(remoteChannel, new Content(new Begin(new Ushort(remoteChannel), new TransferNumber(new Uint(1)), new Uint(2), new Uint(2), null, null, null, null)));
            state = State.BEGIN_SENT;
        }
        for(Link link : linksByHandle.values()) {
            link.send((value, payload) -> channelSender.send(remoteChannel, new Content(value, null != payload ? () -> new ByteArrayInputStream(payload) : null)), maxBodySize);
        }
    }

    public void onContent(Content content) throws IOException {
        IValue performative = content.getPerformative();
        if (performative instanceof Attach) {
            Attach attach = (Attach)performative;
            Link link = new Link(brokerContext, attach.getHandle(), this::getNextDeliveryId);
            long handle = attach.getHandle().longValue();
            linksByHandle.put(handle, link);
            link.onAttach(attach);
        } else if (performative instanceof Transfer) {
            Transfer transfer = (Transfer) performative;
            Handle handle = transfer.getHandle();
            Link link = linksByHandle.get(handle.longValue());
            byte[] payload = Streams.readFully(content.getPayloadStream());
            link.onTransfer(transfer, payload);
        }
    }
}
