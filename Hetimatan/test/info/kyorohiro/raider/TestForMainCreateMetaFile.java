package info.kyorohiro.raider;


import java.io.File;
import java.io.IOException;

import info.kyorohiro.raider.net.torrent.util.MetaFile;
import info.kyorohiro.raider.net.torrent.util.MetaFileCreater;
import junit.framework.TestCase;

public class TestForMainCreateMetaFile extends TestCase {

	public void testHello() {
		;
	}

	public void testSingleMetaFile() throws IOException {
		String[] args = {
				"http://127.0.0.1:6969/announce", 
				"./testdata/1kb/1k.txt"
		};
		MainCreateTorrentFile.main(args);
		MetaFile exp = MetaFileCreater.createFromTorrentFile(new File("./testdata/1k.txt.torrent"));
		MetaFile out = MetaFileCreater.createFromTorrentFile(new File("./a.torrent"));
		assertEquals("http://127.0.0.1:6969/announce", exp.getAnnounce());
		assertEquals("http://127.0.0.1:6969/announce", out.getAnnounce());
		assertEquals(16*1024, exp.getPieceLength());
		assertEquals(16*1024, out.getPieceLength());
		assertEquals(1, exp.getFiles().length);
		assertEquals(1, out.getFiles().length);
		assertEquals("1k.txt", exp.getFiles()[0]);
		assertEquals("1k.txt", out.getFiles()[0]);
		assertEquals(1, exp.getFileLengths().length);
		assertEquals(1, out.getFileLengths().length);
		assertEquals((long)1024, (long)exp.getFileLengths()[0]);
		assertEquals((long)1024, (long)out.getFileLengths()[0]);

		int len = (int)exp.getPieces().toByte().length;
		for(int i=0;i<len;i++) {
			assertEquals(exp.getPieces().toByte()[i], out.getPieces().toByte()[i]);
		}

		assetEqual(exp, out);
	}


	public void testMultiMetaFile() throws IOException {
		String[] args = {
				"http://127.0.0.1:6969/announce", 
				"./testdata/1kb"
		};

		MainCreateTorrentFile.main(args);
		MetaFile exp = MetaFileCreater.createFromTorrentFile(new File("./testdata/1kb.torrent"));
		MetaFile out = MetaFileCreater.createFromTorrentFile(new File("./a.torrent"));

		assetEqual(exp, out);
	}

/*
	public void testMultiMetaFile_02() throws IOException {
		String[] args = {
				"http://127.0.0.1:6969/announce", 
				"./testdata/1mb"
		};

		MainCreateTorrentFile.main(args);
		MetaFile exp = MetaFile.createFromTorrentFile(new File("./testdata/1mb.torrent"));
		MetaFile out = MetaFile.createFromTorrentFile(new File("./a.torrent"));

		assetEqual(exp, out);
	}
*/
	public void assetEqual(MetaFile exp, MetaFile out) {
		assertEquals(exp.getAnnounce(), out.getAnnounce());
		assertEquals(exp.getPieceLength(), out.getPieceLength());
		assertEquals(exp.getFiles().length, out.getFiles().length);
		assertEquals(exp.getFileLengths().length, out.getFileLengths().length);

		for(int i=0;i<exp.getFiles().length;i++) {
			assertEquals(exp.getFiles()[i], out.getFiles()[i]);
		}

		for(int i=0;i<exp.getFileLengths().length;i++) {
			assertEquals(exp.getFileLengths()[i], out.getFileLengths()[i]);
		}

		int len = (int)exp.getPieces().toByte().length;
		for(int i=0;i<len;i++) {
			assertEquals(exp.getPieces().toByte()[i], out.getPieces().toByte()[i]);
		}
	}

}
