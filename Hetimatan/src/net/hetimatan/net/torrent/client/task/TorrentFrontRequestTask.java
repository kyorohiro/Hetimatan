package net.hetimatan.net.torrent.client.task;

import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentFront;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;


public class TorrentFrontRequestTask extends EventTask {
	public static final String TAG = "TorrentFrontRequestTask";
	private WeakReference<TorrentFront> mTorrentFront = null;
	public TorrentFrontRequestTask(TorrentFront front, EventTaskRunner runner) {
		mTorrentFront = new WeakReference<TorrentFront>(front);
	}

	@Override
	public String toString() {
		return TAG;
	}

	@Override
	public void action(EventTaskRunner runner) throws Throwable {
		TorrentFront front = mTorrentFront.get();
		front.sendRequest();
	}
}
