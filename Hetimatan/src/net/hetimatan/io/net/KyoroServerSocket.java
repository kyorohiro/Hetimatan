package net.hetimatan.io.net;

import java.io.IOException;

public abstract class KyoroServerSocket extends KyoroSelectable {
	public abstract void regist(KyoroSelector selector, int key) throws IOException;
	public abstract void bind(int port) throws IOException;
	public abstract int getPort() throws IOException;
	public abstract int select(int timeout) throws IOException;
	public abstract KyoroSocket accept() throws IOException;
	public abstract void close() throws IOException;
	public abstract boolean isBinded();
}
