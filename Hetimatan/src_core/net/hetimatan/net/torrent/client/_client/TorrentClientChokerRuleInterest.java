package net.hetimatan.net.torrent.client._client;

import java.io.IOException;

import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.client.TorrentClientFront;
import net.hetimatan.net.torrent.client.TorrentClientListener;
import net.hetimatan.net.torrent.client.message.MessageHave;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.net.torrent.tracker.TrackerClient;
import net.hetimatan.util.bitfield.BitField;

public class TorrentClientChokerRuleInterest implements TorrentClientListener {

	public void updateInterest(TorrentClientFront front, TorrentMessage message) {
		if(front == null) {return;}
	 	TorrentClient peer = front.getTorrentPeer();
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
	public void onConnection(TorrentClientFront front) throws IOException {
	}

	@Override
	public void onClose(TorrentClientFront front) throws IOException {
	}

	@Override
	public void onClose(TorrentClient client) throws IOException {
	}

	@Override
	public void onShakeHand(TorrentClientFront front) throws IOException {
	}

	@Override
	public void onSendMessage(TorrentClientFront front, TorrentMessage message) throws IOException {
	}

	@Override
	public void onReceiveMessage(TorrentClientFront front, TorrentMessage message) throws IOException {
		updateInterest(front, message);
	}

	@Override
	public void onResponsePeerList(TorrentClient client, TrackerClient tracker)  throws IOException {
	}

	@Override
	public void onInterval(TorrentClient client) {
	}

}
