package net.hetimatan.net.torrent.client.task;

import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentFront;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;


public class TorrentFrontSendPieceTask extends EventTask {
	
	private WeakReference<TorrentFront> mTorrentFront = null;

	public TorrentFrontSendPieceTask(TorrentFront front, EventTaskRunner runner) {
		super(runner);
		mTorrentFront = new WeakReference<TorrentFront>(front);
	}

	@Override
	public void action() throws Throwable {
		super.action();
		TorrentFront front = mTorrentFront.get();
		if(front == null) {return;}	
//		front.sendPiece(mIndex);
		front.sendPiece();
	}
}
