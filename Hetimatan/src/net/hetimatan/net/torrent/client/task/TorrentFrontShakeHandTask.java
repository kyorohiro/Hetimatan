package net.hetimatan.net.torrent.client.task;

import java.io.IOException;
import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentFront;
import net.hetimatan.net.torrent.client.TorrentPeer;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;


public class TorrentFrontShakeHandTask extends EventTask {

	private WeakReference<TorrentFront> mTorrentFront = null;
	public TorrentFrontShakeHandTask(TorrentFront front, EventTaskRunner runner) {
		super(runner);
		mTorrentFront = new WeakReference<TorrentFront>(front);
	}

	@Override
	public boolean isKeep() {
		return mIsKeep;
	}

	private boolean mIsFirst = true;
	private boolean mIsKeep = true;

	@Override
	public void action() throws Throwable {
		TorrentFront front = mTorrentFront.get();
		if(mIsFirst) {
			front.sendShakehand();
			mIsFirst = false;
		} 
		if(front.reveiveSH()) {
			mIsKeep = false;
			front.revcShakehand();
		} else {
			mIsKeep = true;
		}
	}
}
