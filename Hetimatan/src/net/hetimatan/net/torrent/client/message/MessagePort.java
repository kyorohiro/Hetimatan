package net.hetimatan.net.torrent.client.message;


import java.io.IOException;
import java.io.OutputStream;

import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.io.file.MarkableReaderHelper;
import net.hetimatan.util.http.HttpObject;
import net.hetimatan.util.io.ByteArrayBuilder;

public class MessagePort extends TorrentMessage {

	private int mPort = 0;
	public static final String TAG = "port";
	public static final int PORT_LENGTH = 3;
	public MessagePort(int port) {
		super(TorrentMessage.SIGN_HAVE);
		mPort = port;
	}

	@Override
	public String toString() {
		return TAG+":"+mPort;
	}

	public int getPort() {
		return mPort;
	}

	@Override
	public void encode(OutputStream output) throws IOException {
		output.write(ByteArrayBuilder.parseInt(PORT_LENGTH, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN));
		output.write(TorrentMessage.SIGN_PORT);
		output.write(HttpObject.portToB(mPort));
	}

	public static MessagePort decode(MarkableReader reader) throws IOException {
		try {
			reader.pushMark();
			_length(reader, PORT_LENGTH);
			_signed(reader, TorrentMessage.SIGN_PORT);
			int port = MarkableReaderHelper.readShort(reader, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN);
			return new MessagePort(port);
		} catch(IOException e){
			reader.backToMark();
			throw e;
		} finally {
			reader.popMark();			
		}
	}
}
