package org.philippides.endpoint;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;
import java.util.logging.Logger;

import org.philippides.Delivery;
import org.philippides.IBrokerContext;
import org.philippides.Message;
import org.philippides.Sender;
import org.philippides.Terminus;
import org.philippides.data.Accepted;
import org.philippides.data.Attach;
import org.philippides.data.Binary;
import org.philippides.data.Boolean;
import org.philippides.data.DeliveryNumber;
import org.philippides.data.DeliveryTag;
import org.philippides.data.Disposition;
import org.philippides.data.Flow;
import org.philippides.data.Handle;
import org.philippides.data.ISource;
import org.philippides.data.ITarget;
import org.philippides.data.MessageFormat;
import org.philippides.data.Role;
import org.philippides.data.SequenceNo;
import org.philippides.data.Source;
import org.philippides.data.String;
import org.philippides.data.Transfer;
import org.philippides.data.TransferNumber;
import org.philippides.data.Uint;
import org.philippides.util.Bytes;

public class Link {
    private static final Logger LOG = Logger.getLogger(Link.class.getName());

    enum State {
        START,
        ATTACH_RCVD,
        ATTACH_SENT,
        ATTACHED,
        ERROR
    }
    
    private State state;

    private IBrokerContext brokerContext;
    private Handle handle;
    private Role role;
    private String name;
    private ISource source;
    private ITarget target;
    
    private Delivery currentDelivery;
    private Queue<Delivery> completedDeliveries = new LinkedList<>();
    private Supplier<Integer> deliveryIdGenerator;
    private BlockingQueue<Message> queue;


    
    public Link(IBrokerContext brokerContext, Handle handle, Supplier<Integer> deliveryIdGenerator, Sender sender) {
        this.brokerContext = brokerContext;
        this.handle = handle;
        this.deliveryIdGenerator = deliveryIdGenerator;
    }

    public void send(Sender sender, int maxBodySize) {
        switch (state) {
        case ATTACH_RCVD:
            sender.send(new Attach(new String(name), handle, role, null, null, source, target, null, null, new SequenceNo(new Uint(0)), null, null, null, null), null);
            sender.send(new Flow(new TransferNumber(new Uint(1)), new Uint(1024), new TransferNumber(new Uint(1)), new Uint(1024), handle, new SequenceNo(new Uint(1)), new Uint(16), null, null, null, null), null);
            state = State.ATTACHED;
            break;
        case ATTACHED:
            doSend(sender, maxBodySize);
            break;
        default:
            break;
        }
    }

    private void doSend(Sender sender, int maxBodySize) {
        if (Role.SENDER.equals(role)) {
            sendMessages(sender, maxBodySize);
        }
        settleDeliveries(sender);
    }

    private void sendMessages(Sender sender, int maxBodySize) {
        Message message;
        while (null != (message = queue.poll())) {
            Integer deliveryId = deliveryIdGenerator.get();
            sender.send(new Transfer(handle, new DeliveryNumber(new Uint(deliveryId)), new DeliveryTag(new Binary(deliveryId.toString().getBytes())), new MessageFormat(new Uint(1)), null, null, null, null, null, null, null), message.toBytes());
        }
    }

    public void onAttach(Attach attach) {
        role = new Role(new Boolean(!attach.getRole().booleanValue()));
        source = attach.getSource();
        target = attach.getTarget();
        name = attach.getName();
        
        Terminus terminus = brokerContext.getTerminus(getTerminusID());
        if (null == terminus) {
            state = State.ERROR;
            throw new IllegalStateException("Terminus " + getTerminusID() + "doesn't exist");
        } else {
            state = State.ATTACH_RCVD;
            queue = brokerContext.getMessageStore().subscribe(terminus);
            brokerContext.getThreadManager().startLinkSendingThread(this, new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        Message message;
                        try {
                            message = queue.take();
                        } catch (InterruptedException e) {
                        }
                    }
                }
            });
        }
    }

    public void onTransfer(Transfer transfer, byte[] payload) {
        if (null == currentDelivery) {
            currentDelivery = new Delivery();
        }
        currentDelivery.addTransfer(transfer.getDeliveryId(), payload);
        if (!Boolean.TRUE.equals(transfer.getMore())) {
            completedDeliveries.add(currentDelivery);
            currentDelivery = null;
        }
        LOG.info("Link [" + handle + "]: transfer: " + transfer + ", payload: " + Bytes.toString(payload));
    }

    private void settleDeliveries(Sender sender) {
        for(Delivery delivery : completedDeliveries) {
            Message message = Message.fromBytes(delivery.getAccumulatedPayload());
            LOG.info("Message: " + message);
            brokerContext.getMessageStore().put(brokerContext.getTerminus(getTerminusID()), message);
            Disposition disposition = new Disposition(role, delivery.getFirst(), delivery.getLast(), Boolean.TRUE, new Accepted(), Boolean.FALSE);
            sender.send(disposition, null);
            LOG.info("Disposing delivery: " + delivery);
        }
        completedDeliveries.clear();
    }

    private Object getTerminusID() {
        return source instanceof Source ? ((Source)source).getAddress() : null;
    }
}
