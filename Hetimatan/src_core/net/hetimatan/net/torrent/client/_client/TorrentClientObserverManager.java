package net.hetimatan.net.torrent.client._client;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;

import net.hetimatan.net.torrent.client.TorrentClientFront;
import net.hetimatan.net.torrent.client.TorrentClientListener;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.net.torrent.tracker.TrackerClient;

public class TorrentClientObserverManager {

	private LinkedList<WeakReference<TorrentClientListener>> mObservers = new LinkedList<WeakReference<TorrentClientListener>>();

	public void addObserverAtWeak(TorrentClientListener observer) {
		mObservers.add(new WeakReference<TorrentClientListener>(observer));
	}

	public void dispatchTorrentMessage(TorrentClientFront front, TorrentMessage message) throws IOException {
		front.onReceiveMessage(message);
		Iterator<WeakReference<TorrentClientListener>>	ite = mObservers.iterator();
		while(ite.hasNext()) {
			WeakReference<TorrentClientListener> observerref = ite.next();
			TorrentClientListener observer = observerref.get();
			if(null == observerref.get()) {
				mObservers.remove(observerref);
			}
			observer.onReceiveMessage(front, message);
		}
	}

	public void dispatchTrackerResponse(TrackerClient client) throws IOException {
		Iterator<WeakReference<TorrentClientListener>>	ite = mObservers.iterator();
		while(ite.hasNext()) {
			WeakReference<TorrentClientListener> observerref = ite.next();
			TorrentClientListener observer = observerref.get();
			if(null == observerref.get()) {
				mObservers.remove(observerref);
			}
			observer.onResponsePeerList(client);
		}
	}

	public void dispatchCloseFront(TorrentClientFront front) throws IOException {
		Iterator<WeakReference<TorrentClientListener>>	ite = mObservers.iterator();
		while(ite.hasNext()) {
			WeakReference<TorrentClientListener> observerref = ite.next();
			TorrentClientListener observer = observerref.get();
			if(null == observerref.get()) {
				mObservers.remove(observerref);
			}
			observer.onClose(front);
		}
	}
	
}
