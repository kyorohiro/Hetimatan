package net.hetimatan.net.torrent.tracker;


import java.io.IOException;

import net.hetimatan.net.http.request.HttpGetRequestUriBuilder;
import net.hetimatan.net.http.request.HttpGetRequester;
import net.hetimatan.net.http.request.HttpGetRequester;
import net.hetimatan.net.torrent.client.TorrentPeer;
import net.hetimatan.util.http.HttpObject;
import net.hetimatan.util.http.HttpRequestLine;
import net.hetimatan.util.http.HttpRequest;
import net.hetimatan.util.http.HttpRequestUri;


public class TrackerRequest {
	public static final String KEY_INFO_HASH   = "info_hash";
	public static final String KEY_PEER_ID     = "peer_id";
	public static final String KEY_PORT        = "port";
	public static final String KEY_UPLOADED    = "uploaded";
	public static final String KEY_DOWNLOADED  = "downloaded";
	public static final String KEY_LEFT        = "left";
	public static final String KEY_COMPACT     = "compact";
	public final static String KEY_EVENT       = "event";
	public final static String KEY_NUMWANT     = "numwant";
	public final static String KEY_KEY         = "key";
	public final static String KEY_TRACKERID   = "trackerid";
	public final static String EVENT_STARTED   = "started";
	public final static String EVENT_STOPPED   = "stopped";
	public final static String EVENT_COMPLETED = "completed";


	private String mPath = "/announce";
	private String mHttpVersion = HttpRequestLine.HTTP10;
	private String mInfoHash = "dummy";
	private String mPeerId = "dummy";
	private int mTrackerPort = TrackerServer.DEFAULT_TRACKER_PORT;
	private int mClientPort = TorrentPeer.TORRENT_PORT_BEGIN;
	
	private long mDownloaded = 0;
	private long mUploaded = 0;
	private long mLeft = 0;
	private int mCompact = 1;
	private String mEvent = TrackerRequest.EVENT_STARTED;
	private String mTrackerId = "dummy";
	private String mTrackerHost = "127.0.0.1";
	private String mUserAgent = "Raider001";

	public TrackerRequest() {
	}

	public static TrackerRequest decode(HttpRequest uri) {
		TrackerRequest request = new TrackerRequest();
		request.mPath = uri.getLine().getRequestURI().getPath();
		request.mHttpVersion = uri.getLine().getHttpVersion();
		request.putTrackerHost(uri.getHeaderValue(HttpGetRequester.HEADER_HOST));

		request
		.putInfoHash(HttpObject.parseString(uri.getValue(TrackerRequest.KEY_INFO_HASH), "dummy"))
		.putPeerId(HttpObject.parseString(uri.getValue(TrackerRequest.KEY_PEER_ID), "dummy"))
		.putClientPort(HttpObject.parseInt(uri.getValue(TrackerRequest.KEY_PORT),request.mClientPort))
		.putUploaded(HttpObject.parseInt(uri.getValue(TrackerRequest.KEY_UPLOADED), 0))
		.putDownloaded(HttpObject.parseInt(uri.getValue(TrackerRequest.KEY_DOWNLOADED), 0))
		.putLeft(HttpObject.parseInt(uri.getValue(TrackerRequest.KEY_LEFT), 0))
		.putCompact(HttpObject.parseInt(uri.getValue(TrackerRequest.KEY_COMPACT), 0))
		.putEvent(HttpObject.parseString(uri.getValue(TrackerRequest.KEY_EVENT), "empty"))
		.putHeaderUserAgent(uri.getHeaderValue(HttpGetRequester.USER_AGENT));
		return request;
	}

	public HttpRequestUri createUri() throws IOException {
		HttpGetRequestUriBuilder builder = new HttpGetRequestUriBuilder();
		builder
		.setHost(mTrackerHost)
		.setPort(mTrackerPort)
		.putValue(KEY_INFO_HASH, mInfoHash)
		.putValue(KEY_PEER_ID, mPeerId)
		.putValue(KEY_PORT, "" + mClientPort)
		.putValue(KEY_UPLOADED, "" + mUploaded)
		.putValue(KEY_DOWNLOADED, "" + mDownloaded)
		.putValue(KEY_LEFT, "" + mLeft)
		.putValue(KEY_COMPACT, "" + mCompact)
		.putValue(KEY_EVENT, mEvent);
		return builder.createHttpRequestUri();
	}

	public HttpGetRequester createHttpGetRequester() {
		HttpGetRequester ret = (new HttpGetRequester());
		ret.getUrlBuilder()
		.setHost(mTrackerHost)
		.setPath(mPath)
		.setPort(mTrackerPort)
		.setHttpVersion(mHttpVersion)
		.putValue(KEY_INFO_HASH, mInfoHash)
		.putValue(KEY_PEER_ID, mPeerId)
		.putValue(KEY_PORT, "" + mClientPort)
		.putValue(KEY_UPLOADED, "" + mUploaded)
		.putValue(KEY_DOWNLOADED, "" + mDownloaded)
		.putValue(KEY_LEFT, "" + mLeft)
		.putValue(KEY_COMPACT, "" + mCompact)
		.putValue(KEY_EVENT, mEvent)
		.putHeader(HttpGetRequester.USER_AGENT, mUserAgent);
		return ret;
	}



	public String getPath() {return mPath;}
	public String getHttpVersion() {return  mHttpVersion;}
	public String getInfoHash() {return mInfoHash;}
	public String getPeerId(){return mPeerId;}
	public int getPort() {return mTrackerPort;}
	public long getDownloaded() { return mDownloaded;}
	public long getUploaded() { return mUploaded;}
	public long getLeft() {return mLeft;}
	public int getCompact() {return mCompact;}
	public String getEvent() {return mEvent;}
	public String getHost() {return mTrackerHost;}
	public String getUserAgent(){return mUserAgent;}
	public int getClientPort() {return mClientPort;}

	public TrackerRequest putClientPort(int port) {
		mClientPort = port;
		return this;
	}

	public TrackerRequest putPath(String path){
		mPath = path;
		return this;
	}

	public TrackerRequest putHttpVersion(String httpVersion) {
		mHttpVersion = httpVersion;
		return this;
	}

	public TrackerRequest putInfoHash(String sha1hash) {
		mInfoHash = sha1hash;
		return this;
	}

	public TrackerRequest putPeerId(String peerId) {
		mPeerId = peerId;
		return this;
	}

	public TrackerRequest putTrackerHost(String host) {
		mTrackerHost = host;
		return this;
	}

	public TrackerRequest putTrackerPort(int port) {
		mTrackerPort = port;
		return this;
	}

	public TrackerRequest putUploaded(long uploaded) {
		mUploaded = uploaded;
		return this;
	}

	public TrackerRequest putDownloaded(long downloaded) {
		mDownloaded = downloaded;
		return this;
	}

	public TrackerRequest putLeft(long left) {
		mLeft = left;
		return this;
	}

	// 1 or 0
	public TrackerRequest putCompact(int compact) {
		mCompact = compact;
		return this;
	}

	public TrackerRequest putEvent(String event) {
		mEvent = event;
		return this;
	}

	public TrackerRequest putHeaderUserAgent(String ua) {
		mUserAgent = ua;
		return this;
	}
	
}
