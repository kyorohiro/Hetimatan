package net.hetimatan.net.torrent.client.scenario.task;

import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.scenario.TorrentPieceScenario;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;


public class ScenarioSeeder extends EventTask {
	
	private WeakReference<TorrentPieceScenario> mTorrentScenario = null;

	public ScenarioSeeder(TorrentPieceScenario scenario, EventTaskRunner runner) {
		super(runner);
		mTorrentScenario = new WeakReference<TorrentPieceScenario>(scenario);
	}

	@Override
	public void action() throws Throwable {
		super.action();
		TorrentPieceScenario scenario = mTorrentScenario.get();
		if(scenario == null) {return;}	
		scenario.distributeInOrder();
	}

}
