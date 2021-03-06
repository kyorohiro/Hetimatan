package net.hetimatan.net.torrent.client;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import net.hetimatan.net.torrent.client._client.ConnectTicket;
import net.hetimatan.net.torrent.client._client.MessageTicket;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.net.torrent.tracker.TrackerPeerInfo;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.net.torrent.util.metafile.MetaFileCreater;
import junit.framework.TestCase;

public class TestForTorrentPeerWithChoker extends TestCase {

	public void testChoke() throws Throwable {
		File metafile = new File("./testdata/1m_a.txt.torrent");
		MetaFile metainfo = MetaFileCreater.createFromTorrentFile(metafile);
		TorrentClient testPeer = new TorrentClient(metainfo, TorrentClient.createPeerIdAsPercentEncode());
		testPeer.startTorrentClient(null);
		while(!testPeer.isBooted()){Thread.sleep(0);Thread.yield();}
		ConnectTicket connectaCheck = new ConnectTicket(testPeer);

		// 
		TorrentClient compe = new TorrentClient(metainfo, TorrentClient.createPeerIdAsPercentEncode());
		compe.boot();

		TrackerPeerInfo peer = new TrackerPeerInfo("127.0.0.1", testPeer.getServerPort());
		TorrentClientFront front = compe.createFront(peer);
		
		
		//
		// connect
		//
		front.connect(peer.getHostName(), peer.getPort());
		while(!front.isConnect()){Thread.sleep(0);Thread.yield();}
		connectaCheck.getTorrentClientFront();

		
		
		MessageTicket unchokeCheck = new MessageTicket(
				testPeer.getTorrentPeerManager().getTorrentFront(0),
				TorrentMessage.SIGN_UNCHOKE
		);
		front.sendShakehand();
		front.flushSendTask();
		while(!front.parseableShakehand()) {;}
		front.revcShakehand();
		front.sendBitfield();
		front.flushSendTask();
		front.sendUncoke();
		front.flushSendTask();

		assertEquals(1, testPeer.getTorrentPeerManager().numOfFront());
		unchokeCheck.getMessage();

		assertEquals(TorrentClientFront.FALSE,
		testPeer.getTorrentPeerManager().getTorrentFront(testPeer.getTorrentPeerManager().getFrontPeer(0)).getTargetInfo().isChoked());

		MessageTicket chokeCheck = new MessageTicket(
				testPeer.getTorrentPeerManager().getTorrentFront(0),
				TorrentMessage.SIGN_CHOKE
		);
		front.sendChoke();
		front.flushSendTask();
		chokeCheck.getMessage();
		assertEquals(1, testPeer.getTorrentPeerManager().numOfFront());
		assertEquals(true, testPeer.getTorrentPeerManager().getTorrentFront(testPeer.getTorrentPeerManager().getFrontPeer(0)).getTargetInfo().isChoked() == TorrentClientFront.TRUE);

		testPeer.close();
		compe.close();
	}


	public void testChoker() throws IOException, URISyntaxException, InterruptedException {
		File metafile = new File("./testdata/1m_a.txt.torrent");
		MetaFile metainfo = MetaFileCreater.createFromTorrentFile(metafile);
		TorrentClient testPeer = new TorrentClient(metainfo, TorrentClient.createPeerIdAsPercentEncode());
		File[] f = new File[1];f[0] = new File("./testdata/1mb/1m_a.txt");
		testPeer.setMasterFile(f);
		testPeer.startTorrentClient(null);
		while(!testPeer.isBooted()){Thread.sleep(0);Thread.yield();}

		TorrentClient compe001 = new TorrentClient(metainfo, TorrentClient.createPeerIdAsPercentEncode());
		TorrentClient compe002 = new TorrentClient(metainfo, TorrentClient.createPeerIdAsPercentEncode());
		TorrentClient compe003 = new TorrentClient(metainfo, TorrentClient.createPeerIdAsPercentEncode());
		TorrentClient compe004 = new TorrentClient(metainfo, TorrentClient.createPeerIdAsPercentEncode());
		TorrentClient compe005 = new TorrentClient(metainfo, TorrentClient.createPeerIdAsPercentEncode());
		compe001.boot();
		compe002.boot();
		compe003.boot();
		compe004.boot();
		compe005.boot();

		TrackerPeerInfo peer = new TrackerPeerInfo("127.0.0.1", testPeer.getServerPort());
		TorrentClientFront front001 = compe001.createFront(peer);
		TorrentClientFront front002 = compe002.createFront(peer);
		TorrentClientFront front003 = compe003.createFront(peer);
		TorrentClientFront front004 = compe004.createFront(peer);
		TorrentClientFront front005 = compe005.createFront(peer);

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
		
		front001.flushSendTask();
		front002.flushSendTask();
		front003.flushSendTask();
		front004.flushSendTask();
		front005.flushSendTask();
		
		while(!front001.parseableShakehand()) {;}
		while(!front002.parseableShakehand()) {;}
		while(!front003.parseableShakehand()) {;}
		while(!front004.parseableShakehand()) {;}
		while(!front005.parseableShakehand()) {;}

		front001.revcShakehand();
		front002.revcShakehand();
		front003.revcShakehand();
		front004.revcShakehand();
		front005.revcShakehand();
		//Thread.sleep(3000);
		//
		//
		//
		TorrentClientFrontManager ma = testPeer.getTorrentPeerManager();
		MessageTicket unchokeCheck001 = new MessageTicket(ma.getTorrentFront(0), TorrentMessage.SIGN_UNCHOKE);
		MessageTicket unchokeCheck002 = new MessageTicket(ma.getTorrentFront(1), TorrentMessage.SIGN_UNCHOKE);
		MessageTicket unchokeCheck003 = new MessageTicket(ma.getTorrentFront(2), TorrentMessage.SIGN_UNCHOKE);
		MessageTicket unchokeCheck004 = new MessageTicket(ma.getTorrentFront(3), TorrentMessage.SIGN_UNCHOKE);
		MessageTicket unchokeCheck005 = new MessageTicket(ma.getTorrentFront(4), TorrentMessage.SIGN_UNCHOKE);
		
		
		front001.sendBitfield();
		front002.sendBitfield();
		front003.sendBitfield();
		front004.sendBitfield();
		front005.sendBitfield();
		front001.sendInterest();
		front002.sendInterest();
		front003.sendInterest();
		front004.sendInterest();
		front005.sendInterest();
		front001.sendUncoke();
		front002.sendUncoke();
		front003.sendUncoke();
		front004.sendUncoke();
		front005.sendUncoke();

		front001.flushSendTask();
		front002.flushSendTask();
		front003.flushSendTask();
		front004.flushSendTask();
		front005.flushSendTask();

		front001.startReceliver();
		front002.startReceliver();
		front003.startReceliver();
		front004.startReceliver();
		front005.startReceliver();

		
		assertEquals(5, testPeer.getTorrentPeerManager().numOfFront());
		if(unchokeCheck001.getMessage()==null){assertTrue(false);}
		if(unchokeCheck002.getMessage()==null){assertTrue(false);}
		if(unchokeCheck003.getMessage()==null){assertTrue(false);}
		if(unchokeCheck004.getMessage()==null){assertTrue(false);}
		if(unchokeCheck005.getMessage()==null){assertTrue(false);}
		
		//Thread.sleep(3000);
		assertEquals(TorrentClientFront.FALSE, testPeer.getTorrentPeerManager().getTorrentFront(testPeer.getTorrentPeerManager().getFrontPeer(0)).getTargetInfo().isChoked());
		assertEquals(TorrentClientFront.FALSE, testPeer.getTorrentPeerManager().getTorrentFront(testPeer.getTorrentPeerManager().getFrontPeer(1)).getTargetInfo().isChoked());
		assertEquals(TorrentClientFront.FALSE, testPeer.getTorrentPeerManager().getTorrentFront(testPeer.getTorrentPeerManager().getFrontPeer(2)).getTargetInfo().isChoked());
		assertEquals(TorrentClientFront.FALSE, testPeer.getTorrentPeerManager().getTorrentFront(testPeer.getTorrentPeerManager().getFrontPeer(3)).getTargetInfo().isChoked());
		assertEquals(TorrentClientFront.FALSE, testPeer.getTorrentPeerManager().getTorrentFront(testPeer.getTorrentPeerManager().getFrontPeer(4)).getTargetInfo().isChoked());

//		testPeer.updateOptimusUnchokePeer();
//		Thread.sleep(3000);
		int num = 0;
		for(int i=0;i<5;i++) {
			if(testPeer.getTorrentPeerManager()
					.getTorrentFront(testPeer.getTorrentPeerManager().getFrontPeer(i))
					.getMyInfo().isChoked() != TorrentClientFront.FALSE) {
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
