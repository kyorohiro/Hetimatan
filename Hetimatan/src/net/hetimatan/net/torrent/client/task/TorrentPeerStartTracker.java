package net.hetimatan.net.torrent.client.task;


import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentPeer;
import net.hetimatan.net.torrent.tracker.TrackerRequest;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;

public class TorrentPeerStartTracker extends EventTask {
	public static final String TAG = "TorrentPeerStartTracker";

	private WeakReference<TorrentPeer> mServer = null;

	public TorrentPeerStartTracker(TorrentPeer httpServer, EventTaskRunner runner) {
		super(runner);
		mServer = new WeakReference<TorrentPeer>(httpServer);
	}

	@Override
	public String toString() {
		return TAG;
	}

	@Override
	public void action() throws Throwable {
		TorrentPeer server = mServer.get();
		if(server.isSeeder()) {
			server.startTracker(TrackerRequest.EVENT_COMPLETED);
		} else {
			server.startTracker(TrackerRequest.EVENT_STARTED);
		}
	}
}