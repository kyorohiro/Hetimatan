package net.hetimatan.net.torrent.client.message;

import info.kyorohiro.raider.util.TestUtil;
import java.io.IOException;
import junit.framework.TestCase;
import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.filen.CashKyoroFileHelper;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.net.torrent.client.message.MessagePort;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.util.http.HttpObject;
import net.hetimatan.util.io.ByteArrayBuilder;

//have: <len=0003><id=9><port number>
public class TestForMessagePort extends TestCase {

	public void hello() {
		;
	}

	public void testEncode() throws IOException {
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.appendInt(3);
		builder.append(TorrentMessage.SIGN_PORT);
		builder.append(HttpObject.portToB(5));
		byte[] expected = builder.createBuffer();

		MessagePort have = new MessagePort(5);
		CashKyoroFile output = new CashKyoroFile(512);
		have.encode(output.getLastOutput());
		byte[] target = CashKyoroFileHelper.newBinary(output);
		TestUtil.assertArrayEquals(this, "", expected, target);
	}

	public void testDecode() throws IOException {
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.appendInt(3);
		builder.append(TorrentMessage.SIGN_PORT);
		builder.append(HttpObject.portToB(5));

		MarkableFileReader reader = new MarkableFileReader(builder.createBuffer());
		MessagePort have = MessagePort.decode(reader);
		assertEquals(5, have.getPort());
	}

}
