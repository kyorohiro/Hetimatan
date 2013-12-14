package net.hetimatan.net.torrent.client._client;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Iterator;

import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.client.TorrentClientFront;
import net.hetimatan.net.torrent.client.TorrentClientListener;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.net.torrent.tracker.TrackerClient;
import net.hetimatan.net.torrent.tracker.TrackerPeerInfo;

/**
 * if finded new peer, connect this.
 *
 */
public class TorrentClientStartConnection implements TorrentClientListener {

	private WeakReference<TorrentClient> mUploadTargetPeer = null;

	public TorrentClientStartConnection(TorrentClient target) throws IOException {
		mUploadTargetPeer = new WeakReference<TorrentClient>(target);
	}

	public void startConnection() throws IOException {
	 	TorrentClient peer = mUploadTargetPeer.get();
	 	if(peer == null) {return;}
	 	if(peer.isSeeder()) {
	 		return ;
	 	}
	 	int todo_i =0;
	 	TrackerClient client = peer.getTracker();
	 	Iterator<TrackerPeerInfo> peers32 = client.getPeer32();
	 	if(!peer.isSeeder()) {//todo
	 		while(peers32.hasNext()) {
	 			todo_i++;
	 			TrackerPeerInfo targetPeer = peers32.next();
	 			peer.startConnect(targetPeer);
	 			if(todo_i>4) {
	 				break;
	 			}
	 		}
	 	}
	 	client.clearPeer32();
	}

	@Override
	public void onReceiveMessage(TorrentClientFront front, TorrentMessage message) {
	}

	@Override
	public void onResponsePeerList(TrackerClient client) {
		try {
			startConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClose(TorrentClientFront front) {
		try {
			startConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
