package net.hetimatan.net.torrent.tracker;

import java.io.IOException;

import net.hetimatan.io.file.KyoroFile;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.io.net.KyoroSocket;
import net.hetimatan.net.http.HttpServer;
import net.hetimatan.net.torrent.tracker.db.TrackerDB;
import net.hetimatan.net.torrent.tracker.db.TrackerData;
import net.hetimatan.net.torrent.tracker.db.TrackerDatam;
import net.hetimatan.net.torrent.util.bencode.BenDiction;
import net.hetimatan.net.torrent.util.bencode.BenObject;
import net.hetimatan.net.torrent.util.bencode.BenString;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.util.event.SingleTaskRunner;
import net.hetimatan.util.http.HttpRequestUri;
import net.hetimatan.util.http.HttpRequestLine;
import net.hetimatan.util.http.HttpRequest;
import net.hetimatan.util.url.PercentEncoder;


public class TrackerServer extends HttpServer {
	public static final int DEFAULT_TRACKER_PORT      = 6969;
	public static final String MESSAGE_UNMANAGED_DATA = "your ainfo_hash is unmanaged";
	public static final String MESSAGE_WRONG_REQUEST  = "your request is wrong";

	private StatusCheck mObserver = null;
	private TrackerDB mDB = new TrackerDB();
	private long mResponceCount = 0;
	private int mInterval = 1800;
	

	public static KyoroFile newMessageWrongRequest() throws IOException {
		BenDiction diction = new BenDiction();
		diction.put(TrackerResponse.KEY_FAILURE_REASON, new BenString(MESSAGE_UNMANAGED_DATA));
		return new CashKyoroFile(BenObject.createEncode(diction));
	}

	public void setInterval(int interval) {
		mInterval = interval;
	}

	public TrackerDB getTrackerDB() {
		return mDB;
	}

	public long getResponceCount() {
		return mResponceCount;
	}

	public void addData(byte[] infoHash) {
		PercentEncoder encoder = new PercentEncoder();
		System.out.println("infohash:#"+encoder.encode(infoHash)+"#");
		mDB.addManagedData(infoHash);
	}

	public void addData(MetaFile file) throws IOException {
		BenString infoHash = file.getInfoSha1AsBenString();
		addData(infoHash.toByte());
	}

	public boolean containHash(HttpRequest uri) {
		HttpRequestLine line = uri.getLine();
		HttpRequestUri geturi = line.getRequestURI();
		String infoHash = geturi.getValue(TrackerRequest.KEY_INFO_HASH);
		return mDB.isManaged(infoHash);
	}

	@Override
	public KyoroFile createContent(KyoroSocket socket, HttpRequest uri) throws IOException {
		mResponceCount++;
		try {
			System.out.println("#request#"+uri.getLine().toString()+"#");
			if (!containHash(uri)) {
				return newMessageWrongRequest();
			}

			TrackerRequest request = TrackerRequest.decode(uri);
			TrackerData trackerData = mDB.getManagedData(mDB.convertInfoHashForRaider(request.getInfoHash()));
			TrackerDatam peerInfo = trackerData.updatePeerInfo(uri, socket.getHost(), socket.getPort());
			trackerData.putPeerInfo(peerInfo);
			trackerData.setInterval(mInterval);

			BenDiction diction = TrackerResponse.createResponce(trackerData, peerInfo, request.getCompact());
			kickObserver(1);
			return new CashKyoroFile(BenObject.createEncode(diction));
		} catch (Exception e) {
			e.printStackTrace();
			return newMessageWrongRequest();
		}
	}
	@Override
	public void boot() throws IOException {
		super.boot();
		kickObserver(0);
	}
	public synchronized void setStatusCheck(StatusCheck observer) {
		mObserver = observer;
	}

	public synchronized void kickObserver(int event) {
		if(mObserver != null) {
			mObserver.onUpdate(this, event);
		}
	}

	public interface StatusCheck {
		void onUpdate(TrackerServer server, int event);
	}
}
