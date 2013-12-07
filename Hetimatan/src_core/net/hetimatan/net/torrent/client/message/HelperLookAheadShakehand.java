package net.hetimatan.net.torrent.client.message;

import java.io.IOException;

import net.hetimatan.io.file.MarkableReader;

public class HelperLookAheadShakehand {

	private long mStartFP = 0;
	private boolean mIsEnd = false;
	private MarkableReader mReader = null;

	public HelperLookAheadShakehand(long fp, MarkableReader _reader) {
		mStartFP = fp;
		mReader = _reader;
	}

	public void clear(long fp) {
		mStartFP = fp;
		mIsEnd = false;
	}
	
	public boolean parseable() {
		return mIsEnd;
	}
	
	public void read() throws IOException {
		try  {
			mReader.seek(mStartFP);
			MessageHandShake.decode(mReader);
			mIsEnd = true;
		} catch(IOException e) {
		} catch(NegativeArraySizeException e) {			
		} finally {
			mReader.seek(mStartFP);
		}
	}
	
}
