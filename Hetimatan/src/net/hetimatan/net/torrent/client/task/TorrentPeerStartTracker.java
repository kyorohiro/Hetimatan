package net.hetimatan.net.torrent.client.task;


import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentPeer;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;

public class TorrentPeerStartTracker extends EventTask {
	private WeakReference<TorrentPeer> mServer = null;

	public TorrentPeerStartTracker(TorrentPeer httpServer, EventTaskRunner runner) {
		super(runner);
		mServer = new WeakReference<TorrentPeer>(httpServer);
	}

	@Override
	public void action() throws Throwable {
		TorrentPeer server = mServer.get();
		server.startTracker();
	}
}