package net.hetimatan.net.torrent.client.task;


import java.lang.ref.WeakReference;

import net.hetimatan.io.net.KyoroSelector;
import net.hetimatan.io.net.KyoroSocket;
import net.hetimatan.net.torrent.client.TorrentFront;
import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;


public class TorrentFrontShakeHandTask extends EventTask {
	public static final String TAG = "TorrentFrontShakeHandTask";
	private WeakReference<TorrentFront> mTorrentFront = null;
	private boolean mIsFirst = true;
	private boolean mIsKeep = true;
	private boolean mIsNext = true;

	public TorrentFrontShakeHandTask(TorrentFront front) {
		mTorrentFront = new WeakReference<TorrentFront>(front);
	}

	@Override
	public String toString() {
		return TAG;
	}

	@Override
	public boolean isKeep() {
		return mIsKeep;
	}

	@Override
	public boolean isNext() {
		return mIsNext;
	}

	@Override
	public void action(EventTaskRunner runner) throws Throwable {
		TorrentFront front = mTorrentFront.get();

		TorrentClient peer = front.getTorrentPeer();
		KyoroSocket mSocket = front.getSocket();
		if(mIsFirst) {
			front.sendShakehand();
			front.sendBitfield();
			mSocket.regist(peer.getSelector(), KyoroSelector.READ);
			mSocket.setEventTaskAtWrakReference(this, KyoroSelector.READ);
			mIsFirst = false;
			// kick from socket event task
			mIsKeep = false;
			mIsNext = false;
		} else {
			if(front.parseableShakehand()) {
				mSocket.regist(peer.getSelector(), KyoroSelector.READ);
				mSocket.setEventTaskAtWrakReference(null,KyoroSelector.READ);
				front.revcShakehand();
				mIsKeep = false;
				mIsNext = true;
			} else {
				mSocket.regist(peer.getSelector(), KyoroSelector.READ);
				mSocket.setEventTaskAtWrakReference(this,KyoroSelector.READ);
				// kick from socket event task
				mIsKeep = false;
				mIsNext = false;
			}
		}
	}
}
