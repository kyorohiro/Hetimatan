package net.hetimatan.net.torrent.client._peer;


import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;

import net.hetimatan.net.torrent.client.TorrentFront;
import net.hetimatan.net.torrent.client.TorrentPeer;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.net.torrent.client.scenario.task.ScenarioSeeder;
import net.hetimatan.net.torrent.client.scenario.task.ScenarioSendPieceTask;
import net.hetimatan.net.torrent.tracker.TrackerClient;
import net.hetimatan.net.torrent.tracker.TrackerPeerInfo;

//
// uploaad
// 
public class TorrentPeerPiecer implements TorrentFront.EventListener {

	private WeakReference<TorrentPeer> mUploadTargetPeer = null;
	private LinkedList<ScenarioSendPieceTask> mScenarioList = new LinkedList<ScenarioSendPieceTask>();
	private ScenarioSeeder mSeederTask = null;

	public TorrentPeerPiecer(TorrentPeer peer) {
		mUploadTargetPeer = new WeakReference<TorrentPeer>(peer);
		mSeederTask = new ScenarioSeeder(this, peer.getClientRunner());
	}

	public void sendPiece(TorrentFront front) {
		TorrentPeer peer = mUploadTargetPeer.get();
		if(peer == null){return;}
		ScenarioSendPieceTask task = new ScenarioSendPieceTask(this, front, peer.getClientRunner());
		peer.getClientRunner().pushWork(task);
	}

	//
	// upload data as Seeder
	// recreate . some torrent client receive only requested piece.
	public void distributeInOrder() throws IOException {
		TorrentPeer peer = mUploadTargetPeer.get();
		
		if(peer == null) {
			return;
		}
		int existLen = mScenarioList.size();
		int newLen =  4-existLen;
		if(newLen<0) {return;}
		boolean have = false;
		for(int i=0;i<peer.numOfFront();i++) {
			TorrentFront f = peer.getTorrentFront(i);
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
	public void onReceiveMessage(TorrentFront front, TorrentMessage message) {
	 	TorrentPeer peer = mUploadTargetPeer.get();
		if(peer == null) {return;}
		if(peer.getClientRunner().contains(mSeederTask)){
			return;
		} else {
			peer.addClientTask(mSeederTask);
		}
	}

	/**
	 * getted peers from tracker. 
	 */
	public void onFinTracker() throws IOException {
	 	TorrentPeer peer = mUploadTargetPeer.get();
	 	if(peer.isSeeder()) {
	 		return ;
	 	}
	 	if(peer == null) {return;}
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
}
