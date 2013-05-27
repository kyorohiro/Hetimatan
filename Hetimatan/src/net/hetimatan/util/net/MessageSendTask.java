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
	private ByteArrayBuilder mBuffer = null;

	public MessageSendTask(EventTaskRunner runner, KyoroSocket socket, KyoroFile data) {
		super(runner);
		mData = data;
		mSocket = socket;
	}

	public void setLength(int size) {
		mBufferSize = size;
	}

	public void setCash(ByteArrayBuilder builder) {
		mBuffer = builder;
	}

	private EventTask mNext = null;
	@Override
	public void action() throws Throwable {
		if(Log.ON) {Log.v("MessageSendTask", "acrion");}
		if(mNext == null) {
			mNext = nextAction();
		}
		super.action();
		int len = (int)mData.length();
		if (len>mBufferSize) {
			len = mBufferSize;
		}
		if (mBuffer == null) {
			mBuffer = new ByteArrayBuilder(len);
		} else {
			mBuffer.setBufferLength(len);
		}

		byte[] buffer = mBuffer.getBuffer();
		len = mData.read(buffer, 0, len);
		if (len<0) {
			if(Log.ON) {Log.v("MessageSendTask", "-e");}
			nextAction(mNext);
			return;}
		int wrlen = mSocket.write(buffer, 0, len);
		if (wrlen<0) {return;}
//		mData.seek(mData.getFilePointer()-(len-wrlen));
		if (mData.getFilePointer()<mData.length()) {
			if(Log.ON) {Log.v("MessageSendTask", "n");}
			nextAction(this);
		} else {
			if(Log.ON) {Log.v("MessageSendTask", "e");}
//			mSocket.close();
			nextAction(mNext);
		}

	}
	
}
