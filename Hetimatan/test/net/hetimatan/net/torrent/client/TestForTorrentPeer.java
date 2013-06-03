package net.hetimatan.net.torrent.client;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import net.hetimatan.io.filen.KFNextHelper;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.net.torrent.util.metafile.MetaFileCreater;
import junit.framework.TestCase;

public class TestForTorrentPeer extends TestCase {

	public void testShakehand() throws IOException, URISyntaxException {
		File metafile = new File("./testdata/1m_a.txt.torrent");
		MetaFile metainfo = MetaFileCreater.createFromTorrentFile(metafile);
		TorrentPeer testPeer = new TorrentPeer(metainfo, TorrentPeer.createPeerId());
		testPeer.startTask(null);

		TorrentPeer compe = new TorrentPeer(metainfo, TorrentPeer.createPeerId());
		compe
	}

}
