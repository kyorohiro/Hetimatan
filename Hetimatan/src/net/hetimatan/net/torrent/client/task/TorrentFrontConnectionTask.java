package net.hetimatan.net.torrent.client.task;

import java.io.IOException;
import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentFront;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;


public class TorrentFrontConnectionTask extends EventTask {

	public static final String TAG = "TorrentFrontConnectionTask";
	private WeakReference<TorrentFront> mTorrentFront = null;
	private String mHost = "";
	private int mPort = 0;
	private boolean mIsCon = false;
	private boolean mIsKeep = false;

	public TorrentFrontConnectionTask(
			TorrentFront front, EventTaskRunner runner,
			String host, int port) {
		mHost = host;
		mPort = port;
		mTorrentFront = new WeakReference<TorrentFront>(front);
	}

	@Override
	public String toString() {
		return TAG;
	}

	@Override
	public void action(EventTaskRunner runner) throws Throwable {
		TorrentFront front = mTorrentFront.get();
		if(!mIsCon) {
			front.connect(mHost, mPort);
			mIsCon = true;
		}

		if(front.isConnect()) {
			mIsKeep = false;
		} else {
			mIsKeep = true;
		}
	}

	@Override
	public boolean isKeep() {
		return mIsKeep;
	}
	
}
