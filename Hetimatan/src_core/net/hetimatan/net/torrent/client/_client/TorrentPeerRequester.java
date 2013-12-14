package net.hetimatan.net.torrent.client._client;

import java.io.IOException;
import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentClientListener;
import net.hetimatan.net.torrent.client.TorrentData;
import net.hetimatan.net.torrent.client.TorrentClientFront;
import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.client.message.MessagePiece;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.net.torrent.tracker.TrackerClient;
import net.hetimatan.net.torrent.tracker.TrackerRequest;
import net.hetimatan.util.bitfield.BitField;

public class TorrentPeerRequester implements TorrentClientListener {
	private WeakReference<TorrentClient> mOwner = null;


	public TorrentPeerRequester(TorrentClient peer) {
		mOwner = new WeakReference<TorrentClient>(peer);
	}

	public int nextPieceId() {
		TorrentClient peer = mOwner.get();
		TorrentData data = peer.getTorrentData();
		BitField bitfield = data.getRequestedDataInfo();
		int nextId = bitfield.getOffPieceAtRandom();
		data.setRequest(nextId);
		return nextId;//mTodoCurrentRequestIndex++;
	}

	private void sendHave(int index) {
	 	TorrentClient peer = mOwner.get();
		if(peer == null) {return;}
		for(int i=0;i<peer.getTorrentPeerManager().numOfFront();i++) {
			TorrentClientFront front = peer.getTorrentPeerManager().getTorrentFront(i);
			if(front != null) {
				try {
					front.getTaskManager().startHave(peer, front, index);
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
	public void onReceiveMessage(TorrentClientFront front, TorrentMessage message) {
	 	TorrentClient peer = mOwner.get();
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
					front.getTaskManager().startDownload(front.getTorrentPeer(), front);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				tryToSendCompleted();
			}
			
		}
	}

	private void tryToSendCompleted() {
	 	TorrentClient peer = mOwner.get();
		if(peer == null) {return;}

		if(!TrackerRequest.EVENT_COMPLETED.equals(peer.getTracker().getCurrentEvent())){
			peer.startTracker(TrackerRequest.EVENT_COMPLETED);
		}		
	}

	@Override
	public void onResponsePeerList(TrackerClient client) {
	}
}
