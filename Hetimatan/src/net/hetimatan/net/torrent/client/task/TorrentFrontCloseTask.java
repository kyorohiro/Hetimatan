package net.hetimatan.net.torrent.client.task;

import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentFront;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;


public class TorrentFrontCloseTask extends EventTask {

	public static final String TAG = "TorrentFrontCloseTask";
	private WeakReference<TorrentFront> mTorrentFront = null;
	public TorrentFrontCloseTask(TorrentFront front) {
		mTorrentFront = new WeakReference<TorrentFront>(front);
	}

	@Override
	public String toString() {
		return TAG;
	}

	@Override
	public void action(EventTaskRunner runner) throws Throwable {
		TorrentFront front = mTorrentFront.get();
		front.close();
	}
}
