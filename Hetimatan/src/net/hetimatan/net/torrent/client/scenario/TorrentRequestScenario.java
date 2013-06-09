package net.hetimatan.net.torrent.client.scenario;


import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;

import net.hetimatan.net.torrent.client.TorrentData;
import net.hetimatan.net.torrent.client.TorrentFront;
import net.hetimatan.net.torrent.client.TorrentPeer;
import net.hetimatan.net.torrent.client.TorrentFront.EventListener;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.net.torrent.client.scenario.task.ScenarioSendPieceTask;
import net.hetimatan.net.torrent.tracker.TrackerClient;
import net.hetimatan.net.torrent.tracker.TrackerClient.Peer;

//
// uploaad
// 
public class TorrentRequestScenario implements TorrentFront.EventListener {

	private WeakReference<TorrentPeer> mUploadTargetPeer = null;

	public TorrentRequestScenario(TorrentPeer peer) {
		mUploadTargetPeer = new WeakReference<TorrentPeer>(peer);
	}

	/*
	 * except myself's peer send message to me.
	 */
	@Override
	public void onReceiveMessage(TorrentFront front, TorrentMessage message) {
	 	TorrentPeer peer = mUploadTargetPeer.get();
		if(peer == null) {return;}
		if(
		message.getType()==TorrentMessage.SIGN_PIECE||
		message.getType()==TorrentMessage.SIGN_UNCHOKE) {
			if(!peer.isSeeder()) {
				try {
					front.startDownload();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
