package info.kyorohiro.raider.util.bencode;

import java.io.File;
import java.io.IOException;

import info.kyorohiro.helloworld.io.MarkableFileReader;
import info.kyorohiro.helloworld.io.next.RACashFile;
import info.kyorohiro.raider.util.bencode.BenInteger;
import info.kyorohiro.raider.util.bencode.BenList;
import info.kyorohiro.raider.util.bencode.BenObject;
import info.kyorohiro.raider.util.bencode.BenString;
import junit.framework.TestCase;

public class TestForBenList extends TestCase {

	@Override
	protected void setUp() throws Exception {
//		System.out.println("###setup()###");
		File dummy = new File("./dummy_001");
		if(dummy.exists()) {
			dummy.delete();
		}
		super.setUp();
	}

	public void testHello() {

	}

	public void testEncode001() throws IOException {
		BenList blist = new BenList();
		blist.append(new BenInteger(1));
		RACashFile output = new RACashFile(512);
		try {
			blist.encode(output.getLastOutput());
			output.seek(0);
			byte[] buffer = new byte[(int)output.length()];
			int len = output.read(buffer);
			String tag = new String(buffer, 0, len);
			assertEquals("li1ee", tag);
		} finally {
			output.close();
		}
	}

	public void testEncode002() throws IOException {
		BenList blist = new BenList();
		String base = "002";
		byte[] baseBuffer = base.getBytes("utf8");
		blist.append(new BenString(baseBuffer, 0, baseBuffer.length, "utf8"));
		RACashFile output = new RACashFile(512);
		try {
			blist.encode(output.getLastOutput());
			output.seek(0);
			byte[] buffer = new byte[(int)output.length()];
			int len = output.read(buffer);
			String tag = new String(buffer, 0, len);
			assertEquals("l3:002e", tag);
		} finally {
			output.close();
		}
	}

	public void testDecode001() throws IOException {
		RACashFile base = new RACashFile(512);
		base.addChunk(("l3:002e").getBytes());
		MarkableFileReader reader = new MarkableFileReader(base, 512);

		try {
			BenObject value1 = BenList.decodeList(reader);
			assertEquals(BenObject.TYPE_LIST, value1.getType());
			assertEquals(1, value1.size());
			assertEquals(BenObject.TYPE_STRI, value1.getBenValue(0).getType());
			assertEquals("002", value1.getBenValue(0).toString());
		} finally {
			reader.close();
		}
	}

	public void testDecode002() throws IOException {
		RACashFile base = new RACashFile(512);
		base.addChunk(("li100ee").getBytes());
		MarkableFileReader reader = new MarkableFileReader(base, 512);

		try {
			BenObject value1 = BenList.decodeList(reader);
			assertEquals(BenObject.TYPE_LIST, value1.getType());
			assertEquals(1, value1.size());
			assertEquals(BenObject.TYPE_INTE, value1.getBenValue(0).getType());
			assertEquals(100, value1.getBenValue(0).toInteger());
		} finally {
			reader.close();
		}
	}

	public void testDecode003() throws IOException {
		RACashFile base = new RACashFile(512);
		base.addChunk(("li100ei200e2:abe").getBytes());
		MarkableFileReader reader = new MarkableFileReader(base, 512);

		try {
			BenObject value1 = BenList.decodeList(reader);
			assertEquals(BenObject.TYPE_LIST, value1.getType());
			assertEquals(3, value1.size());
			assertEquals(BenObject.TYPE_INTE, value1.getBenValue(0).getType());
			assertEquals(100, value1.getBenValue(0).toInteger());
			assertEquals(BenObject.TYPE_INTE, value1.getBenValue(1).getType());
			assertEquals(200, value1.getBenValue(1).toInteger());
			assertEquals(BenObject.TYPE_STRI, value1.getBenValue(2).getType());
			assertEquals("ab", value1.getBenValue(2).toString());
		} finally {
			reader.close();
		}
	}

	public void testDecode004() throws IOException {
		RACashFile base = new RACashFile(512);
		base.addChunk(("lli100eee").getBytes());
		MarkableFileReader reader = new MarkableFileReader(base, 512);

		try {
			BenObject value1 = BenList.decodeList(reader);
			assertEquals(BenObject.TYPE_LIST, value1.getType());
			assertEquals(1, value1.size());
			assertEquals(BenObject.TYPE_LIST, value1.getBenValue(0).getType());
			assertEquals(1, value1.getBenValue(0).size());
			assertEquals(BenObject.TYPE_INTE, value1.getBenValue(0).getBenValue(0).getType());
			assertEquals(100, value1.getBenValue(0).getBenValue(0).toInteger());
		} finally {
			reader.close();
		}
	}

	public void testDecodeError001() throws IOException {
		RACashFile base = new RACashFile(512);
		base.addChunk(("l-1:002e").getBytes());
		MarkableFileReader reader = new MarkableFileReader(base, 512);

		try {
			BenList.decodeList(reader);
			assertTrue(false);
		} catch(IOException e) {
			assertEquals(0, reader.markSize());			
		} finally {
			base.close();
		}
	}

	public void testDecodeError002() throws IOException {
		RACashFile base = new RACashFile(512);
		base.addChunk(("l3:002").getBytes());
		MarkableFileReader reader = new MarkableFileReader(base, 512);

		try {
			BenList.decodeList(reader);
			assertTrue(false);
		} catch(IOException e) {
			assertEquals(0, reader.markSize());			
		} finally {
			reader.close();
		}
	}
}
