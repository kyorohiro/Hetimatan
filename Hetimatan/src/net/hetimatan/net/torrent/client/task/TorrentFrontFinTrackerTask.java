package net.hetimatan.net.torrent.client.task;

import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.senario.TorrentClientUploadSenario;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;


public class TorrentFrontFinTrackerTask extends EventTask {
	
	public static final String TAG = "TorrentFrontFinTrackerTask";
	private WeakReference<TorrentClientUploadSenario> mTorrentScenario = null;

	public TorrentFrontFinTrackerTask(TorrentClientUploadSenario scenario) {
		mTorrentScenario = new WeakReference<TorrentClientUploadSenario>(scenario);
	}

	@Override
	public String toString() {
		return TAG;
	}

	@Override
	public void action(EventTaskRunner runner) throws Throwable {
		TorrentClientUploadSenario scenario = mTorrentScenario.get();
		if(scenario == null) {return;}	
		scenario.onFinTracker();
	}

}
