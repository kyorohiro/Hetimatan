package net.hetimatan.net.torrent.client._client;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;

import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.client.TorrentClientFront;
import net.hetimatan.net.torrent.client.TorrentClientListener;
import net.hetimatan.net.torrent.client.TorrentHistory;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.net.torrent.tracker.TrackerClient;
import net.hetimatan.net.torrent.tracker.TrackerPeerInfo;

/**
 * if finded new peer, connect this.
 *
 */
public class TorrentClientStartConnection implements TorrentClientListener {

	private WeakReference<TorrentClient> mUploadTargetPeer = null;

	public TorrentClientStartConnection(TorrentClient target) throws IOException {
		mUploadTargetPeer = new WeakReference<TorrentClient>(target);
	}

	public void updatePeerList_tracker2client() throws IOException {
	 	TorrentClient peer = mUploadTargetPeer.get();
	 	if(peer == null) {return;}
	 	if(peer.isSeeder()) {
	 		return ;
	 	}
	 	TrackerClient client = peer.getTracker();
	 	Iterator<TrackerPeerInfo> peers32 = client.getPeer32();
	 	if(!peer.isSeeder()) {//todo
	 		while(peers32.hasNext()) {
	 			TrackerPeerInfo targetPeer = peers32.next();
	 			peer.startConnect(targetPeer);
	 			if(!mPeerInfoList.contains(targetPeer)) {
	 				mPeerInfoList.add(new PeerInfo(targetPeer));
	 			}
	 		}
	 	}
	 	client.clearPeer32();
	}

	public void startConnection() throws IOException {
		TorrentHistory.get().pushMessage("startConnection(\r\n");
	 	TorrentClient peer = mUploadTargetPeer.get();
	 	if(peer == null) {return;}
		int size = mPeerInfoList.size();
		for(int i=0;i<size;i++) {
			peer.startConnect(mPeerInfoList.get(i).mPeerInfo);
		}
	}

	@Override
	public void onReceiveMessage(TorrentClientFront front, TorrentMessage message) {
	}

	@Override
	public void onResponsePeerList(TrackerClient client) {
		try {
			updatePeerList_tracker2client();
			startConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClose(TorrentClientFront front) {
	 	TorrentClient peer = mUploadTargetPeer.get();
	 	if(peer == null) {return;}
		try {
			if(front.isOneself()||!front.isConnectable()) {
				int i = mPeerInfoList.indexOf(front.getPeer());
				if(i!=-1) {
					mPeerInfoList.remove(mPeerInfoList.get(i));
				}
			} else if(!peer.isSeeder()){
				startConnection();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public LinkedList<PeerInfo> mPeerInfoList = new LinkedList<>();
	
	public static class PeerInfo implements Comparable<PeerInfo>{
		private TrackerPeerInfo mPeerInfo = null;
		private int mStatus = 0;
		public PeerInfo(TrackerPeerInfo info) {
			mPeerInfo = info;
		}
		@Override
		public int compareTo(PeerInfo o) {
			return mPeerInfo.compareTo(o.mPeerInfo);
		}

		@Override
		public boolean equals(Object obj) {
			return mPeerInfo.equals(obj);
		}
	}

	@Override
	public void onShakeHand(TorrentClientFront front) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnection(TorrentClientFront front) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClose(TorrentClient client) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSendMessage(TorrentClientFront front, TorrentMessage message)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onInterval(TorrentClient client) {
		// TODO Auto-generated method stub
		
	}
}
