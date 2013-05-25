package net.hetimatan.net.torrent.client.message;


import java.io.IOException;
import java.io.OutputStream;

import net.hetimatan.ky.io.MarkableReader;
import net.hetimatan.util.io.ByteArrayBuilder;

public class MessageNotInterested extends TorrentMessage {
	public static final int NOT_INTEREST_LENGTH = 1;

	public MessageNotInterested() {
		super(TorrentMessage.SIGN_NOTINTERESTED);
	}

	@Override
	public void encode(OutputStream output) throws IOException {
		output.write(ByteArrayBuilder.parseInt(NOT_INTEREST_LENGTH, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN));
		output.write(TorrentMessage.SIGN_NOTINTERESTED);
	}

	public static MessageNotInterested decode(MarkableReader reader) throws IOException {
		_length(reader, NOT_INTEREST_LENGTH);
		_signed(reader, TorrentMessage.SIGN_NOTINTERESTED);
		return new MessageNotInterested();
	}
}
