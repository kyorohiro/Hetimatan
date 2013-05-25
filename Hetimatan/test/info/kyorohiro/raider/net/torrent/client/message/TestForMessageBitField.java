package info.kyorohiro.raider.net.torrent.client.message;

import info.kyorohiro.helloworld.io.MarkableFileReader;
import info.kyorohiro.helloworld.io.next.KFNextHelper;
import info.kyorohiro.helloworld.io.next.RACashFile;
import info.kyorohiro.raider.util.TestUtil;
import info.kyorohiro.raider.util.io.ByteArrayBuilder;

import java.io.IOException;

import junit.framework.TestCase;

public class TestForMessageBitField extends TestCase {

	public void hello() {
		;
	}

	public void testEncode() throws IOException {
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.appendInt((1+1));
		builder.append(MessageBitField.SIGN_BITFIELD);
		builder.append((byte)(0xFF&0xE0));
		byte[] expected = builder.createBuffer();

		MessageBitField bitfield = new MessageBitField(3);
		RACashFile output = new RACashFile(512);
		bitfield.encode(output.getLastOutput());
		byte[] target = KFNextHelper.newBinary(output);
		TestUtil.assertArrayEquals(this, "", expected, target);
	}

	public void testDecode() throws IOException {
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.appendInt((1+1));
		builder.append(MessageBitField.SIGN_BITFIELD);
		builder.append((byte)(0xFF&0xE0));

		MarkableFileReader reader = new MarkableFileReader(builder.createBuffer());
		MessageBitField bitfield = MessageBitField.decode(reader);

		assertEquals(true, bitfield.isOn(0));
		assertEquals(true, bitfield.isOn(1));
		assertEquals(true, bitfield.isOn(2));
		assertEquals(false, bitfield.isOn(3));
		assertEquals(false, bitfield.isOn(4));
		assertEquals(false, bitfield.isOn(5));
		assertEquals(false, bitfield.isOn(6));
		assertEquals(false, bitfield.isOn(7));
	}

	public void testBitfield() throws IOException {
		{
			ByteArrayBuilder builder = new ByteArrayBuilder();
			builder.appendInt((1+1));
			builder.append(MessageBitField.SIGN_BITFIELD);
			builder.append((byte)(0xFF));
			byte[] expected = builder.createBuffer();

			MessageBitField bitfield = new MessageBitField(8);
			RACashFile output = new RACashFile(512);
			bitfield.encode(output.getLastOutput());
			byte[] target = KFNextHelper.newBinary(output);
			TestUtil.assertArrayEquals(this, "", expected, target);
		}

		{
			ByteArrayBuilder builder = new ByteArrayBuilder();
			builder.appendInt((1+2));
			builder.append(MessageBitField.SIGN_BITFIELD);
			builder.append((byte)(0xFF&0xFF));
			builder.append((byte)(0xFF&0x80));
			byte[] expected = builder.createBuffer();

			MessageBitField bitfield = new MessageBitField(9);
			RACashFile output = new RACashFile(512);
			bitfield.encode(output.getLastOutput());
			byte[] target = KFNextHelper.newBinary(output);
			TestUtil.assertArrayEquals(this, "", expected, target);
		}
	}

	public void testBitfield_onoff() throws IOException {
		MessageBitField bitfield = new MessageBitField(13);
		bitfield.isOn(0, false);
		assertEquals(false, bitfield.isOn(0));
		bitfield.isOn(2, false);
		assertEquals(false, bitfield.isOn(2));
		bitfield.isOn(4, false);
		assertEquals(false, bitfield.isOn(4));
		bitfield.isOn(8, false);
		assertEquals(false, bitfield.isOn(8));
		bitfield.isOn(13, false);
		assertEquals(false, bitfield.isOn(13));

		bitfield.isOn(0, true);
		assertEquals(true, bitfield.isOn(0));
		bitfield.isOn(4, true);
		assertEquals(true, bitfield.isOn(4));
		bitfield.isOn(11, true);
		assertEquals(true, bitfield.isOn(11));

		assertEquals(true, bitfield.isOn(0));
		assertEquals(true, bitfield.isOn(1));
		assertEquals(false, bitfield.isOn(2));
		assertEquals(true, bitfield.isOn(3));
		assertEquals(true, bitfield.isOn(4));
		assertEquals(true, bitfield.isOn(5));
		assertEquals(true, bitfield.isOn(6));
		assertEquals(true, bitfield.isOn(7));
		assertEquals(false, bitfield.isOn(8));
		assertEquals(true, bitfield.isOn(9));
		assertEquals(true, bitfield.isOn(10));
		assertEquals(true, bitfield.isOn(11));
		assertEquals(true, bitfield.isOn(12));
		assertEquals(false, bitfield.isOn(13));

		
	}
}
