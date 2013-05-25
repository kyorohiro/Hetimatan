package info.kyorohiro.raider.net.torrent.client.message;

import info.kyorohiro.helloworld.io.MarkableFileReader;
import info.kyorohiro.helloworld.io.next.KFNextHelper;
import info.kyorohiro.helloworld.io.next.RACashFile;
import info.kyorohiro.raider.util.TestUtil;
import info.kyorohiro.raider.util.io.ByteArrayBuilder;

import java.io.IOException;

import junit.framework.TestCase;

public class TestForMessageNotInterested extends TestCase {

	public void hello() {
		;
	}

	public void testEncode() throws IOException {
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.appendInt(1);
		builder.append(MessageBitField.SIGN_INTERESTED);
		byte[] expected = builder.createBuffer();

		MessageInterested interest = new MessageInterested();
		RACashFile output = new RACashFile(512);
		interest.encode(output.getLastOutput());
		byte[] target = KFNextHelper.newBinary(output);
		TestUtil.assertArrayEquals(this, "", expected, target);
	}

	public void testDecode() throws IOException {
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.appendInt(1);
		builder.append(TorrentMessage.SIGN_INTERESTED);

		MarkableFileReader reader = new MarkableFileReader(builder.createBuffer());
		MessageInterested.decode(reader);
	}

}
