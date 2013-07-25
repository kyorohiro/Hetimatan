package net.hetimatan.net.torrent.client.task;


import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentPeer;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;

public class TorrentPeerAcceptTask extends EventTask {

	public static final String TAG = "TorrentPeerAcceptTask";
	private WeakReference<TorrentPeer> mServer = null;

	public TorrentPeerAcceptTask(TorrentPeer clientServer) {
		mServer = new WeakReference<TorrentPeer>(clientServer);
	}

	@Override
	public String toString() {
		return TAG;
	}

	@Override
	public void action(EventTaskRunner runner) throws Throwable {
		TorrentPeer server = mServer.get();
		server.accept();
	}
}