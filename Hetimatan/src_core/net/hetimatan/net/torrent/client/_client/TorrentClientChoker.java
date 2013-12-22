package net.hetimatan.net.torrent.client._client;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.Random;

import net.hetimatan.net.torrent.client.TorrentClientFront;
import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.client.TorrentClientListener;
import net.hetimatan.net.torrent.client.TorrentClientSetting;
import net.hetimatan.net.torrent.client._front.TorrentClientFrontTargetInfo;
import net.hetimatan.net.torrent.client._front.TorrentFrontMyInfo;
import net.hetimatan.net.torrent.client.message.MessageHave;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.net.torrent.tracker.TrackerClient;
import net.hetimatan.net.torrent.tracker.TrackerPeerInfo;
import net.hetimatan.util.bitfield.BitField;

public class TorrentClientChoker implements TorrentClientListener {

	private WeakReference<TorrentClient> mOwner = null;
	private TorrentClientListener mChoke = new TorrentClientChokerRuleChoke();
	private TorrentClientListener mInterest = new TorrentClientChokerRuleInterest();

	public TorrentClientChoker(TorrentClient owner) {
		mOwner = new WeakReference<TorrentClient>(owner);
	}

	@Override
	public void onConnection(TorrentClientFront front) throws IOException {
		mChoke.onConnection(front);
		mInterest.onConnection(front);		
	}

	@Override
	public void onClose(TorrentClientFront front) throws IOException {
		mChoke.onClose(front);
		mInterest.onClose(front);
	}

	@Override
	public void onShakeHand(TorrentClientFront front) throws IOException {
		mChoke.onShakeHand(front);
		mInterest.onShakeHand(front);
	}

	@Override
	public void onReceiveMessage(TorrentClientFront front, TorrentMessage message) throws IOException {
		mChoke.onReceiveMessage(front, message);
		mInterest.onReceiveMessage(front, message);
	}

	@Override
	public void onResponsePeerList(TrackerClient client) throws IOException {
		mChoke.onResponsePeerList(client);
		mInterest.onResponsePeerList(client);
	}

	@Override
	public void onClose(TorrentClient client) throws IOException {
		mChoke.onClose(client);
		mInterest.onClose(client);
	}

	@Override
	public void onSendMessage(TorrentClientFront front, TorrentMessage message)throws IOException {
		mChoke.onSendMessage(front, message);
		mInterest.onSendMessage(front, message);
	}

	@Override
	public void onInterval(TorrentClient client) {
		mChoke.onInterval(client);
		mInterest.onInterval(client);
	}

	
}
