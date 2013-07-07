package net.hetimatan.net.torrent.client._peer;

import java.lang.ref.WeakReference;

import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;


public class ScenarioSeeder extends EventTask {
	
	public static final String TAG = "ScenarioSeeder";
	private WeakReference<TorrentPeerPiecer> mTorrentScenario = null;

	public ScenarioSeeder(TorrentPeerPiecer scenario, EventTaskRunner runner) {
		super(runner);
		mTorrentScenario = new WeakReference<TorrentPeerPiecer>(scenario);
	}

	@Override
	public String toString() {
		return TAG;
	}

	@Override
	public void action() throws Throwable {
		super.action();
		TorrentPeerPiecer scenario = mTorrentScenario.get();
		if(scenario == null) {return;}	
		scenario.distributeInOrder();
	}

}
