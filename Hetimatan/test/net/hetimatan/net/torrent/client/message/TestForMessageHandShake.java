package net.hetimatan.net.torrent.client.message;

import java.io.IOException;

import junit.framework.TestCase;
import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.filen.CashKyoroFileHelper;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.net.torrent.client.message.MessageHandShake;
import net.hetimatan.util.io.ByteArrayBuilder;
import net.hetimatan.util.test.TestUtil;


public class TestForMessageHandShake extends TestCase {

	public void testHello() {
		assertEquals("BitTorrent protocol", MessageHandShake.PROTOCOL_ID);
	}

	public void testDecode() throws IOException {
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.append((byte)19);
		builder.append(MessageHandShake.PROTOCOL_ID.getBytes());
		builder.append(MessageHandShake.RESERVED);
		builder.append("123456789A123456789B".getBytes());
		builder.append("123456789C123456789D".getBytes());
		
		MarkableFileReader reader = new MarkableFileReader(builder.createBuffer());
		MessageHandShake handshake = MessageHandShake.decode(reader);

		TestUtil.assertArrayEquals(this, "infohash", "123456789A123456789B".getBytes(), handshake.getInfoHash());
		TestUtil.assertArrayEquals(this, "peerid", "123456789C123456789D".getBytes(), handshake.getPeerId());
		TestUtil.assertArrayEquals(this, "protocolid", MessageHandShake.PROTOCOL_ID.getBytes(), handshake.getProtocolId());
	}

	public void testEncode() throws IOException {
		MessageHandShake message = new MessageHandShake("123456789A123456789B".getBytes(), "123456789C123456789D".getBytes());
		
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.append((byte)19);
		builder.append(MessageHandShake.PROTOCOL_ID.getBytes());
		builder.append(MessageHandShake.RESERVED);
		builder.append("123456789A123456789B".getBytes());
		builder.append("123456789C123456789D".getBytes());
		byte[] expected = builder.createBuffer();

		CashKyoroFile output = new CashKyoroFile(512);
		message.encode(output.getLastOutput());
		output.syncWrite();
		TestUtil.assertArrayEquals(this, "", expected, CashKyoroFileHelper.newBinary(output));
		output.close();

	}

}
