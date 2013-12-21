package net.hetimatan.net.torrent.client._client;

import java.io.IOException;

import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.client.TorrentClientFront;
import net.hetimatan.net.torrent.client.TorrentClientListener;
import net.hetimatan.net.torrent.client._client.TorrentClientMessageDispatcher;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.net.torrent.tracker.TrackerClient;

/**
 * for test 
 * connect  
 *
 */
public class ConnectTicket implements TorrentClientListener {

	private TorrentClientFront mConnected = null;
	public ConnectTicket(TorrentClient client) throws IOException {
		if(client == null) {throw new IOException();}
		TorrentClientMessageDispatcher dispatcher = client.getDispatcher();
		if(dispatcher == null) {throw new IOException();}
		dispatcher.addObserverAtWeak(this);
	}

	@Override
	public void onConnection(TorrentClientFront front) throws IOException {
		if(mConnected == null) {
			mConnected = front;
			_notify();
		}
	}

	@Override
	public void onClose(TorrentClientFront front) throws IOException {
	}

	@Override
	public void onClose(TorrentClient client) throws IOException {
	}

	@Override
	public void onShakeHand(TorrentClientFront front) throws IOException {
	}

	@Override
	public void onSendMessage(TorrentClientFront front, TorrentMessage message) throws IOException {
	}

	private Object lock = new Object();
	public TorrentClientFront getTorrentClientFront() {
		synchronized (lock) {
			if(mConnected == null) {
				_wait();
			}
		}
		return mConnected;
	}

	private void _wait() {
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void _notify() {
		synchronized (lock) {
			lock.notify();
		}
	}

	@Override
	public void onReceiveMessage(TorrentClientFront front, TorrentMessage message) throws IOException {
	}

	@Override
	public void onResponsePeerList(TrackerClient client) throws IOException {
	}

	@Override
	public void onInterval(TorrentClient client) {
		// TODO Auto-generated method stub
		
	}

}
