package net.hetimatan.net.http;

import net.hetimatan.util.net.MessageSendTask;

public class HttpTaskManager {
	private MessageSendTask mSendTaskChain = null;


	public void startSendTask(HttpGet peer) {
		 if(mSendTaskChain == null) {
			 mSendTaskChain = new MessageSendTask(peer.getRunner(), peer.getSocket(), peer.getSendCash());
		 }

		 if(!peer.getRunner().contains(mSendTaskChain)) {
			 peer.getRunner().pushWork(mSendTaskChain);
		 }
	}
}
