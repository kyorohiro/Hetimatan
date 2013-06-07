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

	public void testChoke() throws IOException, URISyntaxException, InterruptedException {
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
		testPeer.getTorrentFront(testPeer.getFrontPeer(0)).waitMessage(TorrentMessage.SIGN_UNCHOKE, 3000);
		assertEquals(false, testPeer.getTorrentFront(testPeer.getFrontPeer(0)).getTargetInfo().mTargetChoked);


		front.choke();
		testPeer.getTorrentFront(testPeer.getFrontPeer(0))
		.waitMessage(TorrentMessage.SIGN_CHOKE, 3000);
		assertEquals(1, testPeer.numOfFront());
		assertEquals(true, testPeer.getTorrentFront(testPeer.getFrontPeer(0)).getTargetInfo().mTargetChoked);

		testPeer.close();
		compe.close();
	}


	@Deprecated
	public void testChoker() throws IOException, URISyntaxException, InterruptedException {
		File metafile = new File("./testdata/1m_a.txt.torrent");
		MetaFile metainfo = MetaFileCreater.createFromTorrentFile(metafile);
		TorrentPeer testPeer = new TorrentPeer(metainfo, TorrentPeer.createPeerId());
		testPeer.startTask(null);
		while(!testPeer.isBooted()){Thread.sleep(0);Thread.yield();}

		TorrentPeer compe001 = new TorrentPeer(metainfo, TorrentPeer.createPeerId());
		TorrentPeer compe002 = new TorrentPeer(metainfo, TorrentPeer.createPeerId());
		TorrentPeer compe003 = new TorrentPeer(metainfo, TorrentPeer.createPeerId());
		TorrentPeer compe004 = new TorrentPeer(metainfo, TorrentPeer.createPeerId());
		TorrentPeer compe005 = new TorrentPeer(metainfo, TorrentPeer.createPeerId());
		compe001.boot();
		compe002.boot();
		compe003.boot();
		compe004.boot();
		compe005.boot();

		Peer peer = new Peer("127.0.0.1", testPeer.getServerPort());
		Peer peer001 = new Peer("127.0.0.1", compe001.getServerPort());
		Peer peer002 = new Peer("127.0.0.1", compe002.getServerPort());
		Peer peer003 = new Peer("127.0.0.1", compe003.getServerPort());
		Peer peer004 = new Peer("127.0.0.1", compe004.getServerPort());
		Peer peer005 = new Peer("127.0.0.1", compe005.getServerPort());

		TorrentFront front001 = compe001.createFront(peer);
		TorrentFront front002 = compe002.createFront(peer);
		TorrentFront front003 = compe003.createFront(peer);
		TorrentFront front004 = compe004.createFront(peer);
		TorrentFront front005 = compe005.createFront(peer);

		front001.connect(peer.getHostName(), peer.getPort());
		front002.connect(peer.getHostName(), peer.getPort());
		front003.connect(peer.getHostName(), peer.getPort());
		front004.connect(peer.getHostName(), peer.getPort());
		front005.connect(peer.getHostName(), peer.getPort());

		while(!front001.isConnect()){Thread.sleep(0);Thread.yield();}
		while(!front002.isConnect()){Thread.sleep(0);Thread.yield();}
		while(!front003.isConnect()){Thread.sleep(0);Thread.yield();}
		while(!front004.isConnect()){Thread.sleep(0);Thread.yield();}
		while(!front005.isConnect()){Thread.sleep(0);Thread.yield();}

		front001.sendShakehand();
		front002.sendShakehand();
		front003.sendShakehand();
		front004.sendShakehand();
		front005.sendShakehand();
		front001.shakehand();
		front002.shakehand();
		front003.shakehand();
		front004.shakehand();
		front005.shakehand();
		front001.sendBitfield();
		front002.sendBitfield();
		front003.sendBitfield();
		front004.sendBitfield();
		front005.sendBitfield();
		front001.interest();
		front002.interest();
		front003.interest();
		front004.interest();
		front005.interest();
		front001.uncoke();
		front002.uncoke();
		front003.uncoke();
		front004.uncoke();
		front005.uncoke();

		assertEquals(5, testPeer.numOfFront());
		testPeer.getTorrentFront(testPeer.getFrontPeer(0)).waitMessage(TorrentMessage.SIGN_UNCHOKE, 3000);
		testPeer.getTorrentFront(testPeer.getFrontPeer(1)).waitMessage(TorrentMessage.SIGN_UNCHOKE, 3000);
		testPeer.getTorrentFront(testPeer.getFrontPeer(2)).waitMessage(TorrentMessage.SIGN_UNCHOKE, 3000);
		testPeer.getTorrentFront(testPeer.getFrontPeer(3)).waitMessage(TorrentMessage.SIGN_UNCHOKE, 3000);
		testPeer.getTorrentFront(testPeer.getFrontPeer(4)).waitMessage(TorrentMessage.SIGN_UNCHOKE, 3000);
		assertEquals(false, testPeer.getTorrentFront(testPeer.getFrontPeer(0)).getTargetInfo().mTargetChoked);
		assertEquals(false, testPeer.getTorrentFront(testPeer.getFrontPeer(1)).getTargetInfo().mTargetChoked);
		assertEquals(false, testPeer.getTorrentFront(testPeer.getFrontPeer(2)).getTargetInfo().mTargetChoked);
		assertEquals(false, testPeer.getTorrentFront(testPeer.getFrontPeer(3)).getTargetInfo().mTargetChoked);
		assertEquals(false, testPeer.getTorrentFront(testPeer.getFrontPeer(4)).getTargetInfo().mTargetChoked);

		Thread.sleep(2000);

		int num = 0;
		for(int i=0;i<5;i++) {
			if(testPeer.getTorrentFront(testPeer.getFrontPeer(i)).getMyInfo().mChoked) {
				num++;
			}
		}
		assertEquals(1, num);

		testPeer.close();
		compe001.close();
		compe002.close();
		compe003.close();
		compe004.close();
		compe005.close();

	}

}
