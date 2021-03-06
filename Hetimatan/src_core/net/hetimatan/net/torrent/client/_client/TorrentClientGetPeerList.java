package net.hetimatan.net.torrent.client._client;

import java.io.IOException;
import java.lang.ref.WeakReference;


import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.client.TorrentHistory;
import net.hetimatan.net.torrent.tracker.TrackerClient;
import net.hetimatan.net.torrent.tracker.TrackerRequest;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;
import net.hetimatan.util.event.net.KyoroSocketEventRunner;

/**
 * 
 * delegate get peer list task from TorrentClient
 * get peer list from tracker. 
 * reserve next request task.
 * 
 *  (0) startTracker();
 *  (1) reserveNextTrackerRequest()
 * 
 */
public class TorrentClientGetPeerList {
	private TrackerClient mTrackerClient                   = null;
	private OnResponseFromTracker mOnReponseFromTracker          = null;
	private WeakReference<TorrentClient> mUploadTargetPeer = null;
	private TorrentPeerStartTracker mTrackerTask = null;

	public TorrentClientGetPeerList(TorrentClient target, MetaFile metafile, String peerId) throws IOException {
		mUploadTargetPeer = new WeakReference<TorrentClient>(target);
		mTrackerClient = new TrackerClient(metafile, peerId);
		mTrackerTask = new TorrentPeerStartTracker(target);
	}

	public EventTask getStartTrackerTask() {
		return mTrackerTask;
	}

	public TrackerClient getTrackerClient() {
		return mTrackerClient;
	}

	public void setClientPort(int port) {
		mTrackerClient.setClientPort(port);
	}

	public void startTracker(KyoroSocketEventRunner runner, String event, EventTask last, long downloaded, long uploaded) {
		TorrentHistory.get().pushMessage("tracker:"+event+","+ downloaded+","+ uploaded+"\n");
		mTrackerClient.update(event, downloaded, uploaded);
		mTrackerClient.startTask(runner, last);
	}

	public void startToGetPeerListFromTracker(KyoroSocketEventRunner runner, TorrentClient client, String event, long downloaded, long uploaded) {
		mOnReponseFromTracker = new OnResponseFromTracker(this);
		startTracker(runner, event, mOnReponseFromTracker, downloaded, uploaded);
	}

	public void dispatchMessage() throws IOException {
	 	TorrentClient peer = mUploadTargetPeer.get();
	 	if(peer == null) {return;}
	 	peer.getDispatcher().dispatchTrackerResponse(peer, peer.getTracker());
	}

	public void reserveNextTrackerRequest() { 
	 	TorrentClient peer = mUploadTargetPeer.get();
	 	if(peer == null) {return;}
	 	TrackerClient client = peer.getTracker();
	 	setTrackerTask(client.getIntervalPerSec()*1000);
	}

	public void setTrackerTask(int timeout) {
	 	TorrentClient peer = mUploadTargetPeer.get();
		if(mTrackerTask == null) {
			mTrackerTask = new TorrentPeerStartTracker(peer);
		}
		peer.getClientRunner().releaseTask(mTrackerTask);		
		peer.getClientRunner().pushTask(mTrackerTask, timeout);
	}

	public static class OnResponseFromTracker extends EventTask {
		public static final String TAG = "TorrentFrontFinTrackerTask";
		private WeakReference<TorrentClientGetPeerList> mTorrentScenario = null;

		public OnResponseFromTracker(TorrentClientGetPeerList scenario) {
			mTorrentScenario = new WeakReference<TorrentClientGetPeerList>(scenario);
		}

		@Override
		public String toString() {
			return TAG;
		}

		@Override
		public void action(EventTaskRunner runner) throws Throwable {
			TorrentClientGetPeerList scenario = mTorrentScenario.get();
			if(scenario == null) {return;}	
			scenario.dispatchMessage();
			scenario.reserveNextTrackerRequest();
		}
	}

	public static class TorrentPeerStartTracker extends EventTask {
		public static final String TAG = "TorrentPeerStartTracker";

		private WeakReference<TorrentClient> mServer = null;

		public TorrentPeerStartTracker(TorrentClient httpServer) {
			mServer = new WeakReference<TorrentClient>(httpServer);
		}

		@Override
		public String toString() {
			return TAG;
		}

		@Override
		public void action(EventTaskRunner runner) throws Throwable {
			TorrentClient server = mServer.get();
			if(server.isSeeder()) {
				server.startTracker(TrackerRequest.EVENT_COMPLETED);
			} else {
				server.startTracker(TrackerRequest.EVENT_STARTED);
			}
		}
	}
}
