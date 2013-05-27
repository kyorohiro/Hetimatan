package net.hetimatan.util.net;

import net.hetimatan.io.file.KyoroFile;
import net.hetimatan.io.net.KyoroSocket;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;
import net.hetimatan.util.io.ByteArrayBuilder;
import net.hetimatan.util.log.Log;


public class MessageSendTask extends EventTask {
	private int mBufferSize = 512;
	private KyoroFile mData = null;
	private KyoroSocket mSocket = null;
	private boolean mIsKeep = false;

	public MessageSendTask(EventTaskRunner runner, KyoroSocket socket, KyoroFile data) {
		super(runner);
		mData = data;
		mSocket = socket;
	}

	public void setLength(int size) {
		mBufferSize = size;
	}

	@Override
	public boolean isKeep() {
		return mIsKeep;
	}

	@Override
	public void action() throws Throwable {
		if(Log.ON) {Log.v("MessageSendTask", "acrion");}
		mIsKeep = false;
		super.action();
		int len = (int)mData.length();
		if (len>mBufferSize) {
			len = mBufferSize;
		}
		ByteArrayBuilder bufferBase = KyoroSocketEventRunner.getByteArrayBuilder();
		bufferBase.setBufferLength(len);
		byte[] buffer = bufferBase.getBuffer();
		len = mData.read(buffer, 0, len);
		if (len<0) {
			return;
		}
		int wrlen = mSocket.write(buffer, 0, len);
		if (wrlen<0) {return;}
		mData.seek(mData.getFilePointer()-(len-wrlen));
		if (mData.getFilePointer()<mData.length()) {
			mIsKeep = true;
		}
	}
	
}
