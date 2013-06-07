package net.hetimatan.net.torrent.client.task;

import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentFront;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;


public class TorrentFrontShakeHandTask extends EventTask {

	private WeakReference<TorrentFront> mTorrentFront = null;
	public TorrentFrontShakeHandTask(TorrentFront front, EventTaskRunner runner) {
		super(runner);
		mTorrentFront = new WeakReference<TorrentFront>(front);
	}

	@Override
	public void action() throws Throwable {
		TorrentFront front = mTorrentFront.get();
		front.sendShakehand();
		front.shakehand();
		front.sendBitfield();
//		front.uncoke();
		front.choke();
		if(mTorrentFront.get().getTorrentPeer().isSeeder()) {
			front.notinterest();
		} else {
			front.interest();		
		}
		front.startReceliver();
		front.startDownload();
	}
}
