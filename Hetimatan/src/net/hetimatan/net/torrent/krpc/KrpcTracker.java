package net.hetimatan.net.torrent.krpc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

public class KrpcTracker {

	public void receive() throws IOException {
		DatagramChannel channel = DatagramChannel.open();
		channel.socket().setReuseAddress(true);
		channel.socket().bind(new InetSocketAddress(8080));
		channel.configureBlocking(false);
	}
}
