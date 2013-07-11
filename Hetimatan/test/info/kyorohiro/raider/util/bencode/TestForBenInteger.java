package info.kyorohiro.raider.util.bencode;

import java.io.File;
import java.io.IOException;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.net.torrent.util.bencode.BenInteger;
import net.hetimatan.net.torrent.util.bencode.BenObject;
import junit.framework.TestCase;

public class TestForBenInteger extends TestCase {

	@Override
	protected void setUp() throws Exception {
//		System.out.println("###setup()###");
		File dummy = new File("./dummy_003");
		if(dummy.exists()) {
			dummy.delete();
		}
		super.setUp();
	}

	public void testHello() {

	}

	public void testEncode001() throws IOException {
		BenInteger bint = new BenInteger(0);
		CashKyoroFile output = new CashKyoroFile(512);
		try {
			bint.encode(output.getLastOutput());
			output.seek(0);
			byte[] buffer = new byte[(int)output.length()];
			int len = output.read(buffer);
			String tag = new String(buffer, 0, len);
			assertEquals("i0e", tag);
		} finally {
			output.close();
		}
	}

	public void testEncode002() throws IOException {
		BenInteger bint = new BenInteger(-1);
		CashKyoroFile output = new CashKyoroFile(512);
		try {
			bint.encode(output.getLastOutput());
			output.seek(0);
			byte[] buffer = new byte[(int)output.length()];
			int len = output.read(buffer);
			String tag = new String(buffer, 0, len);
			assertEquals("i-1e", tag);
		} finally {
			output.close();
		}
	}

	public void testEncode003() throws IOException {
		BenInteger bint = new BenInteger(Integer.MAX_VALUE);
		CashKyoroFile output = new CashKyoroFile(512);
		try {
			bint.encode(output.getLastOutput());
			output.seek(0);
			byte[] buffer = new byte[(int)output.length()];
			int len = output.read(buffer);
			String tag = new String(buffer, 0, len);
			assertEquals("i"+Integer.MAX_VALUE+"e", tag);
		} finally {
			output.close();
		}
	}

	public void testDecode001() throws IOException {
		CashKyoroFile base = new CashKyoroFile(512);
		base.addChunk("i0e".getBytes());
		MarkableFileReader reader = new MarkableFileReader(base, 512);

		try {
			BenObject value = BenInteger.decodeInteger(reader);
			assertEquals(BenObject.TYPE_INTE, value.getType());
			assertEquals(0, value.toInteger());
		} finally {
			reader.close();
		}
	}

	public void testDecode002() throws IOException {
		CashKyoroFile base = new CashKyoroFile(512);
		base.addChunk("i0ei1e".getBytes());
		MarkableFileReader reader = new MarkableFileReader(base, 512);

		try {
			BenObject value1 = BenInteger.decodeInteger(reader);
			assertEquals(BenObject.TYPE_INTE, value1.getType());
			assertEquals(0, value1.toInteger());
			BenObject value2 = BenInteger.decodeInteger(reader);
			assertEquals(BenObject.TYPE_INTE, value2.getType());
			assertEquals(1, value2.toInteger());
		} finally {
			reader.close();
		}

	}

	public void testDecode003() throws IOException {
		CashKyoroFile base = new CashKyoroFile(512);
		base.addChunk(("i-1ei"+Integer.MAX_VALUE+"e").getBytes());
		MarkableFileReader reader = new MarkableFileReader(base, 512);

		try {
			BenObject value1 = BenInteger.decodeInteger(reader);
			assertEquals(BenObject.TYPE_INTE, value1.getType());
			assertEquals(-1, value1.toInteger());
			BenObject value2 = BenInteger.decodeInteger(reader);
			assertEquals(BenObject.TYPE_INTE, value2.getType());
			assertEquals(Integer.MAX_VALUE, value2.toInteger());
		} finally {
			reader.close();
		}
	}

	public void testDecodeError001() throws IOException {
		CashKyoroFile base = new CashKyoroFile(512);
		base.addChunk("i0ae".getBytes());
		MarkableFileReader reader = new MarkableFileReader(base, 512);

		try {
			BenInteger.decodeInteger(reader);
			assertTrue(false);
		} catch(IOException e) {
			assertEquals(0, reader.markSize());
		} finally {
			reader.close();
		}
	}
}
