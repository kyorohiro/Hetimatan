package net.hetimatan.net.torrent.client._peer;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Set;

import net.hetimatan.net.torrent.client.TorrentFront;
import net.hetimatan.net.torrent.tracker.TrackerPeerInfo;

public class TorrentPeerFrontManager {

	private LinkedHashMap<TrackerPeerInfo, TorrentFront> mFrontList = new LinkedHashMap<TrackerPeerInfo, TorrentFront>();

	public synchronized int numOfFront() {
		return mFrontList.size();
	}	

	public boolean addTorrentFront(TorrentFront front) throws IOException {
		String host = front.getSocket().getHost();
		int port = front.getSocket().getPort();
		TrackerPeerInfo peer = new TrackerPeerInfo(host, port);
		return addTorrentFront(peer, front);
	}

	public boolean contain(TrackerPeerInfo peer) {
		return mFrontList.containsKey(peer);
	}

	public boolean addTorrentFront(TrackerPeerInfo peer, TorrentFront front) throws IOException {
		if(mFrontList.containsKey(peer)) {
			return false;
		} else {
			mFrontList.put(peer, front);
			return true;
		}
	}

	public void removeTorrentFront(TorrentFront front) {
		mFrontList.remove(front.getPeer());
	}

	public TorrentFront getTorrentFront(int i) {
		TrackerPeerInfo key = getFrontPeer(i);
		return getTorrentFront(key);
	}
	public TorrentFront getTorrentFront(TrackerPeerInfo peer) {
		if(peer == null) {return null;}
		return mFrontList.get(peer);
	}

	public TrackerPeerInfo getFrontPeer(int index) {
		Set<TrackerPeerInfo> keys = mFrontList.keySet();
		if(index<keys.size()) {
			return (TrackerPeerInfo)keys.toArray()[index];
		} else {
			return null;
		}
	}

}
