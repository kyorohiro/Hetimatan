package net.hetimatan.net.torrent.client.message;


import java.io.IOException;
import java.io.OutputStream;

import net.hetimatan.ky.io.MarkableReader;
import net.hetimatan.util.io.ByteArrayBuilder;

public class MessageUnchoke extends TorrentMessage {
	public static final int UNCHOKE_LENGTH = 1;

	public MessageUnchoke() {
		super(TorrentMessage.SIGN_UNCHOKE);
	}

	@Override
	public void encode(OutputStream output) throws IOException {
		output.write(ByteArrayBuilder.parseInt(UNCHOKE_LENGTH, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN));
		output.write(TorrentMessage.SIGN_UNCHOKE);
	}

	public static MessageUnchoke decode(MarkableReader reader) throws IOException {
		_length(reader, UNCHOKE_LENGTH);
		_signed(reader, TorrentMessage.SIGN_UNCHOKE);
		return new MessageUnchoke();
	}
}
