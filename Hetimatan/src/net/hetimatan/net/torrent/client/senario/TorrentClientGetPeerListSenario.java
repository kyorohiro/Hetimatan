package net.hetimatan.net.torrent.client.senario;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Iterator;

import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.tracker.TrackerClient;
import net.hetimatan.net.torrent.tracker.TrackerPeerInfo;

public class TorrentClientGetPeerListSenario {

	private WeakReference<TorrentClient> mUploadTargetPeer = null;

	/**
	 * getted peers from tracker. 
	 */
	public void onFinTracker() throws IOException {
	 	TorrentClient peer = mUploadTargetPeer.get();
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
	 	peer.setTrackerTask(client.getIntervalPerSec()*1000);
	}
}
