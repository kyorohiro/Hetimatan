package net.hetimatan.net.torrent.client.message;


import java.io.IOException;
import java.io.OutputStream;

import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.util.io.ByteArrayBuilder;

//request: <len=0013><id=6><index><begin><length>
public class MessageRequest extends TorrentMessage {

	public static final String TAG = "request";
	public static final int REQUEST_LENGTH = 1+4*3;
	private int mIndex = 0;
	private int mBegin = 0;
	private int mLength = 0;

	public MessageRequest(int index, int begin, int length) {
		super(TorrentMessage.SIGN_REQUEST);
		mIndex = index;
		mBegin = begin;
		mLength = length;
	}

	public String toString() {
		return TAG+":"+mIndex+","+mBegin+","+mLength;
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
		output.write(ByteArrayBuilder.parseInt(REQUEST_LENGTH, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN));
		output.write(TorrentMessage.SIGN_REQUEST);

		output.write(ByteArrayBuilder.parseInt(mIndex, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN));
		output.write(ByteArrayBuilder.parseInt(mBegin, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN));
		output.write(ByteArrayBuilder.parseInt(mLength, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN));
	}

	public static MessageRequest decode(MarkableReader reader) throws IOException {
		int len = _length(reader,REQUEST_LENGTH);
		_signed(reader, TorrentMessage.SIGN_REQUEST);
		int index = _decodeInt(reader);
		int begin = _decodeInt(reader);
		int length = _decodeInt(reader);
		return new MessageRequest(index, begin, length);
	}
	
}
