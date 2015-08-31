package org.philippides.test.integration;

import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import org.apache.qpid.proton.Proton;
import org.apache.qpid.proton.amqp.Binary;
import org.apache.qpid.proton.amqp.messaging.Data;
import org.apache.qpid.proton.message.Message;
import org.apache.qpid.proton.messenger.Messenger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.philippides.Configuration;
import org.philippides.IBrokerContext;
import org.philippides.IMessageStore;
import org.philippides.MessageStore;
import org.philippides.Terminus;
import org.philippides.broker.Broker;
import org.philippides.data.String;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class ConnectionIT {
    private Broker broker;
    private Messenger messenger;
    private IMessageStore messageStore;
    private IBrokerContext brokerContext;
    private Configuration configuration;
    private Terminus terminus1;
    private Runnable afterSubscription = () -> {};
    
    private byte[] msg1Bytes = "Msg 1".getBytes();
    private byte[] msg2Bytes = "Msg 2".getBytes();
    
    private class TestableMessageStore implements IMessageStore {
        private IMessageStore messageStore;

        public TestableMessageStore(IMessageStore messageStore) {
            this.messageStore = messageStore;
        }

        @Override
        public void put(Terminus terminus, org.philippides.Message message) {
            messageStore.put(terminus, message);
        }

        @Override
        public BlockingQueue<org.philippides.Message> subscribe(Terminus terminus) {
            BlockingQueue<org.philippides.Message> queue = messageStore.subscribe(terminus);
            afterSubscription.run();
            return queue;
        }
    }
    
    @Before
    public void setUp() throws Exception {
        configuration = new Configuration(new Properties());
        messageStore = new TestableMessageStore(new MessageStore());
        brokerContext = mock(IBrokerContext.class);
        terminus1 = new Terminus() {};

        when(brokerContext.getMessageStore()).thenReturn(messageStore);
        when(brokerContext.getConfiguration()).thenReturn(configuration);
        when(brokerContext.getTerminus(new String("terminus1"))).thenReturn(terminus1);

        broker = new Broker(brokerContext);
        broker.start(1000);

        messenger = Proton.messenger();
        messenger.start();
    }
    
    @After
    public void tearDown() throws Exception {
        broker.stop(1000);
    }

    @Test
    public void testSendSingle() throws Exception {
        BlockingQueue<org.philippides.Message> queue = messageStore.subscribe(terminus1);
        messenger.put(createProtonMessage(msg1Bytes, "terminus1"));
        messenger.send();
        messenger.stop();
        
        assertArrayEquals(msg1Bytes, Optional.ofNullable(queue.poll()).map(m -> m.getData().getValue().bytesValue()).orElse(null));
    }

    @Test
    public void testSendMultiple() throws Exception {
        BlockingQueue<org.philippides.Message> queue = messageStore.subscribe(terminus1);
        messenger.put(createProtonMessage(msg1Bytes, "terminus1"));
        messenger.put(createProtonMessage(msg2Bytes, "terminus1"));
        messenger.send();
        messenger.stop();
        
        assertArrayEquals(msg1Bytes, Optional.ofNullable(queue.poll()).map(m -> m.getData().getValue().bytesValue()).orElse(null));
        assertArrayEquals(msg2Bytes, Optional.ofNullable(queue.poll()).map(m -> m.getData().getValue().bytesValue()).orElse(null));
    }

    @Test
    public void testSendSingleLarge() throws Exception {
        BlockingQueue<org.philippides.Message> queue = messageStore.subscribe(terminus1);
        byte[] msgContent = createLargeContent();
        messenger.put(createProtonMessage(msgContent, "terminus1"));
        messenger.send();
        messenger.stop();

        assertArrayEquals(msgContent, Optional.ofNullable(queue.poll()).map(m -> m.getData().getValue().bytesValue()).orElse(null));
    }

    private byte[] createLargeContent() {
        byte [] msgContent = new byte[1000];
        for(int i=0; i< msgContent.length; i++) {
            msgContent[i] = (byte) i;
        }
        return msgContent;
    }

    @Test
    public void testRecv() throws Exception {
        afterSubscription = () -> messageStore.put(terminus1, createPhilippidesMessage(msg1Bytes));
        messenger.subscribe("amqp://127.0.0.1:5672/terminus1");
        messenger.setTimeout(300000);
        messenger.recv(1);
        Message received = messenger.get();
        messenger.stop();

        assertMessageContent(msg1Bytes, received);
    }


    @Test
    public void testRecvMultiple() throws Exception {
        afterSubscription = () -> { messageStore.put(terminus1, createPhilippidesMessage(msg1Bytes)); messageStore.put(terminus1, createPhilippidesMessage(msg2Bytes)); };
        messenger.subscribe("amqp://127.0.0.1:5672/terminus1");
        messenger.setTimeout(5000);
        while (messenger.incoming() < 2) {
            messenger.recv(2);
        }
        Message received1 = messenger.get();
        Message received2 = messenger.get();
        messenger.stop();

        assertMessageContent(msg1Bytes, received1);
        assertMessageContent(msg2Bytes, received2);
    }

    private void assertMessageContent(byte[] expectedBytes, Message receivedMessage) {
        assertEquals(receivedMessage.getBody().getClass(), Data.class);
        assertArrayEquals(expectedBytes, ((Data)receivedMessage.getBody()).getValue().getArray());
    }

    private Message createProtonMessage(byte[] msgContent, java.lang.String target) {
        Message message = Proton.message();
        message.setAddress("amqp://127.0.0.1:5672/" + target);
        message.setBody(new Data(new Binary(msgContent)));
        return message;
    }

    private org.philippides.Message createPhilippidesMessage(byte[] msgContent) {
        return new org.philippides.Message(null, new org.philippides.data.Data(new org.philippides.data.Binary(msgContent)));
    }
}
