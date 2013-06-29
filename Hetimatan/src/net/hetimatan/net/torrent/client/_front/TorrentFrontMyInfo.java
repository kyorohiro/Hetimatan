package net.hetimatan.net.torrent.client._front;

import net.hetimatan.net.torrent.client.TorrentFront;
import net.hetimatan.util.bitfield.BitField;

public class TorrentFrontMyInfo {
	public boolean mInterest = false;
	private int mChoked = TorrentFront.NONE;
	public BitField mRelative = null;
	public void isChoke(boolean v) {
		if(v) {
			mChoked = TorrentFront.TRUE;
		} else {
			mChoked = TorrentFront.FALSE;			
		}
	}

	public int isChoked() {
		return mChoked;
	}

}
