package net.hetimatan.net.torrent.client;

import java.io.IOException;
import java.net.URISyntaxException;

import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.tracker.TrackerServer;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.net.torrent.util.metafile.MetaFileCreater;
import net.hetimatan.util.event.EventTaskRunner;
import junit.framework.TestCase;

public class TestForGetPeerList extends TestCase {

	public void testTracker() throws IOException, URISyntaxException {
		byte[] infoHash =new byte[20];
		byte[] piece = new byte[20];
		String peerId = TorrentClient.createPeerIdAsPercentEncode();
		MetaFile metaInfo = MetaFileCreater.createFromInfo("127.0.0.1", "a.zip", 10, MetaFile.DEFAULT_PIECE_LENGTH, piece);
		
		TrackerServer server = new TrackerServer();
		TorrentClient client = new TorrentClient(metaInfo, peerId);
		server.setPort(8081);
		server.addData(infoHash);
		EventTaskRunner runner = server.startServer(null);
		try {
			
		} finally {
			runner.close();
			server.close();
		}
	}

}
