package net.hetimatan.net.torrent.client._front;

import java.io.IOException;

import net.hetimatan.io.net.KyoroSelector;
import net.hetimatan.net.torrent.client.TorrentFront;
import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.client.senario.TorrentFrontReceiveMessageSenario.TorrentFrontReceiverTask;
import net.hetimatan.net.torrent.client.task.TorrentFrontChokerTask;
import net.hetimatan.net.torrent.client.task.TorrentFrontCloseTask;
import net.hetimatan.net.torrent.client.task.TorrentFrontConnectionTask;
import net.hetimatan.net.torrent.client.task.TorrentFrontFirstAction;
import net.hetimatan.net.torrent.client.task.TorrentFrontHaveTask;
import net.hetimatan.net.torrent.client.task.TorrentFrontInterestTask;
import net.hetimatan.net.torrent.client.task.TorrentFrontNotInterestTask;
import net.hetimatan.net.torrent.client.task.TorrentFrontRequestTask;
import net.hetimatan.net.torrent.client.task.TorrentFrontShakeHandTask;
import net.hetimatan.util.event.EventTaskRunner;
import net.hetimatan.util.event.net.MessageSendTask;
import net.hetimatan.util.log.Log;

public class TorrentFrontTaskManager {
	public static final String TAG = "TorrentFrontTask";

	private TorrentFrontShakeHandTask mStartTask = null;
	public TorrentFrontReceiverTask mReceiverTask = null; //todo
	private TorrentFrontConnectionTask mConnection = null;
	private TorrentFrontCloseTask mCloseTask = null;
	private TorrentFrontInterestTask mInterestTask = null;
	private TorrentFrontNotInterestTask mNotInterestTask = null;
	private TorrentFrontRequestTask mRequestTask = null;
	private TorrentFrontChokerTask mChokerTask = null;
	private TorrentFrontHaveTask mHaveTask = null;
	private TorrentFrontFirstAction mFirstAction = null;
	private MessageSendTask mSendTaskChain = null;


	public void startSendTask(TorrentClient peer, TorrentFront front) {
		 if(mSendTaskChain == null) {
			 mSendTaskChain = new MessageSendTask(front.getSocket(), front.getSendCash());
		 }

		 if(!peer.getClientRunner().contains(mSendTaskChain)) {
			 peer.getClientRunner().pushTask(mSendTaskChain);
		 }
	}

	public void flushSendTask(TorrentClient peer) throws IOException  {
		try {
			if(peer.getClientRunner().contains(mSendTaskChain)) {
				do {
					mSendTaskChain.action(peer.getClientRunner());
				} while(mSendTaskChain.isKeep());
				peer.getClientRunner().releaseTask(mSendTaskChain);
			}
		} catch(Throwable t) {
			throw new IOException("");
		}
	}

	public void startConnectForAccept(TorrentClient peer, TorrentFront front) {
		if(Log.ON){Log.v(TAG, "["+front.getDebug()+"]"+"start accept task");}
		EventTaskRunner runner = peer.getClientRunner();
		mStartTask = new TorrentFrontShakeHandTask(front);
		mFirstAction = new TorrentFrontFirstAction(front);
		if(mCloseTask == null) {
			mCloseTask = new TorrentFrontCloseTask(front);
		}
		mStartTask.nextAction(mFirstAction);
		mStartTask.errorAction(mCloseTask);
		runner.pushTask(mStartTask);
	}

	public void startConnect(TorrentClient peer, TorrentFront front, String host, int port) throws IOException {
		if(Log.ON){Log.v(TAG, "["+front.getDebug()+"]"+"start connection task");}
		mConnection = new TorrentFrontConnectionTask(front, host, port);
		mStartTask = new TorrentFrontShakeHandTask(front);
		mFirstAction = new TorrentFrontFirstAction(front);
		if(mCloseTask == null) {
			mCloseTask = new TorrentFrontCloseTask(front);
		}
		mConnection.nextAction(mStartTask);
		mStartTask.nextAction(mFirstAction);
		mConnection.errorAction(mCloseTask);
		mStartTask.errorAction(mCloseTask);
		peer.getClientRunner().start(mConnection);
	}

	public void startReceliver(TorrentClient peer, TorrentFront front) throws IOException {
		if(Log.ON){Log.v(TAG, "["+front.getDebug()+"]"+"start receiver");}
		if(peer == null) {return;}
		EventTaskRunner runner = peer.getClientRunner();
		mReceiverTask = new TorrentFrontReceiverTask(front);
		mCloseTask = new TorrentFrontCloseTask(front);
		mReceiverTask.errorAction(mCloseTask);
		peer.getSelector().wakeup();
		front.getSocket().regist(peer.getSelector(), KyoroSelector.READ);
		front.getSocket().setEventTaskAtWrakReference(mReceiverTask, KyoroSelector.READ);
		runner.pushTask(mReceiverTask);
	}

	public void startInterest(TorrentClient peer, TorrentFront front) {
		if(Log.ON){Log.v(TAG, "["+front.getDebug()+"]"+"start interest");}
		if(peer == null) {return;}
		if(front.getMyInfo().mInterest == true) {return;}
		if(mInterestTask == null) {
			mInterestTask = new TorrentFrontInterestTask(front);
		}
		if(mCloseTask == null) {
			mCloseTask = new TorrentFrontCloseTask(front);
		}
		mInterestTask.errorAction(mCloseTask);
		peer.getClientRunner().pushTask(mInterestTask);
	}

	public void startNotInterest(TorrentClient peer, TorrentFront front) {
		if(Log.ON){Log.v(TAG, "["+front.getDebug()+"]"+"start notinterest");}
		if(peer == null) {return;}
		if(front.getMyInfo().mInterest == false) {return;}
		if(mNotInterestTask == null) {
			mNotInterestTask = new TorrentFrontNotInterestTask(front);
		}
		if(mCloseTask == null) {
			mCloseTask = new TorrentFrontCloseTask(front);
		}
		mNotInterestTask.errorAction(mCloseTask);
		peer.getClientRunner().pushTask(mNotInterestTask);
	}

	public void startDownload(TorrentClient peer, TorrentFront front) throws IOException {
		if(Log.ON){Log.v(TAG, "["+front.mDebug+"]"+"start Download");}
		if(peer == null) {return;}
		if(peer.isSeeder()){return;}
		if(front.getTargetInfo().isChoked() != TorrentFront.FALSE){return;}
		if(Log.ON){Log.v(TAG, "["+front.getDebug()+"]"+"startDownload");}
		mRequestTask = new TorrentFrontRequestTask(front);
		if(mCloseTask == null) {
			mCloseTask = new TorrentFrontCloseTask(front);
		}
		mRequestTask.errorAction(mCloseTask);
		peer.getClientRunner().pushTask(mRequestTask);
	}

	public void startChoker(TorrentClient peer, TorrentFront front, boolean isChoke) throws IOException {
		if(Log.ON){Log.v(TAG, "["+front.mDebug+"]"+"start Choker:" +isChoke);}
		if(peer == null) {return;}
		if(mChokerTask == null) {
			mChokerTask = new TorrentFrontChokerTask(front, isChoke);
		}
		if(mCloseTask == null) {
			mCloseTask = new TorrentFrontCloseTask(front);
		}
		mChokerTask.errorAction(mCloseTask);
		mChokerTask.isChoke(isChoke);
		peer.getClientRunner().pushTask(mChokerTask);
	}

	public void startHave(TorrentClient peer, TorrentFront front, int index) throws IOException {
		if(Log.ON){Log.v(TAG, "["+front.mDebug+"]"+"start Have");}
		if(peer == null) {return;}
		mHaveTask = new TorrentFrontHaveTask(front, index);
		if(mCloseTask == null) {
			mCloseTask = new TorrentFrontCloseTask(front);
		}
		mHaveTask.errorAction(mCloseTask);
		peer.getClientRunner().pushTask(mHaveTask);
	}

}
