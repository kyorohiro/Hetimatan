package net.hetimatan.net.torrent.client.message;


import java.io.IOException;
import java.io.OutputStream;

import net.hetimatan.ky.io.MarkableReader;
import net.hetimatan.ky.io.MarkableReaderHelper;
import net.hetimatan.util.io.ByteArrayBuilder;

public class MessageHave extends TorrentMessage {

	private int mIndex = 0;
	public static final int HAVE_LENGTH = 5;
	public MessageHave(int index) {
		super(TorrentMessage.SIGN_HAVE);
		mIndex = index;
	}

	public int getIndex() {
		return mIndex;
	}

	@Override
	public void encode(OutputStream output) throws IOException {
		output.write(ByteArrayBuilder.parseInt(HAVE_LENGTH, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN));
		output.write(TorrentMessage.SIGN_HAVE);
		output.write(ByteArrayBuilder.parseInt(mIndex, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN));
	}

	public static MessageHave decode(MarkableReader reader) throws IOException {
		try {
			reader.pushMark();
			_length(reader, HAVE_LENGTH);
			_signed(reader, TorrentMessage.SIGN_HAVE);
			int index = MarkableReaderHelper.readInt(reader, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN);
			return new MessageHave(index);
		} catch(IOException e){
			reader.backToMark();
			throw e;
		} finally {
			reader.popMark();			
		}
	}
}
