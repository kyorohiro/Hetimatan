package net.hetimatan.net.torrent.client.task;

import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentFront;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;


public class TorrentFrontHaveTask extends EventTask {

	public static final String TAG = "TorrentFrontHaveTask";
	private WeakReference<TorrentFront> mTorrentFront = null;
	private int mIndex = 0;

	public TorrentFrontHaveTask(TorrentFront front, int index) {
		mIndex = index;
		mTorrentFront = new WeakReference<TorrentFront>(front);
	}

	@Override
	public String toString() {
		return TAG;
	}

	@Override
	public void action(EventTaskRunner runner) throws Throwable {
		TorrentFront front = mTorrentFront.get();
		front.sendHave(mIndex);
	}
}
