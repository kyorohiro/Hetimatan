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
import net.hetimatan.net.torrent.client.message.MessageNotInterested;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.util.io.ByteArrayBuilder;
import junit.framework.TestCase;

public class TestForMessageInterested extends TestCase {

	public void hello() {
		;
	}

	public void testEncode() throws IOException {
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.appendInt(1);
		builder.append(MessageBitField.SIGN_NOTINTERESTED);
		byte[] expected = builder.createBuffer();

		MessageNotInterested interest = new MessageNotInterested();
		CashKyoroFile output = new CashKyoroFile(512);
		interest.encode(output.getLastOutput());
		byte[] target = CashKyoroFileHelper.newBinary(output);
		TestUtil.assertArrayEquals(this, "", expected, target);
	}

	public void testDecode() throws IOException {
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.appendInt(1);
		builder.append(TorrentMessage.SIGN_NOTINTERESTED);

		MarkableFileReader reader = new MarkableFileReader(builder.createBuffer());
		MessageNotInterested.decode(reader);
	}

}
