package net.hetimatan.net.torrent.client;

import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.net.torrent.tracker.TrackerClient;

public interface TorrentClientListener {
	void onReceiveMessage(TorrentClientFront front, TorrentMessage message);
	void onResponsePeerList(TrackerClient client);
}

