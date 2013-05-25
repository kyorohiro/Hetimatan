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
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.util.http.HttpObject;
import net.hetimatan.util.io.ByteArrayBuilder;


public class TrackerClient extends HttpGet {

	private MetaFile mMetaFile = null;
	private int mInterval = 0;
	private TrackerRequest mRequest = new TrackerRequest();
	private TreeSet<Peer> mPeers32 = new TreeSet<Peer>();

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
		Peer peer = new Peer(address, port);
		mPeers32.add(peer);
	}

	public synchronized Iterator<Peer> getPeer32() {
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
				mPeers32.add(new Peer(trackerResponse.getIP(i), trackerResponse.getPort(i)));
			}
		} finally {
			close();
		}
	}

	public static class Peer implements Comparable<Peer> {
		private byte[] mBuffer = null;
		public Peer(String host, int port) throws UnknownHostException {
			this(HttpObject.aton(host), HttpObject.portToB(port));
		}

		public Peer(byte peer[], byte[] port) {
			int len = peer.length+port.length;
			mBuffer = new byte[len];
			System.arraycopy(peer, 0, mBuffer, 0, peer.length);
			System.arraycopy(port, 0, mBuffer, peer.length, port.length);
		}

		@Override
		public String toString() {
			return "#"+getHostName()+":"+getPort()+"#";
		}

		@Override
		public int hashCode() {
			return ByteArrayBuilder.parseInt(mBuffer, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN);
		}

		@Override
		public boolean equals(Object obj) {
			if(obj instanceof Peer) {
				if(0==compareTo((Peer)obj)){
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
		@Override
		public int compareTo(Peer o) {
			if(mBuffer.length<o.mBuffer.length) {
				return -1;
			} else if(mBuffer.length>o.mBuffer.length) {				
				return 1;
			}
			int len = mBuffer.length;
			for(int i=0;i<len;i++) {
				if(mBuffer[i]<o.mBuffer[i]) {
					return -1;
				} else 	if(mBuffer[i]>o.mBuffer[i]) {
					return 1;
				}
			}
			return 0;
		}

		public String getHostName() {
			byte[] host = new byte[4];
			byte[] port = new byte[2];
			System.arraycopy(mBuffer, 0, host, 0, 4);
			System.arraycopy(mBuffer, 4, port, 0, 2);
			return HttpObject.ntoa(host);
		}
		public int getPort() {
			byte[] host = new byte[4];
			byte[] port = new byte[2];
			System.arraycopy(mBuffer, 0, host, 0, 4);
			System.arraycopy(mBuffer, 4, port, 0, 2);
			return HttpObject.bToPort(port);		
		}
	}
}
