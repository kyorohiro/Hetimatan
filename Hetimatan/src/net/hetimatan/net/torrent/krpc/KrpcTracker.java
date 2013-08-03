package net.hetimatan.net.torrent.krpc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

import net.hetimatan.io.net.KyoroDatagramImpl;
import net.hetimatan.io.net.KyoroSelector;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;
import net.hetimatan.util.net.KyoroSocketEventRunner;

public class KrpcTracker {
	private int mPort = 10001;
	private KyoroDatagramImpl mReceiver = null;
	private EventTask mReceiveTask = null;

	public void boot() throws IOException {
		mReceiver = new KyoroDatagramImpl();
		for(int i=0;i<100;i++) {
			try {
				mReceiver.bind(mPort);
				return;
			} catch(IOException e) {
				mPort++;
			}
		}
		throw new IOException();
	}

	public KyoroSocketEventRunner start (KyoroSocketEventRunner runner) throws IOException {
		if(runner == null) {
			runner = new KyoroSocketEventRunner();
		}
		mReceiver.regist(runner.getSelector(), KyoroSelector.READ);
		mReceiver.setEventTaskAtWrakReference(mReceiveTask, KyoroSelector.READ);
		return runner;
	}

	public void receive() throws IOException {
		DatagramChannel channel = DatagramChannel.open();
		channel.socket().setReuseAddress(true);
		channel.socket().bind(new InetSocketAddress(8080));
		channel.configureBlocking(false);
	}

	public class Receivetask extends EventTask {
		@Override
		public void action(EventTaskRunner runner) throws Throwable {
			receive();
		}
	}
}
