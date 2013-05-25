package net.hetimatan.io.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class KyoroServerSocketImpl extends KyoroServerSocket {

	private ServerSocketChannel mServerChannel = null;
	private Selector mSelector = null;
	private boolean mOwnSelector = false;

	public KyoroServerSocketImpl() throws IOException {
		this(null);
	}

	public KyoroServerSocketImpl(Selector selector) throws IOException {
		 mServerChannel = ServerSocketChannel.open();
		 if(selector == null) {
			 mSelector = Selector.open();
			 mOwnSelector = true;
		 } else {
			 mSelector = selector;
		 }
		 mServerChannel.configureBlocking(false);
	}

	@Override
	public int select(int timeout) throws IOException {
		return mSelector.select(timeout);
	}

	@Override
	public void bind(int port) throws IOException {
		mServerChannel.socket().bind(new InetSocketAddress(port));
		mServerChannel.register(mSelector, SelectionKey.OP_ACCEPT);
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
		if(true == mOwnSelector&& mSelector != null) {
			mSelector.close();
		}
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
