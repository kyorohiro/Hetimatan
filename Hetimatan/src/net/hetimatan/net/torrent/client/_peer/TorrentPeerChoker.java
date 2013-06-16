package net.hetimatan.net.torrent.client._peer;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.Random;

import net.hetimatan.net.torrent.client.TorrentFront;
import net.hetimatan.net.torrent.client.TorrentPeer;
import net.hetimatan.net.torrent.tracker.TrackerPeerInfo;

public class TorrentPeerChoker {

	private WeakReference<TorrentPeer> mOwner = null;
	private LinkedList<TrackerPeerInfo> mOptimusUnchokePeer    = new LinkedList<>();

	public TorrentPeerChoker(TorrentPeer owner) {
		mOwner = new WeakReference<TorrentPeer>(owner);
	}

	public void appendFront() {
		
	}

	public void updateOptimusUnchokePeer() throws IOException {
		TorrentPeer torrentPeer = mOwner.get();if(torrentPeer == null) {return;}
		TorrentPeerSetting mSetting = torrentPeer.getSetting();
		int numOfUnchokerNow = mOptimusUnchokePeer.size();
		int maxOfUnchoker = mSetting.getNumOfUnchoker();
		int numOfFront = torrentPeer.numOfFront();
		Random r = new Random();
		if (numOfUnchokerNow>maxOfUnchoker) {
			int add = r.nextInt(numOfFront);
			int rm = r.nextInt(numOfUnchokerNow);
			TrackerPeerInfo peer1 = torrentPeer.getFrontPeer(add);
			TrackerPeerInfo peer2 = mOptimusUnchokePeer.get(rm);
			if(!peer1.equals(peer2)) {
				mOptimusUnchokePeer.remove(rm);
				TorrentFront front = torrentPeer.getTorrentFront(peer2);
				__choke(front);
				mOptimusUnchokePeer.add(peer1);				
				front = torrentPeer.getTorrentFront(peer1);
				__unchoke(front);
			}
		}
	}

	public void onStartTorrentFront(TorrentFront front) throws IOException {
		TorrentPeer torrentPeer = mOwner.get();if(torrentPeer == null) {return;}
		TorrentPeerSetting mSetting = torrentPeer.getSetting();
		int numOfUnchokerNow = mOptimusUnchokePeer.size();
		int maxOfUnchoker = mSetting.getNumOfUnchoker();
		int numOfFront = torrentPeer.numOfFront();

		if (numOfUnchokerNow<maxOfUnchoker) {
			__unchoke(front);			
		} else {
			__choke(front);
		}
	}

	public void __choke(TorrentFront front) throws IOException {
		TrackerPeerInfo peer = front.getPeer();
		if(!mOptimusUnchokePeer.contains(peer)) {
			mOptimusUnchokePeer.remove(peer);
		}
		// unchoke通知は制限しない
		//	if(front.getMyInfo().mChoked) {
			front.startChoker(true);
		//	}
	}
	
	public void __unchoke(TorrentFront front) throws IOException {
		TrackerPeerInfo peer = front.getPeer();
		if(!mOptimusUnchokePeer.contains(peer)) {
			mOptimusUnchokePeer.add(peer);
			//if(front.getMyInfo().mChoked) {
				front.startChoker(false);
			//}
		}		
	}
	
}
