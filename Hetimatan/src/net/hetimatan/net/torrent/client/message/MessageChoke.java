package net.hetimatan.net.torrent.client.message;


import java.io.IOException;
import java.io.OutputStream;

import net.hetimatan.ky.io.MarkableReader;
import net.hetimatan.util.io.ByteArrayBuilder;

public class MessageChoke extends TorrentMessage {
	public static final int CHOKE_LENGTH = 1;

	public MessageChoke() {
		super(TorrentMessage.SIGN_CHOKE);
	}

	@Override
	public void encode(OutputStream output) throws IOException {
		output.write(ByteArrayBuilder.parseInt(MessageChoke.CHOKE_LENGTH, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN));
		output.write(TorrentMessage.SIGN_CHOKE);
	}

	public static MessageChoke decode(MarkableReader reader) throws IOException {
		_length(reader, CHOKE_LENGTH);
		_signed(reader, TorrentMessage.SIGN_CHOKE);
		return new MessageChoke();
	}
}
