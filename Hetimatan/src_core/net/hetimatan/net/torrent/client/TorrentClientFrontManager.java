package net.hetimatan.net.torrent.client;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Set;

import net.hetimatan.net.torrent.tracker.TrackerPeerInfo;

public class TorrentClientFrontManager {

	private LinkedHashMap<TrackerPeerInfo, NPeerInfo> mFrontList
		= new LinkedHashMap<TrackerPeerInfo, NPeerInfo>();

	public synchronized int numOfFront() {
		return mFrontList.size();
	}	

	public boolean addTorrentFront(TorrentClientFront front) throws IOException {
		return addTorrentFront(front.getPeer(), front);
	}

	public boolean contain(TrackerPeerInfo peer) {
		return mFrontList.containsKey(peer);
	}

	public boolean addPeerInfo(TrackerPeerInfo peer) {
		if(mFrontList.containsKey(peer)) {
			return false;
		} else {
			NPeerInfo info = new NPeerInfo();
			mFrontList.put(peer, info);			
			return true;
		}
	}

	public boolean addTorrentFront(TrackerPeerInfo peer, TorrentClientFront front) throws IOException {
		if(mFrontList.containsKey(peer)) {
			NPeerInfo info = mFrontList.get(peer);
			if(info.getFront() == front) {
				return false;
			} else {
				info.setFront(front);
				return true;
			}
		} else {
			NPeerInfo info = new NPeerInfo();
			info.setFront(front);
			mFrontList.put(peer, info);
			return true;
		}
	}

	public void removeTorrentFront(TorrentClientFront front) {
		TrackerPeerInfo info = front.getPeer();
		if(mFrontList.containsKey(info)) {
			NPeerInfo cont = mFrontList.get(info);
			cont.setFront(null);
		}
	}

	public void removePeerInfo(TrackerPeerInfo info) {
		mFrontList.remove(info);
	}

	public TorrentClientFront getTorrentFront(int i) {
		TrackerPeerInfo key = getFrontPeer(i);
		return getTorrentFront(key);
	}

	public TorrentClientFront getTorrentFront(TrackerPeerInfo peer) {
		if(peer == null) {return null;}
		NPeerInfo info = mFrontList.get(peer);
		if(info == null) {return null;}
		return info.getFront();
	}

	public TrackerPeerInfo getFrontPeer(int index) {
		Set<TrackerPeerInfo> keys = mFrontList.keySet();
		if(index<keys.size()) {
			return (TrackerPeerInfo)keys.toArray()[index];
		} else {
			return null;
		}
	}

	public static class NPeerInfo {
		private TorrentClientFront mFront = null;
		public TorrentClientFront getFront() {
			return mFront;
		}
		public void setFront(TorrentClientFront front) {
			mFront = front;
		}
	}

	public static class PeerInfo implements Comparable<PeerInfo>{
		private TrackerPeerInfo mPeerInfo = null;
		private int mStatus = 0;

		public PeerInfo(TrackerPeerInfo info) {
			mPeerInfo = info;
		}

		@Override
		public int compareTo(PeerInfo o) {
			return mPeerInfo.compareTo(o.mPeerInfo);
		}

		@Override
		public boolean equals(Object obj) {
			return mPeerInfo.equals(obj);
		}

		public int getStatus() {
			return mStatus;
		}

		public TrackerPeerInfo getPeerInfo() {
			return mPeerInfo;
		}
	}

}
