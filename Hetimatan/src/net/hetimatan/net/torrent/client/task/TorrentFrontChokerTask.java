package net.hetimatan.net.torrent.client.task;

import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentFront;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;


public class TorrentFrontChokerTask extends EventTask {

	private WeakReference<TorrentFront> mTorrentFront = null;
	private boolean mIsChoke = false;

	public TorrentFrontChokerTask(TorrentFront front, EventTaskRunner runner, boolean isChoke) {
		super(runner);
		mTorrentFront = new WeakReference<TorrentFront>(front);
		mIsChoke = isChoke;
	}

	public void isChoke(boolean ischoke) {
		mIsChoke = ischoke;
	}

	@Override
	public void action() throws Throwable {
		TorrentFront front = mTorrentFront.get();
		if(mIsChoke) {
			front.sendChoke();			
		} else {
			front.sendUncoke();
		}
	}
}
