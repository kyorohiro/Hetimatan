package net.hetimatan.net.torrent.client.message;

import info.kyorohiro.raider.util.TestUtil;
import java.io.IOException;
import junit.framework.TestCase;
import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.filen.CashKyoroFileHelper;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.net.torrent.client.message.MessageBitField;
import net.hetimatan.net.torrent.client.message.MessageCancel;
import net.hetimatan.net.torrent.client.message.MessageChoke;
import net.hetimatan.net.torrent.client.message.MessageHave;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.util.io.ByteArrayBuilder;

//have: <len=0005><id=4><piece index>
public class TestForMessageHave extends TestCase {

	public void hello() {
		;
	}

	public void testEncode() throws IOException {
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.appendInt(5);
		builder.append(TorrentMessage.SIGN_HAVE);
		builder.appendInt(5);
		byte[] expected = builder.createBuffer();

		MessageHave have = new MessageHave(5);
		CashKyoroFile output = new CashKyoroFile(512);
		have.encode(output.getLastOutput());
		byte[] target = CashKyoroFileHelper.newBinary(output);
		TestUtil.assertArrayEquals(this, "", expected, target);
	}

	public void testDecode() throws IOException {
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.appendInt(5);
		builder.append(TorrentMessage.SIGN_HAVE);
		builder.appendInt(5);

		MarkableFileReader reader = new MarkableFileReader(builder.createBuffer());
		MessageHave have = MessageHave.decode(reader);
		assertEquals(5, have.getIndex());
	}

}
