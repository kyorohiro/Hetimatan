package net.hetimatan.net.torrent.client.task;

import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentClientFront;
import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;


public class TorrentFrontFirstAction extends EventTask {

	public static final String TAG = "TorrentFrontFirstAction";
	private WeakReference<TorrentClientFront> mTorrentFront = null;
	public TorrentFrontFirstAction(TorrentClientFront front) {
		mTorrentFront = new WeakReference<TorrentClientFront>(front);
	}

	@Override
	public String toString() {
		return TAG;
	}

	@Override
	public void action(EventTaskRunner runner) throws Throwable {
		TorrentClientFront front = mTorrentFront.get();
		System.out.println("-----first----"+front.getDebug());
		front.getSocket().rejectEventTask(this);

		//front.sendBitfield();
		//TorrentClient peer =  front.getTorrentPeer();
		//if(peer == null) {return;}
		//peer.getDispatcher().dispatchConnection(front);

		//if(mTorrentFront.get().getTorrentPeer().isSeeder()) {
		//	front.sendNotinterest();
		//} else {
		//	front.sendInterest();		
		//}
		front.startReceliver();
		//front.getTaskManager().startDownload(front.getTorrentPeer(), front);
	}
}
