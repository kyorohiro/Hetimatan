package net.hetimatan.net.torrent.client.message;

import java.io.IOException;

import junit.framework.TestCase;
import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.filen.CashKyoroFileHelper;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.net.torrent.client.message.MessageBitField;
import net.hetimatan.net.torrent.client.message.MessageCancel;
import net.hetimatan.net.torrent.client.message.MessageChoke;
import net.hetimatan.net.torrent.client.message.MessageRequest;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.util.io.ByteArrayBuilder;
import net.hetimatan.util.test.TestUtil;

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
		CashKyoroFile output = new CashKyoroFile(512);
		request.encode(output.getLastOutput());
		byte[] target = CashKyoroFileHelper.newBinary(output);
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
