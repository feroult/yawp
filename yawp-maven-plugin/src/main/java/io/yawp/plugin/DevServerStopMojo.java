package io.yawp.plugin;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "devserver_stop")
public class DevServerStopMojo extends PluginAbstractMojo {

	@Parameter(property = "yawp.shutdownPort", defaultValue = ShutdownMonitor.DEFAULT_PORT)
	private String shutdownPort;

	public void execute() throws MojoExecutionException {
		shutdown();
	}

	private void shutdown() {
		try {
			Socket socket = new Socket(InetAddress.getByName("127.0.0.1"), getShutdownPort());
			socket.close();
		} catch (IOException e) {
		}
	}

	public int getShutdownPort() {
		return Integer.valueOf(shutdownPort);
	}

}