package net.hetimatan.net.torrent.client._senario;


import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.LinkedList;

import net.hetimatan.net.torrent.client.TorrentClientFront;
import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.client._front.TorrentFrontReceiveMessageSenario.EventListener;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.net.torrent.client.task.TorrentFrontSendPieceTask;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;

//
//もともとぬ、TorrentFront、TorrentClientにあった機能を
//機能ごとに別のクラスに委譲したい。
//
//このクラスもその候補
//まだ、メソッドだけ抜き出した状態
//
//
// upload data senario
// 
// 1. receive message 
// 2. start updalod(ScenarioSeeder)
// 3. if have uploaded data, upload
//
public class TorrentClientUploadSenario implements EventListener {

	private WeakReference<TorrentClient> mUploadTargetPeer = null;
	private LinkedList<TorrentFrontSendPieceTask> mScenarioList = new LinkedList<TorrentFrontSendPieceTask>();
	private ScenarioSeeder mSeederTask = null;

	public TorrentClientUploadSenario(TorrentClient peer) {
		mUploadTargetPeer = new WeakReference<TorrentClient>(peer);
		mSeederTask = new ScenarioSeeder(this);
	}

	public void sendPiece(TorrentClientFront front) {
		TorrentClient peer = mUploadTargetPeer.get();
		if(peer == null){return;}
		TorrentFrontSendPieceTask task = new TorrentFrontSendPieceTask(front);
		peer.getClientRunner().pushTask(task);
	}

	//
	// upload data as Seeder
	// recreate . some torrent client receive only requested piece.
	public void distributeInOrder() throws IOException {
		TorrentClient peer = mUploadTargetPeer.get();
		
		if(peer == null) {
			return;
		}
		int existLen = mScenarioList.size();
		int newLen =  4-existLen;
		if(newLen<0) {return;}
		boolean have = false;
		for(int i=0;i<peer.getTorrentPeerManager().numOfFront();i++) {
			TorrentClientFront f = peer.getTorrentPeerManager().getTorrentFront(i);
			if(f == null){continue;}
			if(f != null&&f.haveTargetRequest()) {
				sendPiece(f);
				have = true;
			}
		}
		if(have) {
			peer.addClientTask(mSeederTask);
		}
	}

	/**
	 * except myself's peer send message to me.
	 */
	@Override
	public void onReceiveMessage(TorrentClientFront front, TorrentMessage message) {
	 	TorrentClient peer = mUploadTargetPeer.get();
		if(peer == null) {return;}
		if(peer.getClientRunner().contains(mSeederTask)){
			return;
		} else {
			peer.addClientTask(mSeederTask);
		}
	}


	public static class ScenarioSeeder extends EventTask {
		
		public static final String TAG = "ScenarioSeeder";
		private WeakReference<TorrentClientUploadSenario> mTorrentScenario = null;

		public ScenarioSeeder(TorrentClientUploadSenario scenario) {
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
			scenario.distributeInOrder();
		}

	}

}