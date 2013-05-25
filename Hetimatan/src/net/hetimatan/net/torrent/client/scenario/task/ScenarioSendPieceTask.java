package net.hetimatan.net.torrent.client.scenario.task;

import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentFront;
import net.hetimatan.net.torrent.client.scenario.TorrentPieceScenario;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;


public class ScenarioSendPieceTask extends EventTask {
	
	private WeakReference<TorrentFront> mTorrentFront = null;
	private WeakReference<TorrentPieceScenario> mTorrentScenario = null;

	public ScenarioSendPieceTask(TorrentPieceScenario scenario, TorrentFront front, EventTaskRunner runner) {
		super(runner);
		mTorrentFront = new WeakReference<TorrentFront>(front);
		mTorrentScenario = new WeakReference<TorrentPieceScenario>(scenario);
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
