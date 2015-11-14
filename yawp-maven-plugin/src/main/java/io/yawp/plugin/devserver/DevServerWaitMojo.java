package io.yawp.plugin.devserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "devserver_wait")
public class DevServerWaitMojo extends DevserverAbstractMojo {

	private static final int SLEEP_MILLIS = 500;

	private static final int MAX_RETRIES = 2 * 60;

	public void execute() throws MojoExecutionException {
		waitSever();
	}

	private void waitSever() {
		int retries = 0;

		while (retries < MAX_RETRIES) {
			try {
				tryToConnect();
				break;
			} catch (IOException e) {
				retries++;
				sleep();
				continue;
			}
		}

		if (retries == MAX_RETRIES) {
			throw new RuntimeException(String.format("Reached max retries"));
		}
	}

	private void sleep() {
		try {
			Thread.sleep(SLEEP_MILLIS);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private void tryToConnect() throws IOException, UnknownHostException {
		Socket socket = new Socket(InetAddress.getByName("127.0.0.1"), getShutdownPort());
		socket.close();
	}

}