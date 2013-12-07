package net.hetimatan.net.torrent.client.message;

import java.io.IOException;

import junit.framework.TestCase;
import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.filen.CashKyoroFileHelper;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.net.torrent.client.message.MessageBitField;
import net.hetimatan.net.torrent.client.message.MessageCancel;
import net.hetimatan.net.torrent.client.message.MessageChoke;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.util.io.ByteArrayBuilder;
import net.hetimatan.util.test.TestUtil;


public class TestForMessageChoke extends TestCase {

	public void hello() {
		;
	}

	public void testEncode() throws IOException {
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.appendInt(1);
		builder.append(MessageBitField.SIGN_CHOKE);
		byte[] expected = builder.createBuffer();

		MessageChoke choke = new MessageChoke();
		CashKyoroFile output = new CashKyoroFile(512);
		choke.encode(output.getLastOutput());
		byte[] target = CashKyoroFileHelper.newBinary(output);
		TestUtil.assertArrayEquals(this, "", expected, target);
	}

	public void testDecode() throws IOException {
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.appendInt(1);
		builder.append(TorrentMessage.SIGN_CHOKE);

		MarkableFileReader reader = new MarkableFileReader(builder.createBuffer());
		MessageChoke.decode(reader);
	}

}
