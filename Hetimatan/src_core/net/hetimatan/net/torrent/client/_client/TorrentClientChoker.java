package net.hetimatan.net.torrent.client._client;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.Random;

import net.hetimatan.net.torrent.client.TorrentClientFront;
import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.client.TorrentClientListener;
import net.hetimatan.net.torrent.client.TorrentClientSetting;
import net.hetimatan.net.torrent.client._front.TorrentClientFrontTargetInfo;
import net.hetimatan.net.torrent.client._front.TorrentFrontMyInfo;
import net.hetimatan.net.torrent.client.message.MessageHave;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.net.torrent.tracker.TrackerClient;
import net.hetimatan.net.torrent.tracker.TrackerPeerInfo;
import net.hetimatan.util.bitfield.BitField;

public class TorrentClientChoker implements TorrentClientListener {

	private WeakReference<TorrentClient> mOwner = null;
	private TorrentClientListener mChoke = new TorrentClientChokerRule();

	public TorrentClientChoker(TorrentClient owner) {
		mOwner = new WeakReference<TorrentClient>(owner);
	}

	public void __choke(TorrentClientFront front) throws IOException {
		TrackerPeerInfo peer = front.getPeer();
		front.getTaskManager().startChoker(front.getTorrentPeer(), front, true);
	}
	
	public void __unchoke(TorrentClientFront front) throws IOException {
		TrackerPeerInfo peer = front.getPeer();
		front.getTaskManager().startChoker(front.getTorrentPeer(), front, false);
	}

	@Override
	public void onConnection(TorrentClientFront front) throws IOException {
		mChoke.onConnection(front);
	}

	@Override
	public void onClose(TorrentClientFront front) throws IOException {
		mChoke.onClose(front);
	}

	@Override
	public void onShakeHand(TorrentClientFront front) throws IOException {
		mChoke.onShakeHand(front);
	}

	@Override
	public void onReceiveMessage(TorrentClientFront front, TorrentMessage message) throws IOException {
		mChoke.onReceiveMessage(front, message);
		updateInterest(front, message);
	}

	@Override
	public void onResponsePeerList(TrackerClient client) throws IOException {
		mChoke.onResponsePeerList(client);
	}

	public void updateInterest(TorrentClientFront front, TorrentMessage message) {
	 	TorrentClient peer = mOwner.get();
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
	public void onClose(TorrentClient client) throws IOException {
		mChoke.onClose(client);
	}

	@Override
	public void onSendMessage(TorrentClientFront front, TorrentMessage message)throws IOException {
		mChoke.onSendMessage(front, message);
	}

	@Override
	public void onInterval(TorrentClient client) {
		mChoke.onInterval(client);
	}

	
}
