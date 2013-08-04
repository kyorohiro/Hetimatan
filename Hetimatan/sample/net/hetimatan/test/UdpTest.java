package net.hetimatan.test;

import java.io.IOException;
import java.net.DatagramSocket;

import net.hetimatan.io.net.KyoroDatagramImpl;
import net.hetimatan.net.torrent.krpc.KrpcTracker;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;
import net.hetimatan.util.http.HttpObject;
import net.hetimatan.util.net.KyoroSocketEventRunner;

public class UdpTest {

	public static void main(String[] args) {
		try {
			start(args);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static KyoroSocketEventRunner mTrackerRunner = null;
	public static KrpcTracker mTracker = null; 
	public static void start(String[] args) throws IOException {
		KrpcTracker tracker = new KrpcTracker();
		KyoroSocketEventRunner runner = tracker.start(null);
		runner.start(null);
		mTrackerRunner = runner;
		runner.pushWork(new TimerTask(), 3000);
		mTracker = tracker;
	}

	public static class TimerTask extends EventTask {
		@Override
		public void action(EventTaskRunner runner) throws Throwable {
			System.out.println("---a----");
			KyoroDatagramImpl send = new KyoroDatagramImpl();
			send.send("d1:t1:ye".getBytes(), HttpObject.address("127.0.0.1", mTracker.getPort()));
			runner.pushWork(this, 3000);
		}
	}
}
