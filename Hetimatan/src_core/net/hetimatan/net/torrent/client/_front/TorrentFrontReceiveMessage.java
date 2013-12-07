package net.hetimatan.net.torrent.client._front;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;

import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.client.TorrentClientFront;
import net.hetimatan.net.torrent.client.TorrentClientListener;
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
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;
import net.hetimatan.util.log.Log;

//
//もともとぬ、TorrentFront、TorrentClientにあった機能を
//機能ごとに別のクラスに委譲したい。
//
//このクラスもその候補
//まだ、メソッドだけ抜き出した状態
//
//
public class TorrentFrontReceiveMessage {

	private HelperLookAheadMessage mCurrentMessage = null;

	// -1 eof
	//  0 parseable
	//  1 end
	public int parseableMessage(TorrentClientFront front) throws IOException {
		if(Log.ON){Log.v(TorrentClientFront.TAG, "["+front.mDebug+"]"+"TorrentFront#parseableMessage()");}
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

	public void receive(TorrentClientFront front) throws IOException {
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

	public void onReceiveMessage(TorrentClientFront front, MessageNull nullMessage) throws IOException {
		if(Log.ON){Log.v(TorrentClientFront.TAG, "["+front.mDebug+"]"+"distribute:"+nullMessage.getSign()+":"+nullMessage.getMessageLength());}
		MarkableReader reader = front.getReader();
		TorrentMessage message = nullMessage2TorrentMessage(reader, nullMessage);
		front.getTorrentPeer().getDispatcher().dispatchTorrentMessage(front, message);
	}


	private TorrentMessage nullMessage2TorrentMessage(MarkableReader reader, MessageNull nullMessage) throws IOException {
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
		return message;
	}

	// ------------------------------------------------
	//
	// ------------------------------------------------
/*
	private LinkedList<WeakReference<TorrentClientListener>> mObservers = new LinkedList<WeakReference<TorrentClientListener>>();
	public void addObserverAtWeak(TorrentClientListener observer) {
		mObservers.add(new WeakReference<TorrentClientListener>(observer));
	}

	public void dispatch(TorrentClientFront front, TorrentMessage message) throws IOException {
		front.onReceiveMessage(message);
		Iterator<WeakReference<TorrentClientListener>>	ite = mObservers.iterator();
		while(ite.hasNext()) {
			WeakReference<TorrentClientListener> observerref = ite.next();
			TorrentClientListener observer = observerref.get();
			if(null == observerref.get()) {
				mObservers.remove(observerref);
			}
			observer.onReceiveMessage(front, message);
		}
	}
*/

	public static class TorrentFrontReceiverTask extends EventTask {

		public static final String TAG  = "TorrentFrontReceiverTask";
		private WeakReference<TorrentClientFront> mTorrentFront = null;

		public TorrentFrontReceiverTask(TorrentClientFront front) {
			mTorrentFront = new WeakReference<TorrentClientFront>(front);
		}

		@Override
		public String toString() {
			return TAG;
		}

		@Override
		public void action(EventTaskRunner runner) throws Throwable {
			TorrentClientFront front = mTorrentFront.get();
			front.receive();
		}
	}

}
