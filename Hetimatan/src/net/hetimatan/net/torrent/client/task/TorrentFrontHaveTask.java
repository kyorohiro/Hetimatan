package net.hetimatan.net.torrent.client.task;

import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentClientFront;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;


public class TorrentFrontHaveTask extends EventTask {

	public static final String TAG = "TorrentFrontHaveTask";
	private WeakReference<TorrentClientFront> mTorrentFront = null;
	private int mIndex = 0;

	public TorrentFrontHaveTask(TorrentClientFront front, int index) {
		mIndex = index;
		mTorrentFront = new WeakReference<TorrentClientFront>(front);
	}

	@Override
	public String toString() {
		return TAG;
	}

	@Override
	public void action(EventTaskRunner runner) throws Throwable {
		TorrentClientFront front = mTorrentFront.get();
		front.sendHave(mIndex);
	}
}
