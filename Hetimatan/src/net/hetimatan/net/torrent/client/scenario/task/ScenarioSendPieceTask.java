package net.hetimatan.net.torrent.client.scenario.task;

import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentFront;
import net.hetimatan.net.torrent.client._peer.TorrentPeerPiecer;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;


public class ScenarioSendPieceTask extends EventTask {
	
	private WeakReference<TorrentFront> mTorrentFront = null;
	private WeakReference<TorrentPeerPiecer> mTorrentScenario = null;

	public ScenarioSendPieceTask(TorrentPeerPiecer scenario, TorrentFront front, EventTaskRunner runner) {
		super(runner);
		mTorrentFront = new WeakReference<TorrentFront>(front);
		mTorrentScenario = new WeakReference<TorrentPeerPiecer>(scenario);
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
