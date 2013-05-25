package net.hetimatan.net.torrent.client.task;

import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentFront;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;


public class TorrentFrontConnectionTask extends EventTask {

	private WeakReference<TorrentFront> mTorrentFront = null;
	private String mHost = "";
	private int mPort = 0;
	public TorrentFrontConnectionTask(
			TorrentFront front, EventTaskRunner runner,
			String host, int port) {
		super(runner);
		mHost = host;
		mPort = port;
		mTorrentFront = new WeakReference<TorrentFront>(front);
	}

	private boolean isCon = false;
	@Override
	public void action() throws Throwable {
		TorrentFront front = mTorrentFront.get();
		if(!isCon) {
			front.connect(mHost, mPort);
			isCon = true;
		}

		if(front.isConnect()) {
			front.startConnectForAccept();
			nextAction(null);
		} else {
			nextAction(this);
		}
	}
}
