package net.hetimatan.net.torrent.client;

import java.io.IOException;

import junit.framework.TestCase;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.net.torrent.util.metafile.MetaFileCreater;

public class TestForTorrentData extends TestCase {

	public void testTorrentData() throws IOException {
		String address = "http://127.0.0.1";
		String path ="a.tmo";
		int fileLength = 20000;
		int pieceLength = 16*1024;
		byte[] piece = new byte[fileLength];
		MetaFile metainfo = MetaFileCreater.createFromInfo(address, path, fileLength, pieceLength, piece);
		
		TorrentData data = new TorrentData(metainfo);
		assertEquals(16*1024, data.getPieceLengthPer(0));
		assertEquals(20000-(16*1024), data.getPieceLengthPer(1));
	}
}
