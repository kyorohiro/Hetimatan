package net.hetimatan.io.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class KyoroServerSocketImpl extends KyoroServerSocket {

	private ServerSocketChannel mServerChannel = null;

	public KyoroServerSocketImpl() throws IOException {
		this(null);
	}

	public KyoroServerSocketImpl(Selector selector) throws IOException {
		 mServerChannel = ServerSocketChannel.open();
		 mServerChannel.configureBlocking(false);
	}

	@Override
	public void bind(int port) throws IOException {
		mServerChannel.socket().bind(new InetSocketAddress(port));
		mIsBinded = true;
	}

	private boolean mIsBinded = false;
	@Override
	public boolean isBinded() {
		return mIsBinded;
	}

	@Override
	public int getPort() throws IOException {
		return mServerChannel.socket().getLocalPort();
	}


	@Override
	public KyoroSocket accept() throws IOException {
		SocketChannel channel = mServerChannel.accept();
		if(channel == null) {
			return null;
		}
		return new KyoroSocketImpl(channel);
	}

	@Override
	public void close() throws IOException {
		if(mServerChannel != null) {
			mServerChannel.close();
		}
		super.close();
	}

	@Override
	public void regist(KyoroSelector selector, int key) throws ClosedChannelException, IOException {
		mServerChannel.register(selector.getSelector(), key);
		selector.putClient(this);
	}

	public ServerSocketChannel getRawChannel() {
		return mServerChannel;
	}
}
