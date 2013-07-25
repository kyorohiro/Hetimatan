package net.hetimatan.net.torrent.client.task;


import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentPeer;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;

public class TorrentPeerBootTask extends EventTask {
	public static final String TAG = "TorrentPeerBootTask";
	private WeakReference<TorrentPeer> mServer = null;

	public TorrentPeerBootTask(TorrentPeer httpServer, EventTaskRunner runner) {
		mServer = new WeakReference<TorrentPeer>(httpServer);
	}

	@Override
	public String toString() {
		return TAG;
	}

	@Override
	public void action(EventTaskRunner runner) throws Throwable {
		TorrentPeer server = mServer.get();
		server.boot();
	}
}