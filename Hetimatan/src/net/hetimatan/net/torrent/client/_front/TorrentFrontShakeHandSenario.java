package net.hetimatan.net.torrent.client._front;

import java.io.IOException;

import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.client.TorrentFront;
import net.hetimatan.net.torrent.client.TorrentHistory;
import net.hetimatan.net.torrent.client.message.HelperLookAheadShakehand;
import net.hetimatan.net.torrent.client.message.MessageHandShake;
import net.hetimatan.util.log.Log;
import net.hetimatan.util.url.PercentEncoder;

//
// 各機能は、個々のクラスに委譲する予定
// このクラスもそのひとつ
//
// Tracker
// HandShake
// Request/Piece
// 
//
public class TorrentFrontShakeHandSenario {
	private HelperLookAheadShakehand mCurrentSHHelper = null;

	private HelperLookAheadShakehand getHelper(MarkableReader reader) throws IOException {
		if(mCurrentSHHelper == null) {
			TorrentHistory.get().pushMessage("[receive start]\n");
			mCurrentSHHelper = new HelperLookAheadShakehand(reader.getFilePointer(), reader);
		}
		return mCurrentSHHelper;
	}

	public boolean parseableShakehand(TorrentFront front) throws IOException {
		String TAG = TorrentFront.TAG;
		String DEBUG = front.mDebug;
		MarkableReader reader = front.getReader();

		if(Log.ON){Log.v(TAG, "["+DEBUG+"]"+"TorrentFront#revieceSH()");}
		HelperLookAheadShakehand currentSHHelper = getHelper(reader);
		currentSHHelper.read();

		if(reader.isEOF()){ front.close(); return true;}
		if(currentSHHelper.isEnd()) {
			return true;
		} else {
			return false;
		}
	}


	public void revcShakehand(TorrentFront front) throws IOException {
		String TAG = TorrentFront.TAG;
		String DEBUG = front.mDebug;
		MarkableReader reader = front.getReader();

		if(Log.ON){Log.v(TAG, "["+DEBUG+"]"+"TorrentFrontTask#shakehand");}
		try {
			MessageHandShake recv = MessageHandShake.decode(reader);
			TorrentHistory.get().pushReceive(front, recv);
			{
				TorrentClient peer = front.getTorrentPeer();
				PercentEncoder encoder = new PercentEncoder();
				if (peer.getPeerId().equals(encoder.encode(recv.getPeerId()))) {	
					throw new IOException();
				}
			}
		} finally {
			Log.v(TAG, "/TorrentFrontTask#shakehand");
		}
	}

	public void sendShakehand(TorrentFront front) throws IOException {
		String TAG = TorrentFront.TAG;
		String DEBUG = front.mDebug;
		if(Log.ON){Log.v(TAG, "["+DEBUG+"]"+"TorrentFrontTask#sendShakehand");}

		PercentEncoder encoder = new PercentEncoder();
		TorrentClient torentPeer = front.getTorrentPeer();
		byte[] infoHash = encoder.decode(torentPeer.getInfoHash().getBytes());
		byte[] peerId = encoder.decode(torentPeer.getPeerId().getBytes());
		MessageHandShake send = new MessageHandShake(infoHash, peerId);
		TorrentHistory.get().pushSend(front, send);
		send.encode(front.getSendCash().getLastOutput());
		front.pushflushSendTask();
	}

}
