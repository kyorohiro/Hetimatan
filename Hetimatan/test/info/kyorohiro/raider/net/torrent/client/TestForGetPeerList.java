package info.kyorohiro.raider.net.torrent.client;

import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.tracker.TrackerServer;
import net.hetimatan.net.torrent.util.metafile.MetaFileCreater;
import net.hetimatan.util.event.EventTaskRunner;
import junit.framework.TestCase;

public class TestForGetPeerList extends TestCase {
/*
	public void testTracker() {
		byte[] infoHash =new byte[20];
		MetaFileCreater.createFromTargetFile(targetFile, address)
		TrackerServer server = new TrackerServer();
		TorrentClient client = new TorrentClient(metafile, peerId);
		server.setPort(8081);
		server.addData(infoHash);
		EventTaskRunner runner = server.startServer(null);
		try {
			
		} finally {
			runner.close();
			server.close();
		}
	}
	*/
}
