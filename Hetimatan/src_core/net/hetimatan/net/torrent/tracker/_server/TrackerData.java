package net.hetimatan.net.torrent.tracker._server;


import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Set;

import net.hetimatan.net.torrent.tracker.TrackerRequest;
import net.hetimatan.util.http.HttpObject;
import net.hetimatan.util.http.HttpRequest;

public class TrackerData {

	private boolean isUpdated = false;
	private int mInterval = 1800;
	private int mComplete = 0;
	private int mIncomplete = 0;
	private byte[] mInfoHash = null;
	private Random mRand = new Random();
	private LinkedHashMap<String, TrackerDatam> mInfos = new LinkedHashMap<String, TrackerDatam>();

	public void setInterval(int interval) {
		mInterval = interval;
	}

	public int getInterval() {
		return mInterval;
	}

	private void update() {
		int completed = 0;
		Iterator<String> keys = mInfos.keySet().iterator();
		while(keys.hasNext()) {
			String key = keys.next();
			TrackerDatam info = mInfos.get(key);
			if(info.isComplete()) {
				completed++;
			}
		}
		mComplete = completed;
		mIncomplete = mInfos.size()-mComplete;
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

	public int numOfPeerInfo() {
		return mInfos.size();
	}

	public String getKeyPeerInfo(int index) {
		Object[] obs = mInfos.keySet().toArray();
		if(obs == null){ return "";}
		if(index<obs.length) {
			Object ob = obs[index];
			if(ob != null) {
				return ob.toString();
			}
		}
		return "";
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

	public void removePeerInfo(TrackerDatam peerInfo) {
		mInfos.remove(peerInfo.getPeerId());
	}

	public boolean cotains(String peerId) {
		return mInfos.containsKey(peerId);
	}

	public void putPeerInfo(TrackerDatam peerInfo) {
		if(peerInfo.getPeerId().length() != 0) {
			mInfos.put(peerInfo.getPeerId(), peerInfo);
		}
	}

	public int getPeerInfoAtRamdom(TrackerDatam[] outputInfos) {
		int outputLength = 0;
		int size = mInfos.size();
		Set<String> keys = mInfos.keySet();
		if(size<outputInfos.length) {
			for (int i = 0; i < size; i++) {
				outputInfos[i] = mInfos.get(keys.toArray()[i]);
				outputLength++;
			}		
		} else {
			for (int i = 0; i < size; i++) {
				outputInfos[i] = mInfos.get(keys.toArray()[mRand.nextInt(size)]);
				outputLength++;
			}
		}
		return outputLength;
	}

	public TrackerDatam getPeerInfo(String peerId) {
		if(cotains(peerId)) {
			return mInfos.get(peerId);
		} else {
			return null;
		}
	}

	public TrackerDatam updatePeerInfo(HttpRequest uri, String ip, int port) {
		isUpdated=true;
		String peerId = HttpObject.parseString(uri.getLine().getRequestURI().getValue(TrackerRequest.KEY_PEER_ID),"");
		if(peerId != null&&!peerId.equals("")&&cotains(peerId)){
			TrackerDatam peerInfo = getPeerInfo(peerId);
			return TrackerDatam.updatePeerInfo(peerInfo, uri, ip, port);		
		} else {
			TrackerDatam peerInfo = TrackerDatam.createPeerInfo(uri, ip, port);
			return peerInfo;
		}
	}
}