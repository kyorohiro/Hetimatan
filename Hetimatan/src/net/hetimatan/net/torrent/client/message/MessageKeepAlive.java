package net.hetimatan.net.torrent.client.message;


import java.io.IOException;
import java.io.OutputStream;

import net.hetimatan.ky.io.MarkableReader;
import net.hetimatan.util.io.ByteArrayBuilder;

public class MessageKeepAlive extends TorrentMessage {

	public MessageKeepAlive() {
		super(TorrentMessage.DUMMY_SIGN_KEEPALIVE);
	}

	@Override
	public void encode(OutputStream output) throws IOException {
		output.write(ByteArrayBuilder.parseInt(0, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN));
	}

	public static MessageKeepAlive decode(MarkableReader reader) throws IOException {
		_length(reader, 0);
		return new MessageKeepAlive();
	}
}
