package net.hetimatan.net.torrent.client.message;


import java.io.IOException;
import java.io.OutputStream;

import net.hetimatan.ky.io.MarkableReader;
import net.hetimatan.util.bitfield.BitField;
import net.hetimatan.util.io.ByteArrayBuilder;
import net.hetimatan.util.url.PercentEncoder;

public class MessageBitField extends TorrentMessage {
	private BitField mBitfield = null;

	public MessageBitField(BitField bitfield) {
		super(TorrentMessage.SIGN_BITFIELD);
		mBitfield = bitfield;
	}

	public MessageBitField(int bitsize) {
		super(TorrentMessage.SIGN_BITFIELD);
		mBitfield = new BitField(bitsize);
	}

	public BitField getBitField() {
		return mBitfield;
	}

	private void setBitfield(byte[] bitfield) {
		mBitfield.setBitfield(bitfield);
	}

	public void isOn(int number, boolean on) {
		mBitfield.isOn(number, on);
	}

	public boolean isOn(int number) {
		return mBitfield.isOn(number);
	}

	public void encode(OutputStream output) throws IOException {
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.appendInt(SIGN_LENGTH + mBitfield.lengthPerByte());
		builder.append(SIGN_BITFIELD);
		byte[] bb = new byte[mBitfield.getBinary().length];		
		builder.append(mBitfield.getBinary());
//		builder.append(bb);
		{
			PercentEncoder en = new PercentEncoder();
			String s =en.encode(builder.getBuffer(), 0, (int)builder.length());
			System.out.println(s);
		}
		//
		output.write(builder.getBuffer(), 0, builder.length());
//		output.write(bb, 0, builder.length());
    }

	public static MessageBitField decode(MarkableReader reader) throws IOException {
		int length = _decodeInt(reader);
		_signed(reader);
		byte[] bitfield = _bitfield(reader, length-1);
		MessageBitField field = new MessageBitField(length*8);//bitsize todo: 
		field.setBitfield(bitfield);
		return field;
	}


	public static byte _signed(MarkableReader reader) throws IOException {
		return _signed(reader, TorrentMessage.SIGN_BITFIELD);
	}

	public static byte[] _bitfield(MarkableReader reader, int length) throws IOException {
		byte[] field = _value(reader, length);
		return field;
	}

}
