package net.hetimatan.net.torrent.client;

import java.io.IOException;

import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.net.torrent.tracker.TrackerClient;

public interface TorrentClientListener {
	void onConnection(TorrentClientFront front) throws IOException;
	void onClose(TorrentClientFront front) throws IOException;
	void onClose(TorrentClient client) throws IOException;
	void onShakeHand(TorrentClientFront front) throws IOException;
	void onSendMessage(TorrentClientFront front, TorrentMessage message) throws IOException;
	void onReceiveMessage(TorrentClientFront front, TorrentMessage message) throws IOException;
	void onResponsePeerList(TorrentClient client, TrackerClient tracker) throws IOException;
	void onInterval(TorrentClient client);
}

