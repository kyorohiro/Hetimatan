package net.hetimatan.net.torrent.client.message;

import java.io.IOException;

import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.io.file.MarkableReaderHelper;
import net.hetimatan.util.io.ByteArrayBuilder;
import net.hetimatan.util.url.PercentEncoder;


public class HelperLookAheadMessage {

	private int mMessageSize = -1;
	private long mStartFP = 0;
	private boolean mIsEnd = false;
	private MarkableReader mReader = null;
	private int mMessageId = -1;

	public HelperLookAheadMessage(long fp, MarkableReader _reader) {
		mStartFP = fp;
		mReader = _reader;
	}

	public void clear(long fp) {
		mStartFP = fp;
		mMessageSize = -1;
		mIsEnd = false;
		mMessageId = -1;
	}
	
	public void printLog() {
		try {
			_showLog();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void _showLog() throws IOException {
		long fp = 0;
		fp = mReader.getFilePointer();
		try {
			mReader.seek(mStartFP);
			StringBuilder builder = new StringBuilder();
			builder.append("id:"+getMessageId());
			builder.append("size:"+getMessageSize());
			if(isEnd()) {
				byte[] content = getMessageContent();
				if(content != null && content.length>0) {
					PercentEncoder encoder = new PercentEncoder();
					builder.append(""+encoder.encode(content));			
				}
			} 
			System.out.println(""+builder.toString());
		} finally {
			mReader.seek(fp);
		}
	}
	public boolean isEnd() {
		return mIsEnd;
	}

	public long myMessageFP() {
		return mStartFP;
	}

	public long nextMessageFP() {
		long end = mStartFP + 4 + mMessageSize;
		return end;
	}

	public long length() {
		return 4+mMessageSize;
	}

	public int getMessageSize(){
		return mMessageSize;
	}

	public int getMessageId() {
		return mMessageId;
	}

	public byte[] getMessageContent() throws IOException {
		int messageSize = getMessageSize()-1;
		if(messageSize<=0){
			return new byte[0];
		}

		byte[] buffer = new byte[messageSize];
		long fp = mReader.getFilePointer();
		try {
			mReader.seek(mStartFP+4+1);
			mReader.read(buffer, 0, buffer.length);
		} finally {
			mReader.seek(fp);
		}
		return null;
	}


	public void read() throws IOException {
	//	if(mStartFP== 528) {
	//		System.out.println("--a--");
	//	}
		if (mMessageSize < 0) {
			// todo add check EOF
			int size = -1;
			try {
				//mReader.pushMark();
				size = MarkableReaderHelper.readInt(mReader,
					ByteArrayBuilder.BYTEORDER_BIG_ENDIAN);
			} catch(IOException e) {
			//	mReader.backToMark();		
				return;
			} finally {
			//	mReader.popMark();
			}
			//if (size < 0) {
			//	return;
			//}
			System.out.println("size="+size);
			mMessageSize = size;
		}
		if(mMessageSize>0&&mMessageId==-1) {
			int tmp = mReader.read();
			if(tmp>=0) {
				mMessageId = tmp;
			}
		}
		long end = nextMessageFP();
		int ret = 0;
		while (mReader.getFilePointer() < end) {
			ret = mReader.read();
			if (ret < 0) {
				break;
			}
		}
		if (mReader.getFilePointer() < end) {
			mIsEnd = false;
		} else {
			mIsEnd = true;
		}
	}

}
