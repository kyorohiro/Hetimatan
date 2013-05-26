package info.kyorohiro.raider.util.bencode;

import java.io.File;
import java.io.IOException;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.filen.RACashFile;
import net.hetimatan.net.torrent.util.bencode.BenObject;
import net.hetimatan.net.torrent.util.bencode.BenString;
import junit.framework.TestCase;

public class TestForBenString extends TestCase {

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

	public void testEncode001() throws IOException {
		byte[] base = "ABC".getBytes("utf8");
		BenString bint = new BenString(base, 0, base.length, "utf8");
		RACashFile output = new RACashFile(512);
		try {
			bint.encode(output.getLastOutput());
			output.seek(0);
			byte[] buffer = new byte[(int)output.length()];
			int len = output.read(buffer);
			String tag = new String(buffer, 0, len);
			assertEquals("3:ABC", tag);
		} finally {
			output.close();
		}
	}

	public void testEncode002() throws IOException {
		byte[] base = "a".getBytes("utf8");
		BenString bint = new BenString(base, 0, base.length, "utf8");
		RACashFile output = new RACashFile(512);
		try {
			bint.encode(output.getLastOutput());
			output.seek(0);
			byte[] buffer = new byte[(int)output.length()];
			int len = output.read(buffer);
			String tag = new String(buffer, 0, len);
			assertEquals("1:a", tag);
		} finally {
			output.close();
		}
	}

	public void testEncode003() throws IOException {
		byte[] base1 = "a".getBytes("utf8");
		BenString bint1 = new BenString(base1, 0, base1.length, "utf8");
		byte[] base2 = "ABCDE".getBytes("utf8");
		BenString bint2 = new BenString(base2, 0, base2.length, "utf8");
		RACashFile output = new RACashFile(512);

		try {
			bint1.encode(output.getLastOutput());
			bint2.encode(output.getLastOutput());
			output.seek(0);
			byte[] buffer = new byte[(int)output.length()];
			int len = output.read(buffer);
			String tag = new String(buffer, 0, len);
			assertEquals("1:a5:ABCDE", tag);
		} finally {
			output.close();
		}
	}

	public void testDecode001() throws IOException {
		RACashFile base = new RACashFile(512);
		base.addChunk(("1:a").getBytes());
		MarkableFileReader reader = new MarkableFileReader(base, 512);

		try {
			BenObject value1 = BenString.decodeString(reader);
			assertEquals(BenObject.TYPE_STRI, value1.getType());
			assertEquals("a", value1.toString());
			assertEquals('a', value1.toByte()[0]);
		} finally {
			reader.close();
		}
	}

	public void testDecode002() throws IOException {
		RACashFile base = new RACashFile(512);
		base.addChunk(("4:abcd3:efg").getBytes());
		MarkableFileReader reader = new MarkableFileReader(base, 512);

		try {
			BenObject value1 = BenString.decodeString(reader);
			assertEquals(BenObject.TYPE_STRI, value1.getType());
			assertEquals("abcd", value1.toString());
			assertEquals('a', value1.toByte()[0]);
			assertEquals('b', value1.toByte()[1]);
			assertEquals('c', value1.toByte()[2]);
			assertEquals('d', value1.toByte()[3]);

			BenObject value2 = BenString.decodeString(reader);
			assertEquals(BenObject.TYPE_STRI, value2.getType());
			assertEquals("efg", value2.toString());
			assertEquals('e', value2.toByte()[0]);
			assertEquals('f', value2.toByte()[1]);
			assertEquals('g', value2.toByte()[2]);
		} finally {
			reader.close();
		}
	}

	public void testDecode003() throws IOException {
		RACashFile base = new RACashFile(512);
		base.addChunk(("10:0123456789").getBytes());
		MarkableFileReader reader = new MarkableFileReader(base, 512);

		try {
			BenObject value1 = BenString.decodeString(reader);
			assertEquals(BenObject.TYPE_STRI, value1.getType());
			assertEquals("0123456789", value1.toString());
			assertEquals('0', value1.toByte()[0]);
			assertEquals('1', value1.toByte()[1]);
			assertEquals('8', value1.toByte()[8]);
			assertEquals('9', value1.toByte()[9]);
		} finally {
			reader.close();
		}
	}

	public void testDecodeError001() throws IOException {
		RACashFile base = new RACashFile(512);
		base.addChunk(("1b:a").getBytes());
		MarkableFileReader reader = new MarkableFileReader(base, 512);

		try {
			BenString.decodeString(reader);
			assertTrue(false);
		} catch(IOException e) {
			assertEquals(0, reader.markSize());
		} finally {
			reader.close();
		}
	}

	public void testDecodeError002() throws IOException {
		RACashFile base = new RACashFile(512);
		base.addChunk(("-1:a").getBytes());
		MarkableFileReader reader = new MarkableFileReader(base, 512);

		try {
			BenString.decodeString(reader);
			assertTrue(false);
		} catch(IOException e) {
			assertEquals(0, reader.markSize());
		} finally {
			reader.close();
		}
	}

	public void testDecodeError003() throws IOException {
		RACashFile base = new RACashFile(512);
		base.addChunk(("2:a").getBytes());
		MarkableFileReader reader = new MarkableFileReader(base, 512);

		try {
			BenString.decodeString(reader);
			assertTrue(false);
		} catch(IOException e) {
			assertEquals(0, reader.markSize());
		} finally {
			reader.close();
		}
	}

}
