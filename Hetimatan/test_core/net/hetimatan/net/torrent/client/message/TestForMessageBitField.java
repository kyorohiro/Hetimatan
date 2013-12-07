package net.hetimatan.net.torrent.client.message;

import java.io.IOException;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.filen.CashKyoroFileHelper;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.net.torrent.client.message.MessageBitField;
import net.hetimatan.util.io.ByteArrayBuilder;
import net.hetimatan.util.test.TestUtil;
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
		CashKyoroFile output = new CashKyoroFile(512);
		bitfield.encode(output.getLastOutput());
		byte[] target = CashKyoroFileHelper.newBinary(output);
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
			CashKyoroFile output = new CashKyoroFile(512);
			bitfield.encode(output.getLastOutput());
			byte[] target = CashKyoroFileHelper.newBinary(output);
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
			CashKyoroFile output = new CashKyoroFile(512);
			bitfield.encode(output.getLastOutput());
			byte[] target = CashKyoroFileHelper.newBinary(output);
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
