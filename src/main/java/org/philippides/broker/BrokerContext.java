package org.philippides.broker;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.philippides.Configuration;
import org.philippides.IBrokerContext;
import org.philippides.IMessageStore;
import org.philippides.IThreadManager;
import org.philippides.Terminus;

public final class BrokerContext implements IBrokerContext {
    private Configuration configuration;
    private IMessageStore messageStore;
    private Map<Object, Terminus> terminusById = new ConcurrentHashMap<>();
    private IThreadManager threadManager;

    public BrokerContext(Properties properties, IMessageStore messageStore, IThreadManager threadManager) {
        this.configuration = new Configuration(properties);
        this.messageStore = messageStore;
        this.threadManager = threadManager;
    }

    @Override
    public IMessageStore getMessageStore() {
        return messageStore;
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public Terminus getTerminus(Object terminusId) {
        return terminusById.get(terminusId);
    }

    public IThreadManager getThreadManager() {
        return threadManager;
    }
}