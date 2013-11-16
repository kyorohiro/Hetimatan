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

	public void testGetPeerListFromTracker() throws IOException, URISyntaxException, InterruptedException {
		byte[] piece = new byte[20];

		MetaFile metaInfo = MetaFileCreater.createFromInfo("http://127.0.0.1:8081", "a.zip", 10, MetaFile.DEFAULT_PIECE_LENGTH, piece);
		
		TrackerServer server = new TrackerServer();
		TorrentClient client1 = new TorrentClient(metaInfo, TorrentClient.createPeerIdAsPercentEncode());
		TorrentClient client2 = new TorrentClient(metaInfo, TorrentClient.createPeerIdAsPercentEncode());
		server.setPort(8081);
		server.addData(metaInfo);
		EventTaskRunner serverRunner = server.startServer(null);
		EventTaskRunner clientRunner1 = client1.startTorrentClient(null);
		EventTaskRunner clientRunner2 = client2.startTorrentClient(null);
		try {
			//
			while(0>=server.getTrackerDB().getManagedData(metaInfo.getInfoSha1AsPercentString()).numOfPeerInfo()) {
				Thread.sleep(100);
			}
			System.out.println("--------AA----"+server.getTrackerDB().getManagedData(metaInfo.getInfoSha1AsPercentString()).numOfPeerInfo());
			//
			while(1>=server.getTrackerDB().getManagedData(metaInfo.getInfoSha1AsPercentString()).numOfPeerInfo()) {
				Thread.sleep(100);
			}
			System.out.println("--------BB----"+server.getTrackerDB().getManagedData(metaInfo.getInfoSha1AsPercentString()).numOfPeerInfo());

//			client1
//			client1.findPeer()
//			assertEquals(expected, actual);
		} finally {
			serverRunner.close();
			server.close();
			clientRunner1.close();
			clientRunner2.close();
			client1.close();
			client2.close();
		}
	}

	public void testSetInterval() {
	}
}
