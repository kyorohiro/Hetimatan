package net.hetimatan.net.torrent.client.message;

import java.io.IOException;

import net.hetimatan.io.file.MarkableReader;

public class HelperLookAheadMessage {

	private long mStartFP = 0;
	private MarkableReader mReader = null;
	private MessageNull mNullMessage = new MessageNull(0, -999);

	public HelperLookAheadMessage(MarkableReader _reader) {
		mReader = _reader;
	}

	public MessageNull getMessageNull() {
		return mNullMessage;
	}

	public long getStartFp() {
		return mStartFP;
	}

	public boolean read() throws IOException {
		try  {
			mStartFP = mReader.getFilePointer();
			mNullMessage = MessageNull.decode(mReader);
			return true;
		} catch(IOException e) {
		} catch(NegativeArraySizeException e) {			
		} finally {
			mReader.seek(mStartFP);
		}
		return false;
	}
	
}
