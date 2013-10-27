package net.hetimatan.net.torrent.client.senario;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Iterator;

import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.tracker.TrackerClient;
import net.hetimatan.net.torrent.tracker.TrackerPeerInfo;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;

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

	private WeakReference<TorrentClient> mUploadTargetPeer = null;

	public TorrentClientGetPeerListSenario(TorrentClient target) {
		mUploadTargetPeer = new WeakReference<TorrentClient>(target);
	}


	public OnResponseFromTrackerTask startTracker(TorrentClient client, String event) {
		OnResponseFromTrackerTask ret = new OnResponseFromTrackerTask(this);
		client.startTracker(event, ret);
		return ret;
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

	public void setNextRequestTask() { 
	 	TorrentClient peer = mUploadTargetPeer.get();
	 	if(peer == null) {return;}
	 	TrackerClient client = peer.getTracker();
	 	peer.setTrackerTask(client.getIntervalPerSec()*1000);
	}

	public static class OnResponseFromTrackerTask extends EventTask {
		
		public static final String TAG = "TorrentFrontFinTrackerTask";
		private WeakReference<TorrentClientGetPeerListSenario> mTorrentScenario = null;

		public OnResponseFromTrackerTask(TorrentClientGetPeerListSenario scenario) {
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
			scenario.setNextRequestTask();
		}

	}
}
