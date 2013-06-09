package net.hetimatan.net.torrent.client._peer;

import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentData;
import net.hetimatan.net.torrent.client.TorrentPeer;
import net.hetimatan.util.bitfield.BitField;

public class TorrentPeerRequester {
	private WeakReference<TorrentPeer> mOwner = null;
	private int mTodoCurrentRequestIndex = 0;

	public TorrentPeerRequester(TorrentPeer peer) {
		mOwner = new WeakReference<TorrentPeer>(peer);
	}

	public int nextPieceId() {
		TorrentPeer peer = mOwner.get();
		TorrentData data = peer.getTorrentData();
		BitField bitfield = data.getStockedDataInfo();
		return mTodoCurrentRequestIndex++;
	}
}
