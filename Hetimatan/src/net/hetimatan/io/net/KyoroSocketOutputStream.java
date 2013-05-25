package net.hetimatan.io.net;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class KyoroSocketOutputStream extends OutputStream {

	private KyoroSocket mSocket = null;

	public KyoroSocketOutputStream(KyoroSocket socket) {
		mSocket = socket;
	}

	private boolean mLogon = false;
	public int vi = 0;
	public void logon(boolean on) {
		mLogon = on;
		vi=0;
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		mSocket.write(b, off, len);
		vi += len-off;
	}

	@Override
	public void write(int b) throws IOException {
		vi++;
		ByteBuffer buffer = ByteBuffer.allocate(1);
		buffer.put((byte)(0xFF&b));
		buffer.flip();
		mSocket.write(buffer.array(), 0, 1);
	}

}
