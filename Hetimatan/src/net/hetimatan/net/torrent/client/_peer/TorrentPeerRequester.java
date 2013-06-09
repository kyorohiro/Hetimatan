package net.hetimatan.net.torrent.client._peer;

import java.lang.ref.WeakReference;

import net.hetimatan.net.torrent.client.TorrentPeer;

public class TorrentPeerRequester {
	private WeakReference<TorrentPeer> mOwner = null;
	private int mTodoCurrentRequestIndex = 0;

	public TorrentPeerRequester(TorrentPeer peer) {
		mOwner = new WeakReference<TorrentPeer>(peer);
	}

	public int nextPieceId() {
		return mTodoCurrentRequestIndex++;
	}
}
