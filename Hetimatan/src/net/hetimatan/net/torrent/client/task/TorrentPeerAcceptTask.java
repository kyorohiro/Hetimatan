package net.hetimatan.net.torrent.client.task;


import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentPeer;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;

public class TorrentPeerAcceptTask extends EventTask {

	private WeakReference<TorrentPeer> mServer = null;

	public TorrentPeerAcceptTask(TorrentPeer clientServer, EventTaskRunner runner) {
		super(runner);
		mServer = new WeakReference<TorrentPeer>(clientServer);
	}

	@Override
	public void action() throws Throwable {
		TorrentPeer server = mServer.get();
		server.accept();
	}
}