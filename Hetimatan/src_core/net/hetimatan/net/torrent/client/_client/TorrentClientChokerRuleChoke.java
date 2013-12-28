package net.hetimatan.net.torrent.client._client;

import java.io.IOException;
import java.util.Currency;
import java.util.LinkedList;
import java.util.Random;

import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.client.TorrentClientFront;
import net.hetimatan.net.torrent.client.TorrentClientListener;
import net.hetimatan.net.torrent.client.TorrentClientSetting;
import net.hetimatan.net.torrent.client._front.TorrentClientFrontTargetInfo;
import net.hetimatan.net.torrent.client._front.TorrentFrontMyInfo;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.net.torrent.tracker.TrackerClient;

public class TorrentClientChokerRuleChoke implements TorrentClientListener {

	public void choke(TorrentClient client) {
		TorrentClientFrontManager manager = client.getTorrentPeerManager();
		LinkedList<TorrentClientFront> unchokeList = new LinkedList<TorrentClientFront>();
		LinkedList<TorrentClientFront> chokeList = new LinkedList<TorrentClientFront>();

		colllectUnchøkeList(client, unchokeList, chokeList);
		LinkedList<TorrentClientFront> cutted = cutUnchokeList(client, unchokeList);
		LinkedList<TorrentClientFront> append = appendUnchokeList(client, unchokeList, chokeList);
		updateStatus(client, cutted, append);
	}

	public void add(TorrentClientFront front) {
		if(front == null) {return;}
		TorrentClient client = front.getTorrentPeer();
		if(client == null) {return;}
		TorrentClientFrontManager manager = client.getTorrentPeerManager();
		TorrentClientSetting setting = client.getSetting();

		if (manager.numOfFront() < setting.getNumOfUnchoker()) {
			choke(client);
		}		
	}

	private void updateStatus(TorrentClient client, LinkedList<TorrentClientFront> cutted, LinkedList<TorrentClientFront> append) {
		//
		// rm duplicate status
		for(int i=0;i<append.size();i++) {
			for(int j=0;j<cutted.size();i++) {
				if(cutted.get(j) == append.get(i)) {
					cutted.remove(i);
					break;
				}
			}
		}
		//
		//
		for(TorrentClientFront front : cutted) {
			try {
				front.sendChoke();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		for(TorrentClientFront front : cutted) {
			try {
				front.sendUncoke();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
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

	private LinkedList<TorrentClientFront> cutUnchokeList(TorrentClient client, LinkedList<TorrentClientFront> unchokeList) {
		TorrentClientSetting setting = client.getSetting();
		LinkedList<TorrentClientFront> ret = new LinkedList<>();
		int maxOfUnchoke = setting.getNumOfUnchoker();
		if(unchokeList.size()<maxOfUnchoke) {return ret;}

		// cut target noninterest
		for(int i=0;i<unchokeList.size();) {
			TorrentClientFront front = unchokeList.get(i);
			if(front.getTargetInfo().isInterested()) {
				unchokeList.remove(front);
				ret.add(front);
			} else {
				i++;
			}
		}
		if(unchokeList.size()<maxOfUnchoke) {return ret;}

		// cut bad performance 
		TorrentClientFront front = unchokeList.get(0);
		for(int i=1;i<unchokeList.size();i++) {
			TorrentClientFront tmp = unchokeList.get(i);
			if(front.getTargetInfo().getFrontReuqstedTime() > tmp.getTargetInfo().getFrontReuqstedTime()) {
				front = tmp;
			}
		}
		unchokeList.remove(front);
		ret.add(front);
		return ret;
	}

	private LinkedList<TorrentClientFront> appendUnchokeList(TorrentClient client, LinkedList<TorrentClientFront> unchokeList, LinkedList<TorrentClientFront> chokeList) {
		TorrentClientSetting setting = client.getSetting();
		LinkedList<TorrentClientFront>  ret  = new LinkedList<>();
		Random r = new Random(System.currentTimeMillis());
		while(unchokeList.size()<setting.getNumOfUnchoker()&&0<chokeList.size()){
			int next = r.nextInt()%chokeList.size();
			TorrentClientFront nf = chokeList.remove(next);
			unchokeList.add(nf);
			ret.add(nf);
		}
		return ret;
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
		add(front);
	}

	@Override
	public void onSendMessage(TorrentClientFront front, TorrentMessage message) throws IOException {
	}

	@Override
	public void onReceiveMessage(TorrentClientFront front, TorrentMessage message) throws IOException {
	}

	@Override
	public void onResponsePeerList(TorrentClient client, TrackerClient tracker)  throws IOException {
	}

	@Override
	public void onInterval(TorrentClient client) {
		choke(client);
	}

}
