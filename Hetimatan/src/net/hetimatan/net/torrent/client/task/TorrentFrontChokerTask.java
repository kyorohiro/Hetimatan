package net.hetimatan.net.torrent.client.task;

import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentFront;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;


public class TorrentFrontChokerTask extends EventTask {

	public static final String TAG = "TorrentFrontChokerTask";
	private WeakReference<TorrentFront> mTorrentFront = null;
	private boolean mIsChoke = false;

	public TorrentFrontChokerTask(TorrentFront front, boolean isChoke) {
		mTorrentFront = new WeakReference<TorrentFront>(front);
		mIsChoke = isChoke;
	}

	@Override
	public String toString() {
		return TAG;
	}

	public void isChoke(boolean ischoke) {
		mIsChoke = ischoke;
	}

	@Override
	public void action(EventTaskRunner runner) throws Throwable {
		TorrentFront front = mTorrentFront.get();
		if(mIsChoke) {
			front.sendChoke();			
		} else {
			front.sendUncoke();
		}
	}
}
