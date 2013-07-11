package info.kyorohiro.raider.net.torrent.client.message;

import info.kyorohiro.raider.util.TestUtil;
import java.io.IOException;
import junit.framework.TestCase;
import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.filen.KFNextHelper;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.net.torrent.client.message.MessageBitField;
import net.hetimatan.net.torrent.client.message.MessageCancel;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.util.io.ByteArrayBuilder;

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
		CashKyoroFile output = new CashKyoroFile(512);
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
