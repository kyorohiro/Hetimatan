package net.hetimatan.net.torrent.client.senario;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.util.Iterator;

import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.client.TorrentHistory;
import net.hetimatan.net.torrent.tracker.TrackerClient;
import net.hetimatan.net.torrent.tracker.TrackerPeerInfo;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;
import net.hetimatan.util.event.net.KyoroSocketEventRunner;

//
// もともとぬ、TorrentFront、TorrentClientにあった機能を
// 機能ごとに別のクラスに委譲したい。
//
// このクラスもその候補
// メソッドだけ抜き出した状態
//
/**
 * Trackerから取得したTorrentクライアント接続する。
 * 次にTrackerへアクセスするタイミングを設定する。
 *
 */
public class TorrentClientGetPeerListSenario {
	private TrackerClient mTrackerClient                   = null;
	private OnResponseFromTracker mFinTrackerTask          = null;
	private WeakReference<TorrentClient> mUploadTargetPeer = null;

	public TorrentClientGetPeerListSenario(TorrentClient target, MetaFile metafile, String peerId) throws URISyntaxException, IOException {
		mUploadTargetPeer = new WeakReference<TorrentClient>(target);
		mTrackerClient = new TrackerClient(metafile, peerId);
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
		mFinTrackerTask = new OnResponseFromTracker(this);
		startTracker(runner, event, mFinTrackerTask, downloaded, uploaded);
	}

	public TrackerClient getTrackerClient() {
		return mTrackerClient;
	}

	public void startConnection() throws IOException {
	 	TorrentClient peer = mUploadTargetPeer.get();
	 	if(peer == null) {return;}
	 	if(peer.isSeeder()) {
	 		return ;
	 	}
	 	TrackerClient client = peer.getTracker();
	 	Iterator<TrackerPeerInfo> peers32 = client.getPeer32();
	 	if(!peer.isSeeder()) {//todo
	 		while(peers32.hasNext()) {
	 			TrackerPeerInfo targetPeer = peers32.next();
	 			peer.startConnect(targetPeer);
	 		}
	 	}
	 	client.clearPeer32();
	}

	public void reserveNextTrackerRequest() { 
	 	TorrentClient peer = mUploadTargetPeer.get();
	 	if(peer == null) {return;}
	 	TrackerClient client = peer.getTracker();
	 	peer.setTrackerTask(client.getIntervalPerSec()*1000);
	}

	public static class OnResponseFromTracker extends EventTask {
		
		public static final String TAG = "TorrentFrontFinTrackerTask";
		private WeakReference<TorrentClientGetPeerListSenario> mTorrentScenario = null;

		public OnResponseFromTracker(TorrentClientGetPeerListSenario scenario) {
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
			scenario.startConnection();
			scenario.reserveNextTrackerRequest();
		}

	}
}
