package info.kyorohiro.raider.net.torrent.client.message;

import info.kyorohiro.raider.util.TestUtil;
import java.io.IOException;
import junit.framework.TestCase;
import net.hetimatan.io.file.KyoroFile;
import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.filen.CashKyoroFileHelper;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.net.torrent.client.message.MessageBitField;
import net.hetimatan.net.torrent.client.message.MessageCancel;
import net.hetimatan.net.torrent.client.message.MessageChoke;
import net.hetimatan.net.torrent.client.message.MessagePiece;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.util.io.ByteArrayBuilder;

import java.io.IOException;

import junit.framework.TestCase;

public class TestForMessagePiece extends TestCase {

	public void testHello() {
		assertEquals(6, TorrentMessage.SIGN_REQUEST);
	}

	public void testEncode() throws IOException {
//		piece: <len=0009+X><id=7><index><begin><block>
		byte[] bcont = {(byte)13,(byte)14,(byte)15};
		KyoroFile cont = new  CashKyoroFile(bcont);
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.appendInt(9+bcont.length);
		builder.append(TorrentMessage.SIGN_PIECE);
		builder.appendInt(10);
		builder.appendInt(100);
		builder.append(bcont);

		byte[] expected = builder.createBuffer();

		MessagePiece request = new MessagePiece(10, 100, cont);
		CashKyoroFile output = new CashKyoroFile(512);
		request.encode(output.getLastOutput());
		byte[] target = CashKyoroFileHelper.newBinary(output);
		TestUtil.assertArrayEquals(this, "", expected, target);
	}

	public void testDecode() throws IOException {
		byte[] bcont = {(byte)13,(byte)14,(byte)15};
		CashKyoroFile cont = new  CashKyoroFile(bcont);
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
