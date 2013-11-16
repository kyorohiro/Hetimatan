package net.hetimatan.net.torrent.client.task;

import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentClientFront;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;


public class TorrentFrontChokerTask extends EventTask {

	public static final String TAG = "TorrentFrontChokerTask";
	private WeakReference<TorrentClientFront> mTorrentFront = null;
	private boolean mIsChoke = false;

	public TorrentFrontChokerTask(TorrentClientFront front, boolean isChoke) {
		mTorrentFront = new WeakReference<TorrentClientFront>(front);
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
		TorrentClientFront front = mTorrentFront.get();
		if(mIsChoke) {
			front.sendChoke();			
		} else {
			front.sendUncoke();
		}
	}
}
