package _test;

import net.hetimatan.net.torrent.tracker.TrackerServer;

public class Main {

	public static void main(String[] args) {
		System.out.println("--test--");
		TrackerServer server = new TrackerServer();
		server.setPort(TrackerServer.DEFAULT_TRACKER_PORT);
	}
}
