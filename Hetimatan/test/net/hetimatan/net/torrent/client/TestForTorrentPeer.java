package net.hetimatan.net.torrent.client;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import net.hetimatan.net.torrent.tracker.TrackerPeerInfo;
import net.hetimatan.net.torrent.tracker.TrackerServer;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.net.torrent.util.metafile.MetaFileCreater;
import net.hetimatan.util.event.EventTaskRunner;
import junit.framework.TestCase;

public class TestForTorrentPeer extends TestCase {

	public void test001() throws IOException, URISyntaxException, InterruptedException {
		TrackerServer server = new TrackerServer();
		server.setPort(6861);
		byte[] infoHash = "abc".getBytes();
		EventTaskRunner runner = null;
		server.addData(infoHash);
		TorrentPeer peer = null;
		MetaFile metafile = MetaFileCreater.createFromTorrentFile(new File("./testdata/1m_a.txt.torrent"));
		server.setInterval(2000);
		try {
			server.startServer(null);
			for(int i=0;!server.isBinded()&&i<1000;i++) {Thread.yield();Thread.sleep(100);}
			peer = new TorrentPeer(metafile, TorrentPeer.createPeerId());
			runner = peer.startTask(null);

			for(int i=0;3<server.getResponceCount()&&i<1000;i++) {Thread.yield();Thread.sleep(100);}
			if(3<server.getResponceCount()) {
				assertTrue(false);
			}
		} finally {
			runner.close();
			server.close();
		}
	}

}
