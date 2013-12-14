package net.hetimatan.net.torrent.client._client;


import java.lang.ref.WeakReference;
import java.util.LinkedList;

import net.hetimatan.net.torrent.client.TorrentClientFront;
import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.client.TorrentClientListener;
import net.hetimatan.net.torrent.client.message.MessageHave;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.net.torrent.tracker.TrackerClient;
import net.hetimatan.util.bitfield.BitField;

//
// if target peer has my lacked piece. notify interest. else notify not interst
// 
public class TorrentPeerInterest implements TorrentClientListener {

	private WeakReference<TorrentClient> mUploadTargetPeer = null;

	public TorrentPeerInterest(TorrentClient peer) {
		mUploadTargetPeer = new WeakReference<TorrentClient>(peer);
	}

	/*
	 * except myself's peer send message to me.
	 */
	@Override
	public void onReceiveMessage(TorrentClientFront front, TorrentMessage message) {
	 	TorrentClient peer = mUploadTargetPeer.get();
		if(peer == null) {return;}
		if(message.getType() == TorrentMessage.SIGN_BITFIELD) {
			BitField relative = front.relativeBitfield();
			if(!relative.isAllOff()) {
				front.getTaskManager().startInterest(front.getTorrentPeer(), front);
			} else {
				front.getTaskManager().startNotInterest(front.getTorrentPeer(), front);				
			}
		}
		if(message.getType() == TorrentMessage.SIGN_HAVE) {
			int index = ((MessageHave)message).getIndex();
			BitField stocked = peer.getTorrentData().getStockedDataInfo();
			if(!stocked.isOn(index)) {
				front.getTaskManager().startInterest(front.getTorrentPeer(), front);
			}
		}
	}

	@Override
	public void onResponsePeerList(TrackerClient client) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onClose(TorrentClientFront front) {
		// TODO Auto-generated method stub
		
	}

}
