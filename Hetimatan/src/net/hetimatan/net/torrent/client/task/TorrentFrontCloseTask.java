package net.hetimatan.net.torrent.client.task;

import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentClientFront;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;


public class TorrentFrontCloseTask extends EventTask {

	public static final String TAG = "TorrentFrontCloseTask";
	private WeakReference<TorrentClientFront> mTorrentFront = null;
	public TorrentFrontCloseTask(TorrentClientFront front) {
		mTorrentFront = new WeakReference<TorrentClientFront>(front);
	}

	@Override
	public String toString() {
		return TAG;
	}

	@Override
	public void action(EventTaskRunner runner) throws Throwable {
		TorrentClientFront front = mTorrentFront.get();
		front.close();
	}
}
