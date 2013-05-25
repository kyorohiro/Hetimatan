package net.hetimatan.net.torrent.client.message;


import java.io.IOException;
import java.io.OutputStream;

import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.util.io.ByteArrayBuilder;

//cancel: <len=0013><id=8><index><begin><length>
public class MessageCancel extends TorrentMessage {

	public static final int CANCEL_LENGTH = 1+4*3;
	private int mIndex = 0;
	private int mBegin = 0;
	private int mLength = 0;

	public MessageCancel(int index, int begin, int length) {
		super(TorrentMessage.SIGN_CANCEL);
		mIndex  = index;
		mBegin  = begin;
		mLength = length;
	}

	public int getIndex() {
		return mIndex;
	}

	public int getBegin() {
		return mBegin;
	}

	public int getLength() {
		return mLength;
	}

	@Override
	public void encode(OutputStream output) throws IOException {
		output.write(ByteArrayBuilder.parseInt(CANCEL_LENGTH, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN));
		output.write(TorrentMessage.SIGN_CANCEL);
		output.write(ByteArrayBuilder.parseInt(mIndex, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN));
		output.write(ByteArrayBuilder.parseInt(mBegin, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN));
		output.write(ByteArrayBuilder.parseInt(mLength, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN));
	}

	public static MessageCancel decode(MarkableReader reader) throws IOException {
		int len = _length(reader,CANCEL_LENGTH);
		_signed(reader, TorrentMessage.SIGN_CANCEL);
		int index = _decodeInt(reader);
		int begin = _decodeInt(reader);
		int length = _decodeInt(reader);
		return new MessageCancel(index, begin, length);
	}
	
}
