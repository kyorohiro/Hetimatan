package net.hetimatan.net.torrent.client.senario;

import java.io.IOException;

import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.client.TorrentFront;
import net.hetimatan.net.torrent.client.TorrentHistory;
import net.hetimatan.net.torrent.client.message.HelperLookAheadMessage;
import net.hetimatan.net.torrent.client.message.MessageBitField;
import net.hetimatan.net.torrent.client.message.MessageCancel;
import net.hetimatan.net.torrent.client.message.MessageChoke;
import net.hetimatan.net.torrent.client.message.MessageHave;
import net.hetimatan.net.torrent.client.message.MessageInterested;
import net.hetimatan.net.torrent.client.message.MessageNotInterested;
import net.hetimatan.net.torrent.client.message.MessageNull;
import net.hetimatan.net.torrent.client.message.MessagePiece;
import net.hetimatan.net.torrent.client.message.MessageRequest;
import net.hetimatan.net.torrent.client.message.MessageUnchoke;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
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
			onReceiveMessage(front, mCurrentMessage.getMessageNull());
		}

		if(reader.length() > reader.getFilePointer()) {
			if(!peer.getClientRunner().contains(front.getTaskManager().mReceiverTask)) {
				peer.getClientRunner().pushTask(front.getTaskManager().mReceiverTask);
			}			
		}
	}

	public void onReceiveMessage(TorrentFront front, MessageNull nullMessage) throws IOException {
		if(Log.ON){Log.v(TorrentFront.TAG, "["+front.mDebug+"]"+"distribute:"+nullMessage.getSign()+":"+nullMessage.getMessageLength());}
		MarkableReader reader = front.getReader();
		TorrentMessage message = null;
		switch(nullMessage.getSign()) {
		case TorrentMessage.SIGN_CHOKE:
			message = MessageChoke.decode(reader);
			break;
		case TorrentMessage.SIGN_UNCHOKE:
			message = MessageUnchoke.decode(reader);
			break;
		case TorrentMessage.SIGN_INTERESTED:
			message = MessageInterested.decode(reader);
			break;
		case TorrentMessage.SIGN_NOTINTERESTED:
			message = MessageNotInterested.decode(reader);
			break;
		case TorrentMessage.SIGN_HAVE:
			MessageHave have = MessageHave.decode(reader);
			message = have;
			break;
		case TorrentMessage.SIGN_BITFIELD:
			MessageBitField bitfieldMS = MessageBitField.decode(reader);
			message = bitfieldMS;
			break;
		case TorrentMessage.SIGN_REQUEST:
			MessageRequest request = MessageRequest.decode(reader);
			message = request;
			break;
		case TorrentMessage.SIGN_PIECE:
			MessagePiece piece = MessagePiece.decode(reader);
			message = piece;
			break;
		case TorrentMessage.SIGN_CANCEL:
			MessageCancel cancel = MessageCancel.decode(reader);
			message = cancel;
			break;
		default:
			message = MessageNull.decode(reader);
			break;
		}

		front.onReceiveMessage(message);
	}


}
