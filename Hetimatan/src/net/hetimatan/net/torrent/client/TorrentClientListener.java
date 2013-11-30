package net.hetimatan.net.torrent.client;

import net.hetimatan.net.torrent.client.message.TorrentMessage;

public interface TorrentClientListener {
		void onReceiveMessage(TorrentClientFront front, TorrentMessage message);
}

