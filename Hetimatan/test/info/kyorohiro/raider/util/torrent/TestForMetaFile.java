package info.kyorohiro.raider.util.torrent;

import java.io.File;
import java.io.IOException;

import info.kyorohiro.helloworld.io.next.RACashFile;
import info.kyorohiro.raider.net.torrent.util.MetaFile;
import info.kyorohiro.raider.net.torrent.util.MetaFileCreater;
import info.kyorohiro.raider.util.bencode.BenString;
import junit.framework.TestCase;

public class TestForMetaFile extends TestCase {

	@Override
	protected void setUp() throws Exception {
//		System.out.println("###setup()###");
		File dummy = new File("./dummy");
		if(dummy.exists()) {
			dummy.delete();
		}
		super.setUp();
	}

	public void testHello() {
		
	}

	public File getTestData(String filename) {
		String path = getClass().getName();
		String tmp = path.replaceAll("[^\\.]*$", "");
		tmp = "./test/"+tmp.replaceAll("\\.", "/");
		File ret = new File(new File(tmp), filename);
		return ret;
	}

	public void testReadSingleFileSample() throws IOException {
		MetaFile torrent = MetaFileCreater.createFromTorrentFile(getTestData("singlefile.torrent"));
		assertEquals("http://127.0.0.1:8080/announce", torrent.getAnnounce());
		assertEquals(1, torrent.getFiles().length);
		assertEquals("moon.jpg", torrent.getFiles()[0]);
		assertEquals((long)2856, (long)torrent.getFileLengths()[0]);
		assertEquals((long)262144, (long)torrent.getPieceLength());
		BenString bstring = torrent.getPieces();

		byte[] expected = {
				 -2, -59, -79, -29,  4,  37,
				114,  76, -25,  26, 96,  56,
				106,  45,  16, 124, 24,-110,
				 56, 103};
		//debug(bstring);
		for(int i=0;i<expected.length;i++) {
			assertEquals(""+i+","+expected[i], expected[i], bstring.toByte()[i]);
		}
		assertTrue(true);		
	}

	public void testReadMultiFileSample() throws IOException {
		MetaFile torrent = MetaFileCreater.createFromTorrentFile(getTestData("multifile.torrent"));
		assertEquals("http://127.0.0.1:8080/announce", torrent.getAnnounce());
		assertEquals(2, torrent.getFiles().length);
		assertEquals("img/moon001.jpg", torrent.getFiles()[0]);
		assertEquals("img/moon002.jpg", torrent.getFiles()[1]);
		assertEquals((long)2856, (long)torrent.getFileLengths()[0]);
		assertEquals((long)2856, (long)torrent.getFileLengths()[1]);

		assertEquals((long)262144, (long)torrent.getPieceLength());
		BenString bstring = torrent.getPieces();

		byte[] expected = {
				-86, -72,  8, -71, -90, -64,
				 84,  -2,120,   8, -23, 104,
				 30, 127,-97,-109,-122,-127,
			   -110, 77};
		//debug(bstring);
		for(int i=0;i<expected.length;i++) {
			assertEquals(""+i+","+expected[i], expected[i], bstring.toByte()[i]);
		}
		assertTrue(true);		
	}

	public void testCreateSingleFileSample() throws IOException {
		MetaFile torrent = 
				MetaFileCreater.createFromTargetFile(getTestData("moon.jpg"), "http://127.0.0.1:8080/announce");

		assertEquals("http://127.0.0.1:8080/announce", torrent.getAnnounce());
		assertEquals(1, torrent.getFiles().length);
		assertEquals("moon.jpg", torrent.getFiles()[0]);
		assertEquals((long)2856, (long)torrent.getFileLengths()[0]);
		assertEquals((long)MetaFile.DEFAULT_PIECE_LENGTH, (long)torrent.getPieceLength());
		BenString bstring = torrent.getPieces();

		byte[] expected = {
				 -2, -59, -79, -29,  4,  37,
				114,  76, -25,  26, 96,  56,
				106,  45,  16, 124, 24,-110,
				 56, 103};
		//debug(bstring);
		for(int i=0;i<expected.length;i++) {
			assertEquals(""+i+","+expected[i], expected[i], bstring.toByte()[i]);
		}
		assertTrue(true);		
	}

	public void testSaveSingleFileSample() throws IOException {
		{ //create
			MetaFile torrent = MetaFileCreater.createFromTargetFile(
					getTestData("moon.jpg"), "http://127.0.0.1:8080/announce");
			File dummy = new File("./dummy");
			dummy.delete();
			RACashFile output = new RACashFile(dummy, 512, 2);
			torrent.save(output);
		}
		{
			MetaFile torrent = MetaFileCreater.createFromTorrentFile(new File("./dummy"));

			assertEquals("http://127.0.0.1:8080/announce", torrent.getAnnounce());
			assertEquals(1, torrent.getFiles().length);
			assertEquals("moon.jpg", torrent.getFiles()[0]);
			assertEquals((long)2856, (long)torrent.getFileLengths()[0]);
			assertEquals((long)MetaFile.DEFAULT_PIECE_LENGTH, (long)torrent.getPieceLength());
			BenString bstring = torrent.getPieces();

			byte[] expected = {
					-2, -59, -79, -29,  4,  37,
					114,  76, -25,  26, 96,  56,
					106,  45,  16, 124, 24,-110,
					56, 103};
			//debug(bstring);
			for(int i=0;i<expected.length;i++) {
				assertEquals(""+i+","+expected[i], expected[i], bstring.toByte()[i]);
			}
		}
		assertTrue(true);		
	}

	public void debug(BenString bstring) {
		byte[] buffer = bstring.toByte();
		StringBuilder builder = new StringBuilder();
		for(int i=0;i<buffer.length;i++) {
			builder.append(","+buffer[i]);
		}
		System.out.println("#"+builder.toString()+"#");
	}

}
