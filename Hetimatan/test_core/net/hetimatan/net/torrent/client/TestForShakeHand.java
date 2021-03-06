package net.hetimatan.net.torrent.client;

import java.io.IOException;
import java.net.URISyntaxException;

import net.hetimatan.net.torrent.tracker.TrackerServer;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.net.torrent.util.metafile.MetaFileCreater;
import net.hetimatan.util.event.EventTaskRunner;
import junit.framework.TestCase;

public class TestForShakeHand extends TestCase {
	public void testGetPeerListFromTracker() throws IOException, URISyntaxException, InterruptedException {
		byte[] piece = new byte[20];

		MetaFile metaInfo = MetaFileCreater.createFromInfo("http://127.0.0.1:8081", "a.zip", 10, MetaFile.DEFAULT_PIECE_LENGTH, piece);
		
		TrackerServer server = new TrackerServer();
		TorrentClient client1 = new TorrentClient(metaInfo, TorrentClient.createPeerIdAsPercentEncode());
		TorrentClient client2 = new TorrentClient(metaInfo, TorrentClient.createPeerIdAsPercentEncode());
		server.setPort(8081);
		server.addData(metaInfo);
		EventTaskRunner clientRunner1 = null;
		EventTaskRunner clientRunner2 = null;
		EventTaskRunner serverRunner = server.startServer(null);
		try {
			clientRunner1 = client1.startTorrentClient(null);
			//
			while(0>=server.getTrackerDB().getManagedData(metaInfo.getInfoSha1AsPercentString()).numOfPeerInfo()) {
				Thread.sleep(100);
			}
			System.out.println("--------AA----"+server.getTrackerDB().getManagedData(metaInfo.getInfoSha1AsPercentString()).numOfPeerInfo());

			clientRunner2 = client2.startTorrentClient(null);
			//
			while(1>=server.getTrackerDB().getManagedData(metaInfo.getInfoSha1AsPercentString()).numOfPeerInfo()) {
				Thread.sleep(100);
			}
			System.out.println("--------BB----"+server.getTrackerDB().getManagedData(metaInfo.getInfoSha1AsPercentString()).numOfPeerInfo());

			{
				int frontNum = client2.getTorrentPeerManager().numOfFront();
				assertEquals(true, (frontNum==1||frontNum==2?true:false));
			}
			assertEquals(client1.getServerPort(), client2.getTorrentPeerManager().getFrontPeer(0).getPort());

			
			for(int i=0;i<100;i++) {
				if(client2.getTorrentPeerManager().getTorrentFront(0).isShakehanded()) {
					break;
				}
				Thread.sleep(100);
			}
			assertEquals(true, client2.getTorrentPeerManager().getTorrentFront(0).isShakehanded());

		} finally {
			serverRunner.close();
			server.close();
			if(clientRunner1 != null) {clientRunner1.close();}
			if(clientRunner2 != null) {clientRunner2.close();}
			client1.close();
			client2.close();
		}
		
//		Thread.sleep(90000);
	}
}
