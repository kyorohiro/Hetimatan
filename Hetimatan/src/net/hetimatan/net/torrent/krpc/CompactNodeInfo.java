package net.hetimatan.net.torrent.krpc;

import net.hetimatan.util.http.HttpObject;

public class CompactNodeInfo {

	byte[] mInfo = new byte[26];
	public CompactNodeInfo(byte[] info) {
		mInfo = info;
	}

	public byte[] getNodeId() {
		byte[] nodeId = new byte[20];
		System.arraycopy(mInfo, 0, nodeId, 0, 20);
		return nodeId;
	}

	public String getIp() {
		byte[] ip = new byte[4];
		System.arraycopy(mInfo, 20, ip, 0, 4);
		return HttpObject.ntoa(ip);
	}

	public int port() {
		byte[] port = new byte[2];
		System.arraycopy(mInfo, 24, port, 0, 2);
		return HttpObject.bToPort(port);		
	}
}
