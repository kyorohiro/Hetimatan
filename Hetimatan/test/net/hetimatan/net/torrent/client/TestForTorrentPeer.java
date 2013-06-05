package net.hetimatan.net.torrent.client;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import net.hetimatan.io.filen.KFNextHelper;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.net.torrent.tracker.TrackerClient.Peer;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.net.torrent.util.metafile.MetaFileCreater;
import junit.framework.TestCase;

public class TestForTorrentPeer extends TestCase {

	public void testShakehand() throws IOException, URISyntaxException, InterruptedException {
		File metafile = new File("./testdata/1m_a.txt.torrent");
		MetaFile metainfo = MetaFileCreater.createFromTorrentFile(metafile);
		TorrentPeer testPeer = new TorrentPeer(metainfo, TorrentPeer.createPeerId());
		testPeer.startTask(null);
		while(!testPeer.isBooted()){Thread.sleep(0);Thread.yield();}

		TorrentPeer compe = new TorrentPeer(metainfo, TorrentPeer.createPeerId());
		compe.boot();
		Peer peer = new Peer("127.0.0.1", testPeer.getServerPort());
		TorrentFront front = compe.createFront(peer);
		front.connect(peer.getHostName(), peer.getPort());
		while(!front.isConnect()){Thread.sleep(0);Thread.yield();}
		front.sendShakehand();
		front.shakehand();
		front.sendBitfield();
		front.uncoke();
		
		assertEquals(1, testPeer.numOfFront());
		assertEquals(false, testPeer.getTorrentFront(testPeer.getFrontPeer(0)).getTargetInfo().mTargetChoked);

		front.choke();

		testPeer.getTorrentFront(testPeer.getFrontPeer(0))
		.waitMessage(TorrentMessage.SIGN_CHOKE, 3000);
		assertEquals(1, testPeer.numOfFront());
		assertEquals(true, testPeer.getTorrentFront(testPeer.getFrontPeer(0)).getTargetInfo().mTargetChoked);

	}

}
