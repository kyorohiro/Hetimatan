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

/**
 * - how to use
 * sendShakehand();
 * while(!parseableShakehand()) {};
 * rescShakehand();
 *
 * - EventTask
 * TorrentFrontShakeHandTask
 */
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
		if(Log.ON){Log.v(TorrentFront.TAG, "["+front.mDebug+"]"+"TorrentFront#revieceSH()");}
		MarkableReader reader = front.getReader();
		HelperLookAheadShakehand currentSHHelper = getHelper(reader);
		currentSHHelper.read();

		if(reader.isEOF()){ front.close(); return true;}
		if(currentSHHelper.parseable()) {return true;} else {return false;}
	}

	public void revcShakehand(TorrentFront front) throws IOException {
		if(Log.ON){Log.v(TorrentFront.TAG, "["+front.mDebug+"]"+"TorrentFrontTask#shakehand");}

		MarkableReader reader = front.getReader();
		MessageHandShake recv = MessageHandShake.decode(reader);
		TorrentHistory.get().pushReceive(front, recv);
		TorrentClient peer = front.getTorrentPeer();
		PercentEncoder encoder = new PercentEncoder();
		if (peer.getPeerId().equals(encoder.encode(recv.getPeerId()))) {	
			throw new IOException();
		}
	}

	public void sendShakehand(TorrentFront front) throws IOException {
		if(Log.ON){Log.v(TorrentFront.TAG, "["+front.mDebug+"]"+"TorrentFrontTask#sendShakehand");}
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
