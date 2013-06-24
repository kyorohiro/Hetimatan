package net.hetimatan.net.torrent.client._peer;

import java.io.IOException;
import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentData;
import net.hetimatan.net.torrent.client.TorrentFront;
import net.hetimatan.net.torrent.client.TorrentPeer;
import net.hetimatan.net.torrent.client.message.MessageHave;
import net.hetimatan.net.torrent.client.message.MessagePiece;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.net.torrent.tracker.TrackerRequest;
import net.hetimatan.util.bitfield.BitField;

public class TorrentPeerRequester implements TorrentFront.EventListener {
	private WeakReference<TorrentPeer> mOwner = null;

	public TorrentPeerRequester(TorrentPeer peer) {
		mOwner = new WeakReference<TorrentPeer>(peer);
	}

	public int nextPieceId() {
		TorrentPeer peer = mOwner.get();
		TorrentData data = peer.getTorrentData();
		BitField bitfield = data.getRequestedDataInfo();
		int nextId = bitfield.getPieceAtRandom();
		data.setRequest(nextId);
		return nextId;//mTodoCurrentRequestIndex++;
	}

	private void sendHave(int index) {
	 	TorrentPeer peer = mOwner.get();
		if(peer == null) {return;}
		for(int i=0;i<peer.numOfFront();i++) {
			TorrentFront front = peer.getTorrentFront(i);
			if(front != null) {
				try {
					front.startHave(index);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private int requestPiece = -1;
	/*
	 * except myself's peer send message to me.
	 */
	@Override
	public void onReceiveMessage(TorrentFront front, TorrentMessage message) {
	 	TorrentPeer peer = mOwner.get();
		if(peer == null) {return;}

		if(message.getType()==TorrentMessage.SIGN_PIECE) {
			sendHave(((MessagePiece)message).getIndex());
		}

		if(
		message.getType()==TorrentMessage.SIGN_PIECE||
		message.getType()==TorrentMessage.SIGN_UNCHOKE||
		message.getType()==TorrentMessage.SIGN_HAVE
		) {
			if(!peer.isSeeder()) {
				try {
						front.startDownload();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				if(!TrackerRequest.EVENT_COMPLETED.equals(peer.getTracker().getCurrentEvent())){
					peer.startTracker(TrackerRequest.EVENT_COMPLETED);
				}
			}
			
		}
	}
}
