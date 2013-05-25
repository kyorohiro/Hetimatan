package net.hetimatan.net.torrent.tracker.db;


import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import net.hetimatan.net.torrent.tracker.TrackerRequest;
import net.hetimatan.util.http.HttpObject;
import net.hetimatan.util.http.HttpRequestURI;

@Deprecated
public class TrackerData {

	private byte[] mInfoHash = null;

	// ↓ is need test or rewrite
	private LinkedHashMap<String, TrackerPeerInfo> mInfos = new LinkedHashMap<String, TrackerPeerInfo>();
	private LinkedList<TrackerPeerInfo> mShuffledInfoCashForResponse = new LinkedList<TrackerPeerInfo>();

	private boolean isUpdated = false;
	private int mComplete = 0;
	private int mIncomplete = 0;
	private void update() {
		int num = 0;
		for(TrackerPeerInfo info:mShuffledInfoCashForResponse) {
			if(info.isComplete()) {
				num++;
			}
		}
		mComplete = num;
		mIncomplete = mShuffledInfoCashForResponse.size()-mComplete;
	}

	public int getComplete() {
		if(isUpdated) {
			update();
		}
		return mComplete;
	}

	public int getIncomplete() {
		if(isUpdated) {
			update();
		}
		return mIncomplete;
	}

	public TrackerData(byte[] infoHash) {
		mInfoHash = infoHash;
	}

	public byte[] getManagedInfoHash() {
		return mInfoHash;
	}

	public void removePeerInfo(String trackerId) {
		removePeerInfo(mInfos.get(trackerId));
	}

	public void removePeerInfo(TrackerPeerInfo peerInfo) {
		mInfos.remove(peerInfo);
		mShuffledInfoCashForResponse.remove(peerInfo);
	}

	public boolean cotains(String peerId) {
		return mInfos.containsKey(peerId);
	}

	public void putPeerInfo(TrackerPeerInfo peerInfo) {
		if (cotains(peerInfo.getPeerId())) {
			mShuffledInfoCashForResponse.remove(peerInfo);
		}
		mShuffledInfoCashForResponse.add(peerInfo);
		mInfos.put(peerInfo.getPeerId(), peerInfo);
	}

	public int getPeerInfoAtRamdom(TrackerPeerInfo[] outputInfos) {
		int outputLength = 0;
		Collections.shuffle(mShuffledInfoCashForResponse);
		for (int i = 0; i < outputInfos.length && i < mShuffledInfoCashForResponse.size(); i++) {
			outputInfos[i] = mShuffledInfoCashForResponse.get(i);
			outputLength++;
		}
		return outputLength;
	}

	public TrackerPeerInfo getPeerInfo(String peerId) {
		if(cotains(peerId)) {
			return mInfos.get(peerId);
		} else {
			return null;
		}
	}

	public TrackerPeerInfo updatePeerInfo(HttpRequestURI uri, String ip, int port) {
		isUpdated=true;
		String peerId = HttpObject.parseString(uri.getLine().getRequestURI().getValue(TrackerRequest.KEY_PEER_ID),"");
		if(peerId != null&&!peerId.equals("")&&cotains(peerId)){
			TrackerPeerInfo peerInfo = getPeerInfo(peerId);
			return TrackerPeerInfo.updatePeerInfo(peerInfo, uri, ip, port);		
		} else {
			TrackerPeerInfo peerInfo = TrackerPeerInfo.createPeerInfo(uri, ip, port);
			return peerInfo;
		}
	}
}