package net.hetimatan.net.torrent.client._client;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.Random;

import net.hetimatan.net.torrent.client.TorrentFront;
import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.client.TorrentClientSetting;
import net.hetimatan.net.torrent.tracker.TrackerPeerInfo;

public class TorrentPeerChoker {

	private WeakReference<TorrentClient> mOwner = null;
	private LinkedList<TrackerPeerInfo> mOptimusUnchokePeer    = new LinkedList<>();

	public TorrentPeerChoker(TorrentClient owner) {
		mOwner = new WeakReference<TorrentClient>(owner);
	}

	public void updateOptimusUnchokePeer() throws IOException {
		
		TorrentClient torrentPeer = mOwner.get();if(torrentPeer == null) {return;}
		TorrentClientSetting mSetting = torrentPeer.getSetting();
		int numOfUnchokerNow = mOptimusUnchokePeer.size();
		int maxOfUnchoker = mSetting.getNumOfUnchoker();
		int numOfFront = torrentPeer.getTorrentPeerManager().numOfFront();
		Random r = new Random();
		if (numOfUnchokerNow>maxOfUnchoker) {
			int add = r.nextInt(numOfFront);
			int rm = r.nextInt(numOfUnchokerNow);
			TrackerPeerInfo peer1 = torrentPeer.getTorrentPeerManager().getFrontPeer(add);
			TrackerPeerInfo peer2 = mOptimusUnchokePeer.get(rm);
			if(!peer1.equals(peer2)) {
				mOptimusUnchokePeer.remove(rm);
				TorrentFront front = torrentPeer.getTorrentPeerManager().getTorrentFront(peer2);
				__choke(front);
				mOptimusUnchokePeer.add(peer1);				
				front = torrentPeer.getTorrentPeerManager().getTorrentFront(peer1);
				__unchoke(front);
			}
		}
	}

	public void onStartTorrentFront(TorrentFront front) throws IOException {
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

	public void __choke(TorrentFront front) throws IOException {
		TrackerPeerInfo peer = front.getPeer();
		if(!mOptimusUnchokePeer.contains(peer)) {
			mOptimusUnchokePeer.remove(peer);
		}
		front.getTaskManager().startChoker(front.getTorrentPeer(), front, true);
	}
	
	public void __unchoke(TorrentFront front) throws IOException {
		TrackerPeerInfo peer = front.getPeer();
		if(!mOptimusUnchokePeer.contains(peer)) {
			mOptimusUnchokePeer.add(peer);
			front.getTaskManager().startChoker(front.getTorrentPeer(), front, false);
		}		
	}
}
