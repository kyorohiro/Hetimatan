package info.kyorohiro.raider.net.torrent.client.message;

import info.kyorohiro.helloworld.io.MarkableFileReader;
import info.kyorohiro.helloworld.io.next.KFNextHelper;
import info.kyorohiro.helloworld.io.next.RACashFile;
import info.kyorohiro.raider.util.TestUtil;
import info.kyorohiro.raider.util.io.ByteArrayBuilder;

import java.io.IOException;

import junit.framework.TestCase;

public class TestForMessageCancel extends TestCase {

	public void testHello() {
		assertEquals(8, TorrentMessage.SIGN_CANCEL);
	}

	public void testEncode() throws IOException {
//		cancel: <len=0013><id=8><index><begin><length>
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.appendInt(13);
		builder.append(TorrentMessage.SIGN_CANCEL);
		builder.appendInt(10);
		builder.appendInt(100);
		builder.appendInt(10300);

		byte[] expected = builder.createBuffer();

		MessageCancel cancel = new MessageCancel(10, 100, 10300);
		RACashFile output = new RACashFile(512);
		cancel.encode(output.getLastOutput());
		byte[] target = KFNextHelper.newBinary(output);
		TestUtil.assertArrayEquals(this, "", expected, target);
	}

	public void testDecode() throws IOException {
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.appendInt(13);
		builder.append(TorrentMessage.SIGN_CANCEL);
		builder.appendInt(10);
		builder.appendInt(100);
		builder.appendInt(10300);

		MarkableFileReader reader = new MarkableFileReader(builder.createBuffer());
		MessageCancel cancel = MessageCancel.decode(reader);
		assertEquals(10, cancel.getIndex());
		assertEquals(100, cancel.getBegin());
		assertEquals(10300, cancel.getLength());
	}

}
