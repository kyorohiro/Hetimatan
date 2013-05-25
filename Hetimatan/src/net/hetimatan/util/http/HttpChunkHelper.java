package net.hetimatan.util.http;


import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.nio.ByteBuffer;

import net.hetimatan.ky.io.MarkableReader;
import net.hetimatan.util.event.EventTaskRunner;
import net.hetimatan.util.io.ByteArrayBuilder;

@Deprecated
public class HttpChunkHelper {
	public static final int EOF = 0;
	public static final int CRLF = 1;
	public static final int KEEP = 2;
	private MarkableReader mCurrentReader = null;
	private long mStart = 0;
	private long mMax = 0;
	private int[] mCrlfCheck = new int[] { -1, '\n', '\n' };

	public HttpChunkHelper(MarkableReader reader, int size) throws IOException {
		mCurrentReader = reader;
		mStart = reader.getFilePointer();
		mMax = size;
	}

	public long getStart() {
		return mStart;
	}

	
	private boolean mIsFirst = true;
	public int readByEndOfHeader(boolean checkCrlf) throws IOException {
		MarkableReader reader = mCurrentReader;
		reader.setBlockOn(false);
		int cur = 0;
		ByteArrayBuilder buffer = EventTaskRunner.getByteArrayBuilder();
		buffer.setBufferLength(5*1024);
		buffer.clear();
		byte[] buf = buffer.getBuffer();
		int bufLen = buf.length;
		
		do {
			// read buffer
			mCurrentReader.setBlockOn(false);
			if(mIsFirst) {reader.seek(mStart);}
			else {reader.seek(reader.getFilePointer()-4);}
			int len = reader.read(buf, 0, buf.length);
			if(len<0) {return EOF;}
			if(mIsFirst) {
				if(len==1&&(buf[0]=='\n')){
					mCurrentReader.seek(mStart+1);
					return CRLF;
				} else if(len==2&&buf[0]=='\r'&&buf[1] =='\n') {
					mCurrentReader.seek(mStart+2);
					return CRLF;
				}
				if(len>4) {
					mIsFirst = false;
				} else {
					if(0>mCurrentReader.read(buf, 0, bufLen)){
						return EOF;
					} else {
						return KEEP;
					}
				}
			}
			for(int i=0;i<len;i++) {
				if(buf[i]!='\n'&&buf[i]!='\r') {
					continue;
				}
				if(i+1<len&&buf[i] == '\n' &&buf[i+1]=='\n'){
					mCurrentReader.seek(mCurrentReader.length()-len+i+2);
					return EOF;
				}
				if(i+2<len&&buf[i] == '\n' &&buf[i] == '\r' &&buf[i+1]=='\n'){
					mCurrentReader.seek(mCurrentReader.length()-len+i+3);
					return EOF;
				}
				if(i+2<len&&buf[i] == '\r' &&buf[i] == '\n' &&buf[i+1]=='\n'){
					mCurrentReader.seek(mCurrentReader.length()-len+i+3);
					return EOF;
				}
				if(i+3<len&&buf[i] == '\r' &&buf[i+1]=='\n'&&buf[i+2] == '\r' &&buf[i+3]=='\n'){
					mCurrentReader.seek(mCurrentReader.length()-len+i+4);
					return EOF;
				}
			}
			len = mCurrentReader.read(buf, 0, bufLen);
			if(len<0) {return EOF;}
			else if(len == 0) {return KEEP;}
		} while (true);
	}

	public static boolean readByEndOfHeader(
			HttpChunkHelper headerChunk, MarkableReader currentReader) throws IOException {
		if (headerChunk == null) {return true;}
		int ret = headerChunk.readByEndOfHeader(true);
		if(ret == HttpChunkHelper.CRLF || ret == HttpChunkHelper.EOF) {
			return true;
		} else if(ret == HttpChunkHelper.KEEP) {
			return false;
		} else {
			return false;
		}
	}

}
