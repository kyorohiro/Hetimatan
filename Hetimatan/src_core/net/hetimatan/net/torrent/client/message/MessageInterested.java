package net.hetimatan.net.torrent.client.message;


import java.io.IOException;
import java.io.OutputStream;

import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.util.io.ByteArrayBuilder;

public class MessageInterested extends TorrentMessage {
	public static final int INTEREST_LENGTH = 1;
	public static final String TAG = "interested";
	
	public MessageInterested() {
		super(TorrentMessage.SIGN_INTERESTED);
	}

	@Override
	public String toString() {
		return TAG+":";
	}

	@Override
	public void encode(OutputStream output) throws IOException {
		output.write(ByteArrayBuilder.parseInt(INTEREST_LENGTH, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN));
		output.write(TorrentMessage.SIGN_INTERESTED);
	}

	public static MessageInterested decode(MarkableReader reader) throws IOException {
		_length(reader, INTEREST_LENGTH);
		_signed(reader, TorrentMessage.SIGN_INTERESTED);
		return new MessageInterested();
	}
}
