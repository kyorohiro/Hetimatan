package net.hetimatan.net.torrent.tracker;

import java.io.IOException;

import net.hetimatan.io.net.KyoroSocket;
import net.hetimatan.ky.io.KyoroFile;
import net.hetimatan.ky.io.next.RACashFile;
import net.hetimatan.net.http.HttpServer;
import net.hetimatan.net.torrent.tracker.db.TrackerDB;
import net.hetimatan.net.torrent.tracker.db.TrackerData;
import net.hetimatan.net.torrent.tracker.db.TrackerPeerInfo;
import net.hetimatan.net.torrent.util.MetaFile;
import net.hetimatan.util.bencode.BenDiction;
import net.hetimatan.util.bencode.BenObject;
import net.hetimatan.util.bencode.BenString;
import net.hetimatan.util.http.HttpGetRequestUri;
import net.hetimatan.util.http.HttpRequestLine;
import net.hetimatan.util.http.HttpRequestURI;
import net.hetimatan.util.url.PercentEncoder;


public class TrackerServer extends HttpServer {
	public static final int DEFAULT_TRACKER_PORT      = 6969;
	public static final String MESSAGE_UNMANAGED_DATA = "your ainfo_hash is unmanaged";
	public static final String MESSAGE_WRONG_REQUEST  = "your request is wrong";
	private TrackerDB mDB = new TrackerDB();

	public static KyoroFile newMessageWrongRequest() throws IOException {
		BenDiction diction = new BenDiction();
		diction.append(TrackerResponse.KEY_FAILURE_REASON, new BenString(MESSAGE_UNMANAGED_DATA));
		return new RACashFile(BenObject.createEncode(diction));
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

	public boolean containHash(HttpRequestURI uri) {
		HttpRequestLine line = uri.getLine();
		HttpGetRequestUri geturi = line.getRequestURI();
		String infoHash = geturi.getValue(TrackerRequest.KEY_INFO_HASH);
		return mDB.isManaged(infoHash);
	}


	@Override
	public KyoroFile createResponse(KyoroSocket socket, HttpRequestURI uri) throws IOException {
		try {
			System.out.println("#request#"+uri.getLine().toString()+"#");
			if (!containHash(uri)) {
				return newMessageWrongRequest();
			}
			TrackerRequest request = TrackerRequest.decode(uri);
			TrackerData trackerData = mDB.getManagedData(mDB.convertInfoHashForRaider(request.getInfoHash()));
			TrackerPeerInfo peerInfo = trackerData.updatePeerInfo(uri, socket.getHost(), socket.getPort());
			trackerData.putPeerInfo(peerInfo);

			BenDiction diction = TrackerResponse.createResponce(trackerData, peerInfo, request.getCompact());
			return new RACashFile(BenObject.createEncode(diction));
		} catch (Exception e) {
			e.printStackTrace();
			return newMessageWrongRequest();
		}
	}


}
