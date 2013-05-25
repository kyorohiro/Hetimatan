package info.kyorohiro.raider.net.torrent.client.message;

import info.kyorohiro.helloworld.io.MarkableFileReader;
import info.kyorohiro.helloworld.io.next.KFNextHelper;
import info.kyorohiro.helloworld.io.next.RACashFile;
import info.kyorohiro.raider.util.TestUtil;
import info.kyorohiro.raider.util.io.ByteArrayBuilder;

import java.io.IOException;

import junit.framework.TestCase;

public class TestForMessageRequest extends TestCase {

	public void testHello() {
		assertEquals(6, TorrentMessage.SIGN_REQUEST);
	}

	public void testEncode() throws IOException {
//		request: <len=0013><id=6><index><begin><length>
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.appendInt(13);
		builder.append(TorrentMessage.SIGN_REQUEST);
		builder.appendInt(10);
		builder.appendInt(100);
		builder.appendInt(10300);

		byte[] expected = builder.createBuffer();

		MessageRequest request = new MessageRequest(10, 100, 10300);
		RACashFile output = new RACashFile(512);
		request.encode(output.getLastOutput());
		byte[] target = KFNextHelper.newBinary(output);
		TestUtil.assertArrayEquals(this, "", expected, target);
	}

	public void testDecode() throws IOException {
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.appendInt(13);
		builder.append(TorrentMessage.SIGN_REQUEST);
		builder.appendInt(10);
		builder.appendInt(100);
		builder.appendInt(10300);

		MarkableFileReader reader = new MarkableFileReader(builder.createBuffer());
		MessageRequest request = MessageRequest.decode(reader);
		assertEquals(10, request.getIndex());
		assertEquals(100, request.getBegin());
		assertEquals(10300, request.getLength());
	}

}
