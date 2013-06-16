package net.hetimatan.io.net;


import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

public class KyoroSelector {
	public static final int ACCEPT = SelectionKey.OP_ACCEPT;
	public static final int READ = SelectionKey.OP_READ;
	public static final int WRITE = SelectionKey.OP_WRITE;
	public static final int CONNECT = SelectionKey.OP_CONNECT;
	public static final int CANCEL = -1;

	private Selector mSelector = null;
	private boolean mIsClosed = false;

	private WeakHashMap<SelectableChannel, KyoroSelectable> mClientList 
	= new WeakHashMap<SelectableChannel, KyoroSelectable>();

	private Iterator<SelectionKey>  mCurrentKeyList = null;
	private SelectionKey mCurrentKey = null;
	private KyoroSelectable mCurrentSocket = null;

	public void putClient(KyoroSelectable s) {
		mClientList.put(s.getRawChannel(), s);
	}

	public Selector getSelector() throws IOException {
		if(mSelector == null&&!mIsClosed) {
			mSelector = Selector.open();
		}
		return mSelector;
	}

	public Set<SelectionKey> a() throws IOException {
		Selector selector = getSelector();
		return selector.selectedKeys();
	}

	public int select(int timeout) throws IOException {
		Selector selector = getSelector();
		if(selector == null) {
			return 0;
		}
		int ret = 0;
	
		if(timeout == 0) {
			ret = selector.selectNow();
		} else {
			ret = selector.select(timeout);
		}
		return ret;
	}

	public synchronized void wakeup() {
		mSelector.wakeup();
	}

	public void close() throws IOException {
		mIsClosed = true;
		Selector s =mSelector;
		if(s != null) {
			for(SelectionKey key:s.selectedKeys()) {
				if(key != null) {
					key.cancel();
				}
			}
			s.close();
			mSelector = null;
		}
	}


	public boolean next() {
		mCurrentKey = null;
		mCurrentSocket = null;

		if(mCurrentKeyList == null) {
			mCurrentKeyList = mSelector.selectedKeys().iterator();
		}
		if (!mCurrentKeyList.hasNext()) {
			mCurrentKeyList = null;
			return false;
		}
		mCurrentKey = mCurrentKeyList.next();
		mCurrentKeyList.remove();
		SelectableChannel channel = mCurrentKey.channel();
		mCurrentSocket = mClientList.get(channel);
		return true;
	}

	public KyoroSelectable getCurrentSocket() {
		return mCurrentSocket;
	}


	public int getkey() {
		try {
			if(mCurrentKey.isAcceptable()) {
				return KyoroSelector.ACCEPT;
			}
			else if(mCurrentKey.isConnectable()) {
				return KyoroSelector.CONNECT;
			}
			else if(mCurrentKey.isReadable()) {
				return KyoroSelector.READ;
			}
			else if(mCurrentKey.isWritable()) {
				return KyoroSelector.WRITE;
			}
		} catch(CancelledKeyException e) {}
		return KyoroSelector.CANCEL;
	}
	
}
