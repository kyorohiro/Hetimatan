package net.hetimatan.net.torrent.client.message;


import java.io.IOException;
import java.io.OutputStream;

import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.util.io.ByteArrayBuilder;

public class MessageNull extends TorrentMessage {

	public MessageNull() {
		super(TorrentMessage.DUMMY_SIGN_NULL);
	}

	@Override
	public void encode(OutputStream output) throws IOException {
		output.write(ByteArrayBuilder.parseInt(0, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN));
	}

	public static MessageNull decode(MarkableReader reader) throws IOException {
		int len =_length(reader);
		for(int i=0;i<len;i++){
			reader.read();
		}
		return new MessageNull();
	}
}
