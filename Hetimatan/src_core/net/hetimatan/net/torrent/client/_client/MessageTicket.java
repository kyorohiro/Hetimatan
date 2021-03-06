package net.hetimatan.net.torrent.client._client;

import java.io.IOException;

import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.client.TorrentClientFront;
import net.hetimatan.net.torrent.client.TorrentClientListener;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.net.torrent.tracker.TrackerClient;

/**
 * for test 
 * received message,  
 *
 */
public class MessageTicket implements TorrentClientListener {

	private int mMessageType = 0;
	private TorrentMessage mReceived = null;
	private TorrentClientFront mFront = null;

	public MessageTicket(TorrentClientFront front, int messageType) throws IOException {
		mMessageType = messageType;
		mFront = front;
		if(front == null) {throw new IOException();}
		TorrentClient client =  front.getTorrentPeer();
		if(client == null) {throw new IOException();}
		TorrentClientMessageDispatcher dispatcher = client.getDispatcher();
		if(dispatcher == null) {throw new IOException();}
		dispatcher.addObserverAtWeak(this);
	}

	@Override
	public void onConnection(TorrentClientFront front) throws IOException {
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
	public TorrentMessage getMessage() {
		synchronized (lock) {
			if(mReceived == null) {
				_wait();
			}
		}
		return mReceived;
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
		if(message == null) {return;}
		if(mMessageType!=-1 && mMessageType != message.getType()) {
			return;
		}
		if(front == mFront) {
			mReceived = message;
			_notify();
		}
	}

	@Override
	public void onResponsePeerList(TorrentClient client, TrackerClient tracker) throws IOException {
	}

	@Override
	public void onInterval(TorrentClient client) {
		// TODO Auto-generated method stub
		
	}

}
