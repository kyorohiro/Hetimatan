package net.hetimatan.net.torrent.client.task;


import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;

public class TorrentPeerBootTask extends EventTask {
	public static final String TAG = "TorrentPeerBootTask";
	private WeakReference<TorrentClient> mServer = null;

	public TorrentPeerBootTask(TorrentClient httpServer) {
		mServer = new WeakReference<TorrentClient>(httpServer);
	}

	@Override
	public String toString() {
		return TAG;
	}

	@Override
	public void action(EventTaskRunner runner) throws Throwable {
		TorrentClient server = mServer.get();
		server.boot();
	}
}