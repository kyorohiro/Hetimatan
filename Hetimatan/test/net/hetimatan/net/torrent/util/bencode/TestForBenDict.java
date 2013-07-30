package net.hetimatan.net.torrent.util.bencode;

import java.io.File;
import java.io.IOException;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.net.torrent.util.bencode.BenDiction;
import net.hetimatan.net.torrent.util.bencode.BenInteger;
import net.hetimatan.net.torrent.util.bencode.BenObject;
import net.hetimatan.net.torrent.util.bencode.BenString;
import junit.framework.TestCase;

public class TestForBenDict extends TestCase {

	public void testHello() {

	}

	@Override
	protected void setUp() throws Exception {
//		System.out.println("###setup()###");
		File dummy = new File("./dummy_002");
		if(dummy.exists()) {
			dummy.delete();
		}
		super.setUp();
	}
	@Override
	protected void tearDown() throws Exception {
		File dummy = new File("./dummy_002");
		if(dummy.exists()) {
			dummy.delete();
		}
		super.tearDown();
	}
	public void testEncode001() throws IOException {
		BenDiction bDict = new BenDiction();
		bDict.append("001", new BenInteger(100));
		CashKyoroFile output = new CashKyoroFile(new File("./dummy_002"), 512, 2);
		try {
		bDict.encode(output.getLastOutput());
		output.seek(0);
		byte[] buffer = new byte[(int)output.length()];
		int len = output.read(buffer);
		String tag = new String(buffer, 0, len);
		assertEquals("d3:001i100ee", tag);
		} finally {
			output.close();
		}
	}

	public void testEncode002() throws IOException {
		BenDiction bDict = new BenDiction();

		bDict.append("001", new BenString("abc"));
		CashKyoroFile output = new CashKyoroFile(new File("./dummy_002"), 512, 2);
		try {
		bDict.encode(output.getLastOutput());
		output.seek(0);
		byte[] buffer = new byte[(int)output.length()];
		int len = output.read(buffer);
		String tag = new String(buffer, 0, len);
		assertEquals("d3:0013:abce", tag);
		} finally {
			output.close();
		}
	}

	public void testEncode003() throws IOException {
		BenDiction bDict = new BenDiction();
		bDict.append("001", new BenString("abc"));
		bDict.append("abcd", new BenString("abc"));
		CashKyoroFile output = new CashKyoroFile(new File("./dummy_002"), 512, 2);
		try {
			bDict.encode(output.getLastOutput());
			output.seek(0);
			byte[] buffer = new byte[(int)output.length()];
			int len = output.read(buffer);
			String tag = new String(buffer, 0, len);
			System.out.println("#"+tag+"#");
			assertEquals("d3:0013:abc4:abcd3:abce", tag);
		} finally {
			output.close();
		}
	}

	public void testDecode001() throws IOException {
		File dummy = new File("./dummy_002");
		dummy.delete();
		CashKyoroFile base = new CashKyoroFile(new File("./dummy_002"), 512, 2);
		base.addChunk(("d3:001i100ee").getBytes());
		MarkableFileReader reader = new MarkableFileReader(base, 512);

		try {
			BenObject value1 = BenDiction.decodeDiction(reader);
			assertEquals(BenObject.TYPE_DICT, value1.getType());
			assertEquals(1, value1.size());
			assertEquals(BenObject.TYPE_INTE, value1.getBenValue("001").getType());
			assertEquals(100, value1.getBenValue("001").toInteger());
		} finally {
			reader.close();
		}
	}

	public void testDecode002() throws IOException {
		File dummy = new File("./dummy_002");
		dummy.delete();
		CashKyoroFile base = new CashKyoroFile(new File("./dummy_002"), 512, 2);
		base.addChunk(("d3:0013:abc4:abcd4:abcde").getBytes());
		MarkableFileReader reader = new MarkableFileReader(base, 512);

		try {
			BenObject value1 = BenDiction.decodeDiction(reader);
			assertEquals(BenObject.TYPE_DICT, value1.getType());
			assertEquals(2, value1.size());
			assertEquals(BenObject.TYPE_STRI, value1.getBenValue("001").getType());
			assertEquals("abc", value1.getBenValue("001").toString());
			assertEquals(BenObject.TYPE_STRI, value1.getBenValue("abcd").getType());
			assertEquals("abcd", value1.getBenValue("abcd").toString());
		} finally {
			reader.close();
		}
	}

	public void testDecodeError001() throws IOException {
		File dummy = new File("./dummy_002");
		dummy.delete();
		CashKyoroFile base = new CashKyoroFile(new File("./dummy_002"), 512, 2);
		base.addChunk(("d3:001i100e").getBytes());
		MarkableFileReader reader = new MarkableFileReader(base, 512);

		try {
			BenDiction.decodeDiction(reader);
			assertTrue(false);
		} catch(IOException e) {
			assertEquals(0, reader.markSize());			
		} finally {
			reader.close();
		}
	}
	public void testDecodeError002() throws IOException {
		File dummy = new File("./dummy_002");
		dummy.delete();
		CashKyoroFile base = new CashKyoroFile(new File("./dummy_002"), 512, 2);
		base.addChunk(("d3:01i100ee").getBytes());
		MarkableFileReader reader = new MarkableFileReader(base, 512);

		try {
			BenDiction.decodeDiction(reader);
			assertTrue(false);
		} catch(IOException e) {
			assertEquals(0, reader.markSize());			
		} finally {
			reader.close();
		}
	}
}
