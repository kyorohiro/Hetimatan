package net.hetimatan.io.net;

import java.io.IOException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;

public class KyoroDatagramImpl extends KyoroSelectable {

	private DatagramChannel mChannel = null;

	public KyoroDatagramImpl() throws IOException {
		mChannel = DatagramChannel.open();
		mChannel.configureBlocking(false);
	}

	@Override
	public SelectableChannel getRawChannel() {
		return mChannel;
	}

}
