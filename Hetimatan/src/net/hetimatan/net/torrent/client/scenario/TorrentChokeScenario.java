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
import net.hetimatan.net.torrent.client.scenario.task.ScenarioSeeder;
import net.hetimatan.net.torrent.client.scenario.task.ScenarioSendPieceTask;
import net.hetimatan.net.torrent.tracker.TrackerClient;
import net.hetimatan.net.torrent.tracker.TrackerClient.Peer;

//
// uploaad
// 
public class TorrentChokeScenario implements TorrentFront.EventListener {

	private WeakReference<TorrentPeer> mUploadTargetPeer = null;
	private LinkedList<ScenarioSendPieceTask> mScenarioList = new LinkedList<ScenarioSendPieceTask>();

	public TorrentChokeScenario(TorrentPeer peer) {
		mUploadTargetPeer = new WeakReference<TorrentPeer>(peer);
	}



	/*
	 * except myself's peer send message to me.
	 */
	@Override
	public void onReceiveMessage(TorrentFront front, TorrentMessage message) {
	 	TorrentPeer peer = mUploadTargetPeer.get();
		if(peer == null) {return;}
	}

}
