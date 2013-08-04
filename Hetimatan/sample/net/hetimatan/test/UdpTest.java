package net.hetimatan.test;

import java.io.IOException;

import net.hetimatan.net.torrent.krpc.KrpcTracker;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;
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
	public static void start(String[] args) throws IOException {
		KrpcTracker tracker = new KrpcTracker();
		KyoroSocketEventRunner runner = tracker.start(null);
		runner.start(null);
		mTrackerRunner = runner;
		runner.pushWork(new TimerTask(), 3000);
	}

	public static class TimerTask extends EventTask {
		@Override
		public void action(EventTaskRunner runner) throws Throwable {
			System.out.println("---a----");
			runner.pushWork(this, 3000);
		}
	}
}
