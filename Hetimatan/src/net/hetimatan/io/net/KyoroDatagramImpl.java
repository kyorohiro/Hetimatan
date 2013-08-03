package net.hetimatan.io.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;

import net.hetimatan.util.http.HttpObject;

public class KyoroDatagramImpl extends KyoroSelectable {

	private DatagramChannel mChannel = null;
	private ByteBuffer mBuffer = ByteBuffer.allocate(8*1024);

	public KyoroDatagramImpl() throws IOException {
		mChannel = DatagramChannel.open();
		mChannel.configureBlocking(false);
	}

	@Override
	public SelectableChannel getRawChannel() {
		return mChannel;
	}

	public byte[] receive() throws IOException {
		SocketAddress address = mChannel.receive(mBuffer);
		byte[] ret = new byte[4];
		byte[] ad = ((InetSocketAddress)address).getAddress().getAddress();
		int port =  ((InetSocketAddress)address).getPort();
		System.arraycopy(ad, 0, ret, 0, 4);
		System.arraycopy(HttpObject.portToB(port), 0, ret, 4, 2);
		return ret;
	}

	public int send(byte[] message, byte[] address) throws IOException {
		InetSocketAddress iad = getInetSocketAddress(address);
		ByteBuffer buffer = ByteBuffer.allocate(message.length);
		buffer.put(message, 0, message.length);
		buffer.flip();
		return mChannel.send(buffer, iad);
	}

	InetSocketAddress getInetSocketAddress(byte[] info) {
		byte[] address = new byte[4];
		byte[] port = new byte[2];
		System.arraycopy(info, 0, address, 0, 4);
		System.arraycopy(info, 4, port, 0, 2);
		return new InetSocketAddress(HttpObject.ntoa(address), HttpObject.bToPort(port));
	}
	
	@Override
	public void close() throws IOException {
		try {
			if(mChannel != null) {
				mChannel.close();
			}
			mChannel = null;
		} finally {
			super.close();
		}
	}
}
