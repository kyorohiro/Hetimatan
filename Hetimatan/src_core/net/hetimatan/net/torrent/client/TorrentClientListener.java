package net.hetimatan.net.torrent.client;

import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.net.torrent.tracker.TrackerClient;

public interface TorrentClientListener {
	void onClose(TorrentClientFront front);
	void onShakeHand(TorrentClientFront front);
	void onReceiveMessage(TorrentClientFront front, TorrentMessage message);
	void onResponsePeerList(TrackerClient client);
}

