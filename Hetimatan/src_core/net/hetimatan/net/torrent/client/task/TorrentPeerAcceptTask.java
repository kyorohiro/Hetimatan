package net.hetimatan.net.torrent.client.task;


import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;

public class TorrentPeerAcceptTask extends EventTask {

	public static final String TAG = "TorrentPeerAcceptTask";
	private WeakReference<TorrentClient> mServer = null;

	public TorrentPeerAcceptTask(TorrentClient clientServer) {
		mServer = new WeakReference<TorrentClient>(clientServer);
	}

	@Override
	public String toString() {
		return TAG;
	}

	@Override
	public void action(EventTaskRunner runner) throws Throwable {
		TorrentClient server = mServer.get();
		server.accept();
	}
}