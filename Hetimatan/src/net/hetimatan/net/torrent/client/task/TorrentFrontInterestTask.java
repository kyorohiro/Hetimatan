package net.hetimatan.net.torrent.client.task;

import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentFront;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;


public class TorrentFrontInterestTask extends EventTask {
	public static final String TAG  = "TorrentFrontInterestTask";
	private WeakReference<TorrentFront> mTorrentFront = null;
	public TorrentFrontInterestTask(TorrentFront front) {
		mTorrentFront = new WeakReference<TorrentFront>(front);
	}

	@Override
	public String toString() {
		return TAG;
	}

	@Override
	public void action(EventTaskRunner runner) throws Throwable {
		TorrentFront front = mTorrentFront.get();
		front.sendInterest();
	}
}
