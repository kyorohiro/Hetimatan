package net.hetimatan.net.torrent.tracker;

import java.net.UnknownHostException;

import net.hetimatan.util.http.HttpObject;
import net.hetimatan.util.io.ByteArrayBuilder;

public class TrackerPeerInfo implements Comparable<TrackerPeerInfo> {
	private byte[] mBuffer = null;
	public TrackerPeerInfo(String host, int port) throws UnknownHostException {
		this(HttpObject.aton(host), HttpObject.portToB(port));
	}

	public TrackerPeerInfo(byte peer[], byte[] port) {
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
		if(obj instanceof TrackerPeerInfo) {
			if(0==compareTo((TrackerPeerInfo)obj)){
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	@Override
	public int compareTo(TrackerPeerInfo o) {
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