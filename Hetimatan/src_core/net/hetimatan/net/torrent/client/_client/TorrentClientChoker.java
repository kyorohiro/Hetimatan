package net.hetimatan.net.torrent.client._client;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.LinkedList;

import net.hetimatan.net.torrent.client.TorrentClientFront;
import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.client.TorrentClientListener;
import net.hetimatan.net.torrent.client.TorrentClientSetting;
import net.hetimatan.net.torrent.client.message.MessageHave;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.net.torrent.tracker.TrackerClient;
import net.hetimatan.net.torrent.tracker.TrackerPeerInfo;
import net.hetimatan.util.bitfield.BitField;

public class TorrentClientChoker implements TorrentClientListener {

	private WeakReference<TorrentClient> mOwner = null;
	private LinkedList<TrackerPeerInfo> mOptimusUnchokePeer    = new LinkedList<>();

	public TorrentClientChoker(TorrentClient owner) {
		mOwner = new WeakReference<TorrentClient>(owner);
	}

	public void __choke(TorrentClientFront front) throws IOException {
		TrackerPeerInfo peer = front.getPeer();
		if(!mOptimusUnchokePeer.contains(peer)) {
			mOptimusUnchokePeer.remove(peer);
		}
		front.getTaskManager().startChoker(front.getTorrentPeer(), front, true);
	}
	
	public void __unchoke(TorrentClientFront front) throws IOException {
		TrackerPeerInfo peer = front.getPeer();
		if(!mOptimusUnchokePeer.contains(peer)) {
			mOptimusUnchokePeer.add(peer);
			front.getTaskManager().startChoker(front.getTorrentPeer(), front, false);
		}		
	}

	@Override
	public void onConnection(TorrentClientFront front) throws IOException {

	}

	@Override
	public void onClose(TorrentClientFront front) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onShakeHand(TorrentClientFront front) throws IOException {
		TorrentClient torrentPeer = mOwner.get();if(torrentPeer == null) {return;}
		TorrentClientSetting mSetting = torrentPeer.getSetting();
		int numOfUnchokerNow = mOptimusUnchokePeer.size();
		int maxOfUnchoker = mSetting.getNumOfUnchoker();

		System.out.println("--AA-0-"+numOfUnchokerNow);
		if (numOfUnchokerNow<maxOfUnchoker) {
			__unchoke(front);			
		} else {
			__choke(front);
		}
		System.out.println("--AA-1-"+numOfUnchokerNow);	
	}

	@Override
	public void onReceiveMessage(TorrentClientFront front, TorrentMessage message) {
		 updateInterest(front, message);
	}

	@Override
	public void onResponsePeerList(TrackerClient client) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSendMessage(TorrentClientFront front, TorrentMessage message)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInterval(TorrentClient client) {
		// TODO Auto-generated method stub
		
	}

	
}
