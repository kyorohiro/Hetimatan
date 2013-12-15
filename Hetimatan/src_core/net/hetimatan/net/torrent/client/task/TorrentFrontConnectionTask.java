package net.hetimatan.net.torrent.client.task;

import java.io.IOException;
import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.client.TorrentClientFront;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;


public class TorrentFrontConnectionTask extends EventTask {

	public static final String TAG = "TorrentFrontConnectionTask";
	private WeakReference<TorrentClientFront> mTorrentFront = null;
	private String mHost = "";
	private int mPort = 0;
	private boolean mIsCon = false;
	private boolean mIsKeep = false;

	public TorrentFrontConnectionTask(
			TorrentClientFront front, 
			String host, int port) {
		mHost = host;
		mPort = port;
		mTorrentFront = new WeakReference<TorrentClientFront>(front);
	}

	@Override
	public String toString() {
		return TAG;
	}

	@Override
	public void action(EventTaskRunner runner) throws Throwable {
		TorrentClientFront front = mTorrentFront.get();
		if(!mIsCon) {
			front.connect(mHost, mPort);
			mIsCon = true;
		}

		if(front.isConnect()) {
			mIsKeep = false;
			TorrentClient peer = front.getTorrentPeer();
			if(peer != null) {
				peer.getDispatcher().dispatchConnection(front);
			}
		} else {
			mIsKeep = true;
		}
	}

	@Override
	public boolean isKeep() {
		return mIsKeep;
	}
	
}
