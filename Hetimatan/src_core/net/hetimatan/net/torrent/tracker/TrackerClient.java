package net.hetimatan.net.torrent.tracker;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.TreeSet;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.net.http.HttpGet;
import net.hetimatan.net.http.request.HttpGetRequester;
import net.hetimatan.net.http.request.HttpGetResponse;
import net.hetimatan.net.torrent.util.metafile.MetaFile;


public class TrackerClient extends HttpGet {

	private MetaFile mMetaFile = null;
	private long mLastResponse = System.currentTimeMillis();
	private int mInterval = 30;
	private TrackerRequest mRequest = new TrackerRequest();
	private TreeSet<TrackerPeerInfo> mPeers32 = new TreeSet<TrackerPeerInfo>();

	public TrackerClient(MetaFile metafile, String peerId) throws IOException {
		mRequest = new TrackerRequest();
		mRequest
		.putCompact(1)
		.putPeerId(peerId)
		.putHttpVersion(HttpGetRequester.HTTP11)
		.putHeaderUserAgent("Raider/1.0")
		;
		setMetaFile(metafile);
	}

	public void update(String event, long downloaded, long uploaded) {
		mRequest.putEvent(event);
		mRequest.putDownloaded(downloaded);
		mRequest.putUploaded(uploaded);
	}

	public String getCurrentEvent() {
		return mRequest.getEvent();
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

	public int getIntervalPerSec() {
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

	public void setMetaFile(MetaFile metafile) throws IOException {
		try {
			mMetaFile = metafile;
			URI uri = new URI(mMetaFile.getAnnounce());
			int port = uri.getPort();
			if(port == -1) {port = 80;}
			update(uri.getHost(), uri.getPath(), port);
			mRequest
			.putTrackerHost(uri.getHost())
			.putTrackerHost(uri.getHost())
			.putPath(uri.getPath())
			.putTrackerPort(port)
			.putInfoHash(mMetaFile.getInfoSha1AsPercentString());
		} catch(URISyntaxException e) {
			throw new IOException(e);
		}
	}

	@Override
	public HttpGetRequester createGetRequest() {
		return mRequest.createHttpGetRequester();
	}

	@Override
	public void recvBody() throws IOException, InterruptedException {
		System.out.println("TrackerClient#recvBody()");
		HttpGetResponse mResponse = getGetResponse();
		mResponse.readBody();

		if(isRedirect()) {
			close();
			return;
		}
		try {
			CashKyoroFile vf = mResponse.getVF();
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
			int interval = trackerResponse.getInterval();
			
			// todo test guard
			if(interval>10) {
				mInterval = interval;
			}
			mLastResponse = System.currentTimeMillis();
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
		//
		// call this method when received response  
		void onUpdate(TrackerClient client);
	}
}
