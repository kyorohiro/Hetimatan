package net.hetimatan.net.torrent.client.task;

import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentFront;
import net.hetimatan.net.torrent.client.TorrentPeer;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;


public class TorrentFrontFirstAction extends EventTask {

	public static final String TAG = "TorrentFrontFirstAction";
	private WeakReference<TorrentFront> mTorrentFront = null;
	public TorrentFrontFirstAction(TorrentFront front, EventTaskRunner runner) {
		mTorrentFront = new WeakReference<TorrentFront>(front);
	}

	@Override
	public String toString() {
		return TAG;
	}

	@Override
	public void action(EventTaskRunner runner) throws Throwable {
		TorrentFront front = mTorrentFront.get();
		System.out.println("-----first----"+front.getDebug());
		front.getSocket().rejectEventTask(this);

		//front.sendBitfield();
		TorrentPeer peer =  front.getTorrentPeer();
		if(peer != null) { peer.updateOptimusUnchokePeer(front);}

		if(mTorrentFront.get().getTorrentPeer().isSeeder()) {
			front.sendNotinterest();
		} else {
			front.sendInterest();		
		}
		front.startReceliver();
		front.startDownload();
	}
}
