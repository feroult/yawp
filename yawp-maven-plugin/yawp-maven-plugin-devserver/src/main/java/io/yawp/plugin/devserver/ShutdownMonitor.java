package io.yawp.plugin.devserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ShutdownMonitor extends Thread {

    public static final String DEFAULT_PORT = "8359";

    public static final String SHUTDOWN_MESSAGE = "shutdown";

    private MojoWrapper mojo;

    private ServerSocket socket;

    private int shutdownPort;

    public ShutdownMonitor(MojoWrapper mojo, int shutdownPort) {
        this.mojo = mojo;
        this.shutdownPort = shutdownPort;
        listen();
    }

    private void listen() {
        try {
            setDaemon(true);
            setName("yawp-devserver-shutdown-monitor");
            socket = new ServerSocket(shutdownPort, 1, InetAddress.getByName("127.0.0.1"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (getShutdownMessage()) {
                    break;
                }
            }

            socket.close();
            // TODO: using real logging
            System.out.println("Stopping devserver...");
            shutdown();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void shutdown() {
        // no better solution to avoid exceptions when trying to stop jetty
        // server thread
        // the best options would be server.stop()
        System.exit(0);
    }

    private boolean getShutdownMessage() throws IOException {
        Socket accept = socket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(accept.getInputStream()));
        String msg = reader.readLine();
        accept.close();
        return msg != null && msg.equals(SHUTDOWN_MESSAGE);
    }

}
