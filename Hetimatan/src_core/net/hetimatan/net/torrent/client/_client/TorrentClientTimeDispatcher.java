package net.hetimatan.net.torrent.client._client;

import java.io.IOException;
import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;

public class TorrentClientTimeDispatcher {

	private int mInterval = 1000*60;
	private Time mTimer = new Time();
	private WeakReference<TorrentClient> mTarget = null;
	private boolean mIsStop = false;

	public TorrentClientTimeDispatcher(TorrentClient client, int interval) {
		mTarget = new WeakReference<TorrentClient>(client);
		mInterval = interval;
	}

	public void start(EventTaskRunner runner) throws IOException {
		mIsStop = false;
		mTimer.update(runner);
	}

	public void stop() {
		mIsStop = true;
	}

	public class Time extends EventTask {
		@Override
		public void action(EventTaskRunner runner) throws Throwable {
			update(runner);
		}

		public void update(EventTaskRunner runner) throws IOException {
			TorrentClient client = mTarget.get();
			if(client == null) {;}
			client.getDispatcher().dispatchIntervalAction(client);
			if(!mIsStop) {
				runner.pushTask(this, mInterval);
			}
		}
	}
}
