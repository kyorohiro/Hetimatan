package net.hetimatan.net.torrent.client._front;

import java.io.IOException;
import java.lang.ref.WeakReference;

import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.io.net.KyoroSelector;
import net.hetimatan.io.net.KyoroSocket;
import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.client.TorrentClientFront;
import net.hetimatan.net.torrent.client.TorrentHistory;
import net.hetimatan.net.torrent.client.message.HelperLookAheadShakehand;
import net.hetimatan.net.torrent.client.message.MessageHandShake;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;
import net.hetimatan.util.log.Log;
import net.hetimatan.util.url.PercentEncoder;

/**
 * delegate shakehand task from TorrentClientFront
 * - how to use
 * sendShakehand();
 * while(!parseableShakehand()) {};
 * rescShakehand();
 *
 * - EventTask
 * TorrentFrontShakeHandTask
 */
public class TorrentFrontShakeHand {
	private HelperLookAheadShakehand mCurrentSHHelper = null;
	private boolean mReceivedShakehand = false;
	private boolean mSendShakehand = false;

	private HelperLookAheadShakehand getHelper(MarkableReader reader) throws IOException {
		if(mCurrentSHHelper == null) {
			TorrentHistory.get().pushMessage("[receive start]\n");
			mCurrentSHHelper = new HelperLookAheadShakehand(reader.getFilePointer(), reader);
		}
		return mCurrentSHHelper;
	}

	public boolean isShakehanded() {
		return (mReceivedShakehand&mSendShakehand);
	}

	public boolean parseableShakehand(TorrentClientFront front) throws IOException {
		if(Log.ON){Log.v(TorrentClientFront.TAG, "["+front.mDebug+"]"+"TorrentFront#revieceSH()");}
		MarkableReader reader = front.getReader();
		HelperLookAheadShakehand currentSHHelper = getHelper(reader);
		currentSHHelper.read();

		if(reader.isEOF()){ front.close(); return true;}
		if(currentSHHelper.parseable()) {return true;} else {return false;}
	}

	public void revcShakehand(TorrentClientFront front) throws IOException {
		if(Log.ON){Log.v(TorrentClientFront.TAG, "["+front.mDebug+"]"+"TorrentFrontTask#shakehand");}

		MarkableReader reader = front.getReader();
		MessageHandShake recv = MessageHandShake.decode(reader);
		TorrentHistory.get().pushReceive(front, recv);
		TorrentClient peer = front.getTorrentPeer();
		PercentEncoder encoder = new PercentEncoder();
		if (peer.getPeerId().equals(encoder.encode(recv.getPeerId()))) {	
			throw new IOException();
		}
		mReceivedShakehand = true;
	}

	public void sendShakehand(TorrentClientFront front) throws IOException {
		if(Log.ON){Log.v(TorrentClientFront.TAG, "["+front.mDebug+"]"+"TorrentFrontTask#sendShakehand");}
		PercentEncoder encoder = new PercentEncoder();
		TorrentClient torentPeer = front.getTorrentPeer();
		byte[] infoHash = encoder.decode(torentPeer.getInfoHash().getBytes());
		byte[] peerId = encoder.decode(torentPeer.getPeerId().getBytes());
		MessageHandShake send = new MessageHandShake(infoHash, peerId);
		TorrentHistory.get().pushSend(front, send);
		send.encode(front.getSendCash().getLastOutput());
		front.pushflushSendTask();
		mSendShakehand = true;
	}

	public static class TorrentFrontShakeHandTask extends EventTask {
		public static final String TAG = "TorrentFrontShakeHandTask";
		private WeakReference<TorrentClientFront> mTorrentFront = null;
		private boolean mIsFirst = true;
		private boolean mIsKeep = true;
		private boolean mIsNext = true;

		public TorrentFrontShakeHandTask(TorrentClientFront front) {
			mTorrentFront = new WeakReference<TorrentClientFront>(front);
		}

		@Override
		public String toString() {
			return TAG;
		}

		@Override
		public boolean isKeep() {
			return mIsKeep;
		}

		@Override
		public boolean isNext() {
			return mIsNext;
		}

		@Override
		public void action(EventTaskRunner runner) throws Throwable {
			TorrentClientFront front = mTorrentFront.get();

			TorrentClient peer = front.getTorrentPeer();
			KyoroSocket mSocket = front.getSocket();
			if(mIsFirst) {
				front.sendShakehand();
				front.sendBitfield();
				mSocket.regist(peer.getSelector(), KyoroSelector.READ);
				mSocket.setEventTaskAtWrakReference(this, KyoroSelector.READ);
				mIsFirst = false;
				// kick from socket event task
				mIsKeep = false;
				mIsNext = false;
			} else {
				if(front.parseableShakehand()) {
					mSocket.regist(peer.getSelector(), KyoroSelector.READ);
					mSocket.setEventTaskAtWrakReference(null,KyoroSelector.READ);
					front.revcShakehand();
					mIsKeep = false;
					mIsNext = true;
				} else {
					mSocket.regist(peer.getSelector(), KyoroSelector.READ);
					mSocket.setEventTaskAtWrakReference(this,KyoroSelector.READ);
					// kick from socket event task
					mIsKeep = false;
					mIsNext = false;
				}
			}
		}
	}
}
