package io.yawp.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.maven.plugin.logging.Log;

public class ShutdownMonitor extends Thread {

	public static final String DEFAULT_PORT = "8359";

	private DevServerMojo mojo;

	private ServerSocket socket;

	private int shutdownPort;

	public ShutdownMonitor(DevServerMojo mojo, int shutdownPort) {
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
			getShutdownMessage();
			mojo.shutdown();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void getShutdownMessage() throws IOException {
		Socket accept = socket.accept();
		BufferedReader reader = new BufferedReader(new InputStreamReader(accept.getInputStream()));
		reader.readLine();
		getLog().info("Stopping devserver...");
		accept.close();
		socket.close();
	}

	private Log getLog() {
		return mojo.getLog();
	}

}
