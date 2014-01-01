package net.hetimatan.net.torrent.client._client;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;

import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.client.TorrentClientFront;
import net.hetimatan.net.torrent.client.TorrentClientListener;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.net.torrent.tracker.TrackerClient;

public class TorrentClientMessageDispatcher {

	private LinkedList<WeakReference<TorrentClientListener>> mObservers = new LinkedList<WeakReference<TorrentClientListener>>();

	public void addObserverAtWeak(TorrentClientListener observer) {
		mObservers.add(new WeakReference<TorrentClientListener>(observer));
	}

	public void dispatchTorrentMessage(TorrentClientFront front, TorrentMessage message) throws IOException {
		front.onReceiveMessage(message);
		for(int i=0;i<mObservers.size();i++) {
			WeakReference<TorrentClientListener> observerref = mObservers.get(i);
			TorrentClientListener observer = observerref.get();
			if(null == observerref.get()) {
				mObservers.remove(observerref);
				continue;
			}
			observer.onReceiveMessage(front, message);
		}
	}

	public void dispatchTrackerResponse(TorrentClient client, TrackerClient tracker) throws IOException {
		for(int i=0;i<mObservers.size();i++) {
			WeakReference<TorrentClientListener> observerref = mObservers.get(i);
			TorrentClientListener observer = observerref.get();
			if(null == observerref.get()) {
				mObservers.remove(observerref);
				continue;
			}
			observer.onResponsePeerList(client, tracker);
		}
	}

	public void dispatchCloseFront(TorrentClientFront front) throws IOException {
		for(int i=0;i<mObservers.size();i++) {
			WeakReference<TorrentClientListener> observerref = mObservers.get(i);
			TorrentClientListener observer = observerref.get();
			if(null == observerref.get()) {
				mObservers.remove(observerref);
				continue;
			}
			observer.onClose(front);
		}
	}

	public void dispatchShakeHand(TorrentClientFront front) throws IOException {
		for(int i=0;i<mObservers.size();i++) {
			WeakReference<TorrentClientListener> observerref = mObservers.get(i);
			TorrentClientListener observer = observerref.get();
			if(null == observerref.get()) {
				mObservers.remove(observerref);
				continue;
			}
			observer.onShakeHand(front);			
		}
	}

	public void dispatchConnection(TorrentClientFront front) throws IOException {
		for(int i=0;i<mObservers.size();i++) {
			WeakReference<TorrentClientListener> observerref = mObservers.get(i);
			TorrentClientListener observer = observerref.get();
			if(null == observerref.get()) {
				mObservers.remove(observerref);
				continue;
			}
			observer.onConnection(front);
		}
	}

	public void dispatchClose(TorrentClient client) throws IOException {
		for(int i=0;i<mObservers.size();i++) {
			WeakReference<TorrentClientListener> observerref = mObservers.get(i);
			TorrentClientListener observer = observerref.get();
			if(null == observerref.get()) {
				mObservers.remove(observerref);
				continue;
			}
			observer.onClose(client);
		}
	}
	
	public void dispatchSendTorrentMessage(TorrentClientFront front, TorrentMessage message) throws IOException {
		for(int i=0;i<mObservers.size();i++) {
			WeakReference<TorrentClientListener> observerref = mObservers.get(i);
			TorrentClientListener observer = observerref.get();
			if(null == observerref.get()) {
				mObservers.remove(observerref);
				continue;
			}
			observer.onSendMessage(front, message);
		}
	}

	public void dispatchIntervalAction(TorrentClient client) throws IOException {
		for(int i=0;i<mObservers.size();i++) {
			WeakReference<TorrentClientListener> observerref = mObservers.get(i);
			TorrentClientListener observer = observerref.get();
			if(null == observerref.get()) {
				mObservers.remove(observerref);
				continue;
			}
			observer.onInterval(client);
		}
	}
}
