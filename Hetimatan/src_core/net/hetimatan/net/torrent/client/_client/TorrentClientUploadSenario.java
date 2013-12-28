package net.hetimatan.net.torrent.client._client;


import java.io.IOException;
import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentClientFront;
import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.client.TorrentClientListener;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.net.torrent.tracker.TrackerClient;
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
public class TorrentClientUploadSenario implements TorrentClientListener {

	private WeakReference<TorrentClient> mUploadTargetPeer = null;
	private UploaderTask mUploaderTask = null;
		
	public TorrentClientUploadSenario(TorrentClient peer) {
		mUploadTargetPeer = new WeakReference<TorrentClient>(peer);
		mUploaderTask = new UploaderTask(this);
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
		if(peer == null) {return;}

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
			peer.addClientTask(mUploaderTask);
		}
	}

	/**
	 * except myself's peer send message to me.
	 */
	@Override
	public void onReceiveMessage(TorrentClientFront front, TorrentMessage message) {
	 	TorrentClient peer = mUploadTargetPeer.get();
		if(peer == null) {return;}
		if(peer.getClientRunner().contains(mUploaderTask)){return;}
		peer.addClientTask(mUploaderTask);
	}

	@Override
	public void onResponsePeerList(TorrentClient client, TrackerClient tracker)  {
	}

	//
	//
	public static class UploaderTask extends EventTask {
		
		public static final String TAG = "ScenarioSeeder";
		private WeakReference<TorrentClientUploadSenario> mTorrentScenario = null;

		public UploaderTask(TorrentClientUploadSenario scenario) {
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

	public class TorrentFrontSendPieceTask extends EventTask {
		
		public static final String TAG = "TorrentFrontSendPieceTask";
		private WeakReference<TorrentClientFront> mTorrentFront = null;

		public TorrentFrontSendPieceTask(TorrentClientFront front) {
			mTorrentFront = new WeakReference<TorrentClientFront>(front);
		}

		@Override
		public String toString() {
			return TAG;
		}

		@Override
		public void action(EventTaskRunner runner) throws Throwable {
			TorrentClientFront front = mTorrentFront.get();
			if(front == null) {return;}	
			front.sendPiece();
		}
	}

	@Override
	public void onClose(TorrentClientFront front) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onShakeHand(TorrentClientFront front) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnection(TorrentClientFront front) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClose(TorrentClient client) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSendMessage(TorrentClientFront front, TorrentMessage message)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInterval(TorrentClient client) {
		// TODO Auto-generated method stub
		
	}
}
