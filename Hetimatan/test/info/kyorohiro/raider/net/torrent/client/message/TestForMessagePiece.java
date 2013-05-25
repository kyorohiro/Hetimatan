package info.kyorohiro.raider.net.torrent.client.message;

import info.kyorohiro.helloworld.io.KyoroFile;
import info.kyorohiro.helloworld.io.MarkableFileReader;
import info.kyorohiro.helloworld.io.next.KFNextHelper;
import info.kyorohiro.helloworld.io.next.RACashFile;
import info.kyorohiro.raider.util.TestUtil;
import info.kyorohiro.raider.util.io.ByteArrayBuilder;

import java.io.IOException;

import junit.framework.TestCase;

public class TestForMessagePiece extends TestCase {

	public void testHello() {
		assertEquals(6, TorrentMessage.SIGN_REQUEST);
	}

	public void testEncode() throws IOException {
//		piece: <len=0009+X><id=7><index><begin><block>
		byte[] bcont = {(byte)13,(byte)14,(byte)15};
		KyoroFile cont = new  RACashFile(bcont);
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.appendInt(9+bcont.length);
		builder.append(TorrentMessage.SIGN_PIECE);
		builder.appendInt(10);
		builder.appendInt(100);
		builder.append(bcont);

		byte[] expected = builder.createBuffer();

		MessagePiece request = new MessagePiece(10, 100, cont);
		RACashFile output = new RACashFile(512);
		request.encode(output.getLastOutput());
		byte[] target = KFNextHelper.newBinary(output);
		TestUtil.assertArrayEquals(this, "", expected, target);
	}

	public void testDecode() throws IOException {
		byte[] bcont = {(byte)13,(byte)14,(byte)15};
		RACashFile cont = new  RACashFile(bcont);
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.appendInt(9+bcont.length);
		builder.append(TorrentMessage.SIGN_PIECE);
		builder.appendInt(10);
		builder.appendInt(100);
		builder.append(bcont);

		MarkableFileReader reader = new MarkableFileReader(builder.createBuffer());
		MessagePiece request = MessagePiece.decode(reader);
		assertEquals(10, request.getIndex());
		assertEquals(100, request.getBegin());
		TestUtil.assertArrayEquals(this, "", bcont, 
				TestUtil.createBuffer(request.getCotent()));
	}

}
