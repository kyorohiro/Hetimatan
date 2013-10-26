package net.hetimatan.net.torrent.client.task;


import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.tracker.TrackerRequest;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;

public class TorrentPeerStartTracker extends EventTask {
	public static final String TAG = "TorrentPeerStartTracker";

	private WeakReference<TorrentClient> mServer = null;

	public TorrentPeerStartTracker(TorrentClient httpServer) {
		mServer = new WeakReference<TorrentClient>(httpServer);
	}

	@Override
	public String toString() {
		return TAG;
	}

	@Override
	public void action(EventTaskRunner runner) throws Throwable {
		TorrentClient server = mServer.get();
		if(server.isSeeder()) {
			server.startTracker(TrackerRequest.EVENT_COMPLETED);
		} else {
			server.startTracker(TrackerRequest.EVENT_STARTED);
		}
	}
}