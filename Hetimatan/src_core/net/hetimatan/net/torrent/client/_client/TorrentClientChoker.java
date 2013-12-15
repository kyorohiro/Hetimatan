package net.hetimatan.net.torrent.client._client;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.LinkedList;

import net.hetimatan.net.torrent.client.TorrentClientFront;
import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.client.TorrentClientListener;
import net.hetimatan.net.torrent.client.TorrentClientSetting;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.net.torrent.tracker.TrackerClient;
import net.hetimatan.net.torrent.tracker.TrackerPeerInfo;

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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResponsePeerList(TrackerClient client) {
		// TODO Auto-generated method stub
		
	}
}
