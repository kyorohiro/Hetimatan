package net.hetimatan.net.torrent.client.task;

import java.io.IOException;
import java.lang.ref.WeakReference;

import net.hetimatan.io.net.KyoroSelector;
import net.hetimatan.io.net.KyoroSocket;
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
		return false;//mIsKeep;
	}

	private boolean mIsFirst = true;
	private boolean mIsKeep = true;

	private EventTask mNext = null;

	@Override
	public void action() throws Throwable {
		TorrentFront front = mTorrentFront.get();
		if(mIsFirst) {
			front.sendShakehand();
			front.sendBitfield();
			mIsFirst = false;
		} 
		TorrentPeer peer = front.getTorrentPeer();
		KyoroSocket mSocket = front.getSocket();
		if(front.reveiveSH()) {
			mSocket.regist(peer.getSelector(), KyoroSelector.READ);
			mSocket.setEventTaskAtWrakReference(null);
			mIsKeep = false;
			front.revcShakehand();
			if(mNext != null) {
				nextAction(mNext);
			}

		} else {
			mSocket.regist(peer.getSelector(), KyoroSelector.READ);
			mSocket.setEventTaskAtWrakReference(this);
			mIsKeep = true;
			if(mNext == null) {
				mNext = nextAction();
			}
			nextAction(null);

		}
	}
}
