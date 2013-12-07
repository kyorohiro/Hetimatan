package net.hetimatan.net.torrent.client._front;

import net.hetimatan.net.torrent.client.TorrentClientFront;
import net.hetimatan.util.bitfield.BitField;

public class TorrentFrontMyInfo {
	public boolean mInterest = false;
	private int mChoked = TorrentClientFront.NONE;
	public BitField mRelative = null;
	public void isChoke(boolean v) {
		if(v) {
			mChoked = TorrentClientFront.TRUE;
		} else {
			mChoked = TorrentClientFront.FALSE;			
		}
	}

	public int isChoked() {
		return mChoked;
	}

}
