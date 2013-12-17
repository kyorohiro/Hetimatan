package net.hetimatan.net.torrent.tracker._server;

import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.tracker.TrackerRequest;
import net.hetimatan.util.http.HttpRequestUri;
import net.hetimatan.util.http.HttpObject;
import net.hetimatan.util.http.HttpRequestLine;
import net.hetimatan.util.http.HttpRequest;

@Deprecated
public class TrackerDatam {
	private String mInfoHash = "";
	private String mPeerId = "";
	private String mKey = "";
	private String mTrackerId = "";
	private int mPort = TorrentClient.TORRENT_PORT_BEGIN;
	private int mUploaded = 0;
	private int mDownloaded = 0;
	private int mLeft = 0;
	private String mEvent = TrackerRequest.EVENT_STARTED;
	private String mIp = "127.0.0.1";

	private TrackerDatam() {}

	public String getInfoHash() {
		return mInfoHash;
	}

	public String getPeerId() {
		return mPeerId;
	}

	public String getKey() {
		return mKey;
	}

	public String getTrackerId() {
		return mTrackerId;
	}

	public String getIP() {
		return mIp;
	}

	public int getPort() {
		return mPort;
	}

	public int getUploaded() {
		return mUploaded;
	}

	public int getDownloaded() {
		return mDownloaded;
	}

	public int getLeft() {
		return mLeft;
	}

	public String getEvent() {
		return mEvent;
	}

	public boolean isComplete() {
		if(mEvent.equals(TrackerRequest.EVENT_COMPLETED)) {
			return true;
		} else {
			return false;
		}
	}

	public void setTrackerId(String trackerId) {
		mTrackerId = trackerId;
	}

	public static TrackerDatam createPeerInfo(HttpRequest uri, String ip, int port) {
		TrackerDatam peerInfo = new TrackerDatam();
		updatePeerInfo(peerInfo, uri, ip, port);
		return peerInfo;
	}

	public static TrackerDatam updatePeerInfo(TrackerDatam peerInfo, HttpRequest uri, String ip, int port) {
		HttpRequestLine line   = uri.getLine();
		HttpRequestUri path = line.getRequestURI();
		peerInfo.mDownloaded = HttpObject.parseInt(path.getValue(TrackerRequest.KEY_DOWNLOADED), peerInfo.mDownloaded);
		peerInfo.mLeft       = HttpObject.parseInt(path.getValue(TrackerRequest.KEY_LEFT), peerInfo.mLeft);
		peerInfo.mUploaded   = HttpObject.parseInt(path.getValue(TrackerRequest.KEY_UPLOADED), peerInfo.mUploaded);
		peerInfo.mIp    = ip;
		peerInfo.mPort  = HttpObject.parseInt(path.getValue(TrackerRequest.KEY_PORT), peerInfo.mPort);
		peerInfo.mEvent = HttpObject.parseString(path.getValue(TrackerRequest.KEY_EVENT), peerInfo.mEvent);
		peerInfo.mKey   = HttpObject.parseString(path.getValue(TrackerRequest.KEY_KEY), peerInfo.mKey);
		peerInfo.mTrackerId = HttpObject.parseString(path.getValue(TrackerRequest.KEY_TRACKERID), peerInfo.mTrackerId);
		peerInfo.mInfoHash  = HttpObject.parseString(path.getValue(TrackerRequest.KEY_INFO_HASH), peerInfo.mInfoHash);
		peerInfo.mPeerId    = HttpObject.parseString(path.getValue(TrackerRequest.KEY_PEER_ID), peerInfo.mPeerId);
		return peerInfo;
	}

}
