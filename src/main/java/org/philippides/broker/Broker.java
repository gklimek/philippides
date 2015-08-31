package org.philippides.broker;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.philippides.IBrokerContext;
import org.philippides.IClientProcessor;
import org.philippides.PhilippidesException;
import org.philippides.data.Register;
import org.philippides.util.Sockets;


public class Broker {
    private static final int MAXIMUM_WAIT_TIME_IN_MS = 100;
    private static final Logger LOG = Logger.getLogger(Broker.class.getName());

    enum State {
        CREATED,
        STARTING,
        STARTED,
        STOPPING,
        STOPPED
    }
    
    private volatile State state = State.CREATED;
    private ServerSocket serverSocket;
    private final IBrokerContext brokerContext;

    public Broker(IBrokerContext brokerContext) {
        this.brokerContext = brokerContext;
    }
    
    public void start(long timeout) throws InterruptedException {
        changeState(State.STARTING);

        Register.registerEncodings();
        new Thread(() -> listen(brokerContext.getConfiguration().getPort())).start();

        waitFor(State.STARTED, timeout);
    }

    private void listen(int port) {
        createServerSocket(port);
        changeState(State.STARTED);

        while (!State.STOPPING.equals(state)) {
            accept(serverSocket);
        }

        Sockets.quietClose(serverSocket);
        changeState(State.STOPPED);
    }

    private void createServerSocket(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new PhilippidesException(e);
        }
    }

    private void accept(ServerSocket serverSocket) {
        try (Socket clientSocket = serverSocket.accept()) {
            LOG.info("new client");

            process(clientSocket);
        } catch (SocketException ste) {
            if (!"Socket closed".equals(ste.getMessage())) {
                LOG.log(Level.WARNING, "socket exception: ", ste);
            }
        } catch (IOException e) {
            LOG.log(Level.WARNING, "client exception: ", e);
        }
    }
    
    private void process(Socket clientSocket) throws IOException {
        IClientProcessor clientProcessor = new ClientProcessor(brokerContext);
        clientProcessor.process(clientSocket.getInputStream(), clientSocket.getOutputStream());
    }

    public void stop(long timeout) throws InterruptedException {
        changeState(State.STOPPING);

        Sockets.quietClose(serverSocket);
        waitFor(State.STOPPED, timeout);
    }

    public State getState() {
        return state;
    }

    private synchronized void changeState(State newState) {
        state = newState;
        LOG.info("New state: " + newState);
        notifyAll();
    }

    private synchronized void waitFor(State targetState, long timeout) throws InterruptedException {
        long startedAt = System.currentTimeMillis();
        while (state != targetState && (System.currentTimeMillis() - startedAt < timeout)) {
            wait(MAXIMUM_WAIT_TIME_IN_MS);
        }
    }
}
