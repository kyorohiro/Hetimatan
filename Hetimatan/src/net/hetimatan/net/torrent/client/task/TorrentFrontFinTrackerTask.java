package net.hetimatan.net.torrent.client.task;

import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.senario.TorrentClientGetPeerListSenario;
import net.hetimatan.net.torrent.client.senario.TorrentClientUploadSenario;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;


public class TorrentFrontFinTrackerTask extends EventTask {
	
	public static final String TAG = "TorrentFrontFinTrackerTask";
	private WeakReference<TorrentClientGetPeerListSenario> mTorrentScenario = null;

	public TorrentFrontFinTrackerTask(TorrentClientGetPeerListSenario scenario) {
		mTorrentScenario = new WeakReference<TorrentClientGetPeerListSenario>(scenario);
	}

	@Override
	public String toString() {
		return TAG;
	}

	@Override
	public void action(EventTaskRunner runner) throws Throwable {
		TorrentClientGetPeerListSenario scenario = mTorrentScenario.get();
		if(scenario == null) {return;}	
		scenario.onFinTracker();
	}

}
