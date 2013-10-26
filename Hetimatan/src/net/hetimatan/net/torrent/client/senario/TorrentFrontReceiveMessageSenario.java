package net.hetimatan.net.torrent.client.senario;

import java.io.IOException;

import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.client.TorrentFront;
import net.hetimatan.net.torrent.client.TorrentHistory;
import net.hetimatan.net.torrent.client.message.HelperLookAheadMessage;
import net.hetimatan.util.log.Log;

public class TorrentFrontReceiveMessageSenario {

	private HelperLookAheadMessage mCurrentMessage = null;
	//
	// -1 eof
	//  0 parseable
	//  1 end
	public int parseableMessage(TorrentFront front) throws IOException {
		if(Log.ON){Log.v(TorrentFront.TAG, "["+front.mDebug+"]"+"TorrentFront#parseableMessage()");}
		MarkableReader reader = front.getReader();
		if(mCurrentMessage == null) {
			TorrentHistory.get().pushMessage("[receive start]\n");
			mCurrentMessage = new HelperLookAheadMessage();
		}
		boolean isEnd = mCurrentMessage.lookahead(reader);
		if(isEnd) {return 0;}
		else if(reader.isEOF()){ return -1;}
		else{return 1;}
	}

	public void receive(TorrentFront front) throws IOException {
		MarkableReader reader = front.getReader();
		TorrentClient peer = front.getTorrentPeer();
		int parseable = parseableMessage(front);
		if(parseable == -1) {
			front.close();
		} else if(parseable == 0) {
			TorrentHistory.get().pushMessage("[receive end]\n");
			front.onReceiveMessage(mCurrentMessage.getMessageNull());
		}

		if(reader.length() > reader.getFilePointer()) {
			if(!peer.getClientRunner().contains(front.getTaskManager().mReceiverTask)) {
				peer.getClientRunner().pushTask(front.getTaskManager().mReceiverTask);
			}			
		}
	}

}
