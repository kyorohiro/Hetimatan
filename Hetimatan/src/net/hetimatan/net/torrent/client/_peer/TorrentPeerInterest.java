package net.hetimatan.net.torrent.client._peer;


import java.lang.ref.WeakReference;
import java.util.LinkedList;

import net.hetimatan.net.torrent.client.TorrentFront;
import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.client.message.MessageHave;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.net.torrent.client.task.TorrentFrontSendPieceTask;
import net.hetimatan.util.bitfield.BitField;

//
// if target peer has my lacked piece. notify interest. else notify not interst
// 
public class TorrentPeerInterest implements TorrentFront.EventListener {

	private WeakReference<TorrentClient> mUploadTargetPeer = null;
	private LinkedList<TorrentFrontSendPieceTask> mScenarioList = new LinkedList<TorrentFrontSendPieceTask>();

	public TorrentPeerInterest(TorrentClient peer) {
		mUploadTargetPeer = new WeakReference<TorrentClient>(peer);
	}

	/*
	 * except myself's peer send message to me.
	 */
	@Override
	public void onReceiveMessage(TorrentFront front, TorrentMessage message) {
	 	TorrentClient peer = mUploadTargetPeer.get();
		if(peer == null) {return;}
		if(message.getType() == TorrentMessage.SIGN_BITFIELD) {
			BitField relative = front.relativeBitfield();
			if(!relative.isAllOff()) {
				front.startInterest();
			} else {
				front.startNotInterest();				
			}
		}
		if(message.getType() == TorrentMessage.SIGN_HAVE) {
			int index = ((MessageHave)message).getIndex();
			BitField stocked = peer.getTorrentData().getStockedDataInfo();
			if(!stocked.isOn(index)) {
				front.startInterest();
			}
		}
	}

}
