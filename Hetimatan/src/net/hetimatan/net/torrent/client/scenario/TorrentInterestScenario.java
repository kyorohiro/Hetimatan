package net.hetimatan.net.torrent.client.scenario;


import java.lang.ref.WeakReference;
import java.util.LinkedList;

import net.hetimatan.net.torrent.client.TorrentFront;
import net.hetimatan.net.torrent.client.TorrentPeer;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.net.torrent.client.scenario.task.ScenarioSendPieceTask;
import net.hetimatan.util.bitfield.BitField;

//
// if target peer has my lacked piece. notify interest. else notify not interst
// 
public class TorrentInterestScenario implements TorrentFront.EventListener {

	private WeakReference<TorrentPeer> mUploadTargetPeer = null;
	private LinkedList<ScenarioSendPieceTask> mScenarioList = new LinkedList<ScenarioSendPieceTask>();

	public TorrentInterestScenario(TorrentPeer peer) {
		mUploadTargetPeer = new WeakReference<TorrentPeer>(peer);
	}


	/*
	 * except myself's peer send message to me.
	 */
	@Override
	public void onReceiveMessage(TorrentFront front, TorrentMessage message) {
	 	TorrentPeer peer = mUploadTargetPeer.get();
		if(peer == null) {return;}
		if(message.getType() == TorrentMessage.SIGN_BITFIELD) {
			BitField relative = front.relativeBitfield();
			if(!relative.isAllOff()) {
				front.startInterest();
			} else {
				front.startNotInterest();				
			}
		}
	}

}
