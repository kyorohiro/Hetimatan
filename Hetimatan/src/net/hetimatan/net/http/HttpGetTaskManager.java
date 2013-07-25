package net.hetimatan.net.http;

import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.net.MessageSendTask;

public class HttpGetTaskManager {
	private MessageSendTask mSendTaskChain = null;
	public EventTask mLast = null;
	public void startSendTask(HttpGet peer) {
		// if(mSendTaskChain == null) {
			 mSendTaskChain = new MessageSendTask(peer.getSocket(), peer.getSendCash());
		 //}

		 if(!peer.getRunner().contains(mSendTaskChain)) {
			 peer.getRunner().pushWork(mSendTaskChain);
		 }
	}

	public void nextTask(EventTask task) {
		mSendTaskChain.nextAction(task);
	}
}
