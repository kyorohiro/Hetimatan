package info.kyorohiro.raider.net.torrent.client.message;

import info.kyorohiro.helloworld.io.MarkableFileReader;
import info.kyorohiro.helloworld.io.next.KFNextHelper;
import info.kyorohiro.helloworld.io.next.RACashFile;
import info.kyorohiro.raider.util.TestUtil;
import info.kyorohiro.raider.util.io.ByteArrayBuilder;

import java.io.IOException;

import junit.framework.TestCase;

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
		RACashFile output = new RACashFile(512);
		have.encode(output.getLastOutput());
		byte[] target = KFNextHelper.newBinary(output);
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
