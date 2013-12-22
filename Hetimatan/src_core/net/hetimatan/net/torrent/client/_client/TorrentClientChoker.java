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
	private LinkedList<TrackerPeerInfo> mOptimusUnchokePeer    = new LinkedList<>();

	public TorrentClientChoker(TorrentClient owner) {
		mOwner = new WeakReference<TorrentClient>(owner);
	}

	public void __choke(TorrentClientFront front) throws IOException {
		TrackerPeerInfo peer = front.getPeer();
		if(!mOptimusUnchokePeer.contains(peer)) {
			mOptimusUnchokePeer.remove(peer);
		}
		front.getTaskManager().startChoker(front.getTorrentPeer(), front, true);
	}
	
	public void __unchoke(TorrentClientFront front) throws IOException {
		TrackerPeerInfo peer = front.getPeer();
		if(!mOptimusUnchokePeer.contains(peer)) {
			mOptimusUnchokePeer.add(peer);
			front.getTaskManager().startChoker(front.getTorrentPeer(), front, false);
		}		
	}

	@Override
	public void onConnection(TorrentClientFront front) throws IOException {

	}

	@Override
	public void onClose(TorrentClientFront front) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onShakeHand(TorrentClientFront front) throws IOException {
		TorrentClient torrentPeer = mOwner.get();if(torrentPeer == null) {return;}
		TorrentClientSetting mSetting = torrentPeer.getSetting();
		int numOfUnchokerNow = mOptimusUnchokePeer.size();
		int maxOfUnchoker = mSetting.getNumOfUnchoker();

		System.out.println("--AA-0-"+numOfUnchokerNow);
		if (numOfUnchokerNow<maxOfUnchoker) {
			__unchoke(front);			
		} else {
			__choke(front);
		}
		System.out.println("--AA-1-"+numOfUnchokerNow);	
	}

	@Override
	public void onReceiveMessage(TorrentClientFront front, TorrentMessage message) {
		 updateInterest(front, message);
	}

	@Override
	public void onResponsePeerList(TrackerClient client) {
		// TODO Auto-generated method stub
		
	}

	public void updateInterest(TorrentClientFront front, TorrentMessage message) {
	 	TorrentClient peer = mOwner.get();
		if(peer == null) {return;}
		if(message.getType() == TorrentMessage.SIGN_BITFIELD) {
			BitField relative = front.relativeBitfield();
			if(!relative.isAllOff()) {
				front.getTaskManager().startInterest(front.getTorrentPeer(), front);
			} else {
				front.getTaskManager().startNotInterest(front.getTorrentPeer(), front);				
			}
		}
		if(message.getType() == TorrentMessage.SIGN_HAVE) {
			int index = ((MessageHave)message).getIndex();
			BitField stocked = peer.getTorrentData().getStockedDataInfo();
			if(!stocked.isOn(index)) {
				front.getTaskManager().startInterest(front.getTorrentPeer(), front);
			}
		}
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

	public void chokeStrategy(TorrentClient client) {
		TorrentClientFrontManager manager = client.getTorrentPeerManager();
		LinkedList<TorrentClientFront> unchokeList = new LinkedList<TorrentClientFront>();
		LinkedList<TorrentClientFront> chokeList = new LinkedList<TorrentClientFront>();

		colllectUnchøkeList(client, unchokeList, chokeList);
		cutUnchokeList(client, unchokeList);
		appendUnchokeList(client, unchokeList, chokeList);
	}

	private void colllectUnchøkeList(TorrentClient client, LinkedList unchokeList, LinkedList chokeList) {
		TorrentClientFrontManager manager = client.getTorrentPeerManager();
		int num = manager.numOfFront();
		for(int i=0;i<num;i++) {
			TorrentClientFront front = manager.getTorrentFront(i);
			TorrentClientFrontTargetInfo targetInfo = front.getTargetInfo();
			TorrentFrontMyInfo ownInfo = front.getMyInfo();
			if(TorrentClientFront.FALSE == ownInfo.isChoked()) {
				unchokeList.add(front);
			} else {
				chokeList.add(front);
			}
		}
	}

	private void cutUnchokeList(TorrentClient client, LinkedList<TorrentClientFront> unchokeList) {
		TorrentClientSetting setting = client.getSetting();
		int maxOfUnchoke = setting.getNumOfUnchoker();
		if(unchokeList.size()<maxOfUnchoke) {return;}

		// cut target noninterest
		for(int i=0;i<unchokeList.size();) {
			TorrentClientFront front = unchokeList.get(i);
			if(front.getTargetInfo().isInterested()) {
				unchokeList.remove(front);
			} else {
				i++;
			}
		}
		if(unchokeList.size()<maxOfUnchoke) {return;}

		// cut bad performance 
		TorrentClientFront front = unchokeList.get(0);
		for(int i=1;i<unchokeList.size();i++) {
			TorrentClientFront tmp = unchokeList.get(i);
			if(front.getTargetInfo().getFrontReuqstedTime() > tmp.getTargetInfo().getFrontReuqstedTime()) {
				front = tmp;
			}
		}
		unchokeList.remove(front);
	}

	private void appendUnchokeList(TorrentClient client, LinkedList<TorrentClientFront> unchokeList, LinkedList<TorrentClientFront> chokeList) {
		TorrentClientSetting setting = client.getSetting();
		
		Random r = new Random(System.currentTimeMillis());
		while(unchokeList.size()<setting.getNumOfUnchoker()&&0<chokeList.size()){
			int next = r.nextInt()%chokeList.size();
			TorrentClientFront nf = chokeList.remove(next);
			unchokeList.add(nf);
		}
	}
	
}
