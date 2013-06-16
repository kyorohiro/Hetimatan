package net.hetimatan.net.torrent.tracker;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.TreeSet;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.filen.RACashFile;
import net.hetimatan.net.http.HttpGet;
import net.hetimatan.net.http.request.GetRequesterInter;
import net.hetimatan.net.http.request.GetResponseInter;
import net.hetimatan.net.torrent.tracker.TrackerServer.StatusCheck;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.util.http.HttpObject;
import net.hetimatan.util.io.ByteArrayBuilder;


public class TrackerClient extends HttpGet {

	private MetaFile mMetaFile = null;
	private int mInterval = 0;
	private TrackerRequest mRequest = new TrackerRequest();
	private TreeSet<TrackerPeerInfo> mPeers32 = new TreeSet<TrackerPeerInfo>();

	public TrackerClient(MetaFile metafile, String peerId) throws URISyntaxException, IOException {
		mRequest = new TrackerRequest();
		mRequest
		.putCompact(1)
		.putPeerId(peerId)
		.putHttpVersion(GetRequesterInter.HTTP11)
		.putHeaderUserAgent("Raider/1.0")
		;
		setMetaFile(metafile);
	}

	public void updateStatus(long downloaded, long lefted, long uploaded) {
		mRequest
		.putDownloaded(downloaded)
		.putLeft(lefted)
		.putUploaded(uploaded);		
	}

	public int numOfPiece() {
		return mMetaFile.getPieces().byteLength()/20;
	}

	public int getTrackerPort() {
		return mRequest.getPort();
	}

	public void setClientPort(int port) {
		mRequest.putClientPort(port);
	}

	public int getClientPort() {
		return mRequest.getClientPort();
	}

	public int getInterval() {
		return mInterval;
	}	

	public String getPeerId() {
		return mRequest.getPeerId();
	}

	public String getInfoHash() {
		return mRequest.getInfoHash();
	}

	public synchronized int numOfPeers32() {
		return mPeers32.size();
	}

	public synchronized void putPeers32(byte[] address, byte[] port) {
		TrackerPeerInfo peer = new TrackerPeerInfo(address, port);
		mPeers32.add(peer);
	}

	public synchronized Iterator<TrackerPeerInfo> getPeer32() {
		return mPeers32.iterator();
	}

	public void clearPeer32() {
		mPeers32.clear();
	}

	public void setMetaFile(MetaFile metafile) throws IOException, URISyntaxException {
		mMetaFile = metafile;
		URI uri = new URI(mMetaFile.getAnnounce());
		mRequest
		.putTrackerHost(uri.getHost())
		.putPath(uri.getPath())
		.putTrackerPort(uri.getPort())
		.putInfoHash(mMetaFile.getInfoSha1AsPercentString());
	}

	@Override
	public GetRequesterInter createGetRequest() {
		return mRequest.encodeToGetRequester();
	}

	@Override
	public void recvBody() throws IOException, InterruptedException {
		System.out.println("TrackerClient#recvBody()");
		GetResponseInter mResponse = getGetResponse();
		mResponse.readBody();

		try {
			RACashFile vf = mResponse.getVF();
			vf.seek(mResponse.getVFOffset());
			int len = (int)vf.length();
			byte[] buffer = new byte[len];
			vf.read(buffer, 0, len);
			System.out.println("@1:"+new String(buffer));
			System.out.println("@2:"+new String(buffer,mResponse.getVFOffset(),buffer.length-mResponse.getVFOffset()));
			System.out.println("@3:"+mResponse.getVFOffset()+","+buffer.length);

			MarkableFileReader reader = new MarkableFileReader(vf, 512);
			reader.seek(mResponse.getVFOffset());
			TrackerResponse trackerResponse = TrackerResponse.decode(reader);
			System.out.println("@4:compact="+trackerResponse.getCompact());
			System.out.println("@5:complete="+trackerResponse.getComplete());
			System.out.println("@6:incompact="+trackerResponse.getIncomplete());
			System.out.println("@7:interval="+trackerResponse.getInterval());
			System.out.println("@8:failre="+trackerResponse.getFailureReason());
			System.out.println("@9:warning="+trackerResponse.getWarningMessage());
			for(int i=0;i<trackerResponse.numOfIp();i++) {
				System.out.println("@ip["+i+"]="+trackerResponse.getIP(i)+":"+trackerResponse.getPort(i));					
				mPeers32.add(new TrackerPeerInfo(trackerResponse.getIP(i), trackerResponse.getPort(i)));
			}
		} finally {
			close();
		}
		kickObserver();
	}

	//
	// --------------------------------------------
	//
	private StatusCheck mObserver = null;

	public synchronized void setStatusCheck(StatusCheck observer) {
		mObserver = observer;
	}

	public synchronized void kickObserver() {
		if(mObserver != null) {
			mObserver.onUpdate(this);
		}
	}
	public interface StatusCheck {
		void onUpdate(TrackerClient client);
	}
}
