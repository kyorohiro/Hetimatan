package net.hetimatan.net.torrent.client;

public class TorrentClientSetting {

	private int mNumOfRequestPerFront = 1;
	public int getNumOfRequestPerFront() {
		return mNumOfRequestPerFront;
	}
	public int mNumOfPeer = 50;
	public int mNumOfUnchoker = 4;
	public int getNumOfUnchoker() {
		return mNumOfUnchoker;
	}
	public int getNumOfConnection() {
		return mNumOfPeer;
	}
}
