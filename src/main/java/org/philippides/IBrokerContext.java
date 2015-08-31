package org.philippides;


public interface IBrokerContext {
    Configuration getConfiguration();
    IMessageStore getMessageStore();
    Terminus getTerminus(Object terminusId);
    IThreadManager getThreadManager();
}
