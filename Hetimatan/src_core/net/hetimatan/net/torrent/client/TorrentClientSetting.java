package net.hetimatan.net.torrent.client;

public class TorrentClientSetting {

	private int mNumOfRequestPerFront = 1;
	public int mNumOfPeer = 50;
	public int mNumOfUnchoker = 4;
	private int mUpdateTime = 30*1000;//ms

	public int getNumOfRequestPerFront() {
		return mNumOfRequestPerFront;
	}

	public int getNumOfUnchoker() {
		return mNumOfUnchoker;
	}

	public int getNumOfConnection() {
		return mNumOfPeer;
	}

	public int getUpdateTime() {
		return mUpdateTime;
	}
}
