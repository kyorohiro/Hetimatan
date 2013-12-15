package net.hetimatan.net.torrent.client._client;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Set;

import net.hetimatan.net.torrent.client.TorrentClientFront;
import net.hetimatan.net.torrent.tracker.TrackerPeerInfo;

public class TorrentClientFrontManager {

	private LinkedHashMap<TrackerPeerInfo, TorrentClientFront> mFrontList = new LinkedHashMap<TrackerPeerInfo, TorrentClientFront>();

	public synchronized int numOfFront() {
		return mFrontList.size();
	}	

	public boolean addTorrentFront(TorrentClientFront front) throws IOException {
		String host = front.getSocket().getHost();
		int port = front.getSocket().getPort();
		TrackerPeerInfo peer = new TrackerPeerInfo(host, port);
		return addTorrentFront(peer, front);
	}

	public boolean contain(TrackerPeerInfo peer) {
		return mFrontList.containsKey(peer);
	}

	public boolean addTorrentFront(TrackerPeerInfo peer, TorrentClientFront front) throws IOException {
		if(mFrontList.containsKey(peer)) {
			return false;
		} else {
			mFrontList.put(peer, front);
			return true;
		}
	}

	public void removeTorrentFront(TorrentClientFront front) {
		mFrontList.remove(front.getPeer());
	}

	public TorrentClientFront getTorrentFront(int i) {
		TrackerPeerInfo key = getFrontPeer(i);
		return getTorrentFront(key);
	}

	public TorrentClientFront getTorrentFront(TrackerPeerInfo peer) {
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
