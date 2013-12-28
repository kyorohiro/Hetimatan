package net.hetimatan.net.torrent.client._client;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;

import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.client.TorrentClientFront;
import net.hetimatan.net.torrent.client.TorrentClientFrontManager;
import net.hetimatan.net.torrent.client.TorrentClientFrontManager.PeerInfo;
import net.hetimatan.net.torrent.client.TorrentClientListener;
import net.hetimatan.net.torrent.client.TorrentHistory;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.net.torrent.tracker.TrackerClient;
import net.hetimatan.net.torrent.tracker.TrackerPeerInfo;

/**
 * if finded new peer, connect this.
 *
 */
public class TorrentClientChokerConnection implements TorrentClientListener {


	public TorrentClientChokerConnection() {
	}

	public void updatePeerList_tracker2client(TorrentClient peer) throws IOException {
	 	if(peer == null) {return;}
	 	if(peer.isSeeder()) {
	 		return ;
	 	}
	 	TorrentClientFrontManager manager = peer.getTorrentPeerManager();
	 	TrackerClient client = peer.getTracker();
	 	Iterator<TrackerPeerInfo> peers32 = client.getPeer32();
	 	if(!peer.isSeeder()) {//todo
	 		while(peers32.hasNext()) {
	 			TrackerPeerInfo targetPeer = peers32.next();
	 			peer.startConnect(targetPeer);
	 			if(!manager.contain(targetPeer)) {
	 				manager.addPeerInfo(targetPeer);
	 			}
	 		}
	 	}
	 	client.clearPeer32();
	}

	public void startConnection(TorrentClient peer) throws IOException {
		TorrentHistory.get().pushMessage("startConnection(\r\n");
	 	if(peer == null) {return;}
	 	TorrentClientFrontManager manager = peer.getTorrentPeerManager();
	 	
	 	//todo
		for(int i=0;i<manager.numOfFront();i++) {
			TrackerPeerInfo info = manager.getFrontPeer(i);
			TorrentClientFront front = manager.getTorrentFront(info);
			if(front == null) {
				peer.startConnect(info);
			}
		}
	}

	@Override
	public void onReceiveMessage(TorrentClientFront front, TorrentMessage message) {
	}

	@Override
	public void onResponsePeerList(TorrentClient client, TrackerClient tracker) {
		try {
			updatePeerList_tracker2client(client);
			startConnection(client);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClose(TorrentClientFront front) {
	 	TorrentClient peer = front.getTorrentPeer();
	 	TorrentClientFrontManager manager = peer.getTorrentPeerManager();
		try {
			if(front.isOneself()||!front.isConnectable()) {
				manager.removePeerInfo(front.getPeer());
			} else if(!peer.isSeeder()){
				startConnection(peer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onShakeHand(TorrentClientFront front) {
	}

	@Override
	public void onConnection(TorrentClientFront front) {
	}

	@Override
	public void onClose(TorrentClient client) throws IOException {
	}

	@Override
	public void onSendMessage(TorrentClientFront front, TorrentMessage message) throws IOException {
	}

	@Override
	public void onInterval(TorrentClient client) {
	}
}
