package net.hetimatan.net.torrent.client.senario;

import java.io.IOException;

import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.client.TorrentData;
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
			front.onReceiveMessage(mCurrentMessage.getMessageNull());
		}

		if(reader.length() > reader.getFilePointer()) {
			if(!peer.getClientRunner().contains(front.getTaskManager().mReceiverTask)) {
				peer.getClientRunner().pushTask(front.getTaskManager().mReceiverTask);
			}			
		}
	}
/*
	public void onReceiveMessage(TorrentFront front, MessageNull nullMessage) throws IOException {
		if(Log.ON){Log.v(TorrentFront.TAG, "["+front.mDebug+"]"+"distribute:"+nullMessage.getSign()+":"+nullMessage.getMessageLength());}
		TorrentMessage message = null;
		switch(nullMessage.getSign()) {
		case TorrentMessage.SIGN_CHOKE:
			mTargetInfo.isChoke(true);
			message = MessageChoke.decode(mReader);
			break;
		case TorrentMessage.SIGN_UNCHOKE:
			mTargetInfo.isChoke(false);
			message = MessageUnchoke.decode(mReader);
			break;
		case TorrentMessage.SIGN_INTERESTED:
			mTargetInfo.mTargetInterested = true;
			message = MessageInterested.decode(mReader);
			break;
		case TorrentMessage.SIGN_NOTINTERESTED:
			mTargetInfo.mTargetInterested = false;
			message = MessageNotInterested.decode(mReader);
			break;
		case TorrentMessage.SIGN_HAVE:
			MessageHave have = MessageHave.decode(mReader);
			mTargetInfo.mTargetBitField.isOn(have.getIndex());
			message = have;
			break;
		case TorrentMessage.SIGN_BITFIELD:
			MessageBitField bitfieldMS = MessageBitField.decode(mReader);
			mTargetInfo.mTargetBitField.setBitfield(bitfieldMS.getBitField().getBinary());
			message = bitfieldMS;
			break;
		case TorrentMessage.SIGN_REQUEST:
			MessageRequest request = MessageRequest.decode(mReader);
			message = request;
			mTargetInfo.request(
					request.getIndex(), request.getBegin(), 
					request.getBegin()+request.getLength());
			break;
		case TorrentMessage.SIGN_PIECE:
			MessagePiece piece = MessagePiece.decode(mReader);
			message = piece;
			{
				TorrentClient peer = mTorrentPeer.get();
				if(peer == null) {return;}
				TorrentData data = peer.getTorrentData();
				data.setPiece(piece.getIndex(), piece.getCotent());
				peer.addDownloaded((int)piece.getCotent().length());
			}
			{
				if(mRequestPiece == piece.getIndex()) {
					mRequestPiece = -1;
				}
			}
			break;
		case TorrentMessage.SIGN_CANCEL:
			MessageCancel cancel = MessageCancel.decode(mReader);
			message = cancel;
			break;
		default:
			message = MessageNull.decode(mReader);
			break;
		}

		TorrentClient peer = mTorrentPeer.get();
		if(peer != null) {
			TorrentHistory.get().pushReceive(this, message);
		}

		if (null != message) {
			dispatch(message);
			mLastMessage = message;
		}
	}
*/

}
