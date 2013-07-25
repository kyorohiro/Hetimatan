package net.hetimatan.net.torrent.client.task;

import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client._peer.TorrentPeerPiecer;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;


public class TorrentFrontFinTrackerTask extends EventTask {
	
	public static final String TAG = "TorrentFrontFinTrackerTask";
	private WeakReference<TorrentPeerPiecer> mTorrentScenario = null;

	public TorrentFrontFinTrackerTask(TorrentPeerPiecer scenario, EventTaskRunner runner) {
		mTorrentScenario = new WeakReference<TorrentPeerPiecer>(scenario);
	}

	@Override
	public String toString() {
		return TAG;
	}

	@Override
	public void action(EventTaskRunner runner) throws Throwable {
		TorrentPeerPiecer scenario = mTorrentScenario.get();
		if(scenario == null) {return;}	
		scenario.onFinTracker();
	}

}
