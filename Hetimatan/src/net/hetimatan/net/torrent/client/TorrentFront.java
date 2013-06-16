package net.hetimatan.net.torrent.client;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;

import net.hetimatan.io.file.KyoroFile;
import net.hetimatan.io.file.KyoroFileForKyoroSocket;
import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.io.net.KyoroSelector;
import net.hetimatan.io.net.KyoroSocket;
import net.hetimatan.io.net.KyoroSocketOutputStream;
import net.hetimatan.net.torrent.client._front.TorrentFrontMyInfo;
import net.hetimatan.net.torrent.client._front.TorrentFrontTargetInfo;
import net.hetimatan.net.torrent.client.message.HelperLookAheadMessage;
import net.hetimatan.net.torrent.client.message.MessageBitField;
import net.hetimatan.net.torrent.client.message.MessageCancel;
import net.hetimatan.net.torrent.client.message.MessageChoke;
import net.hetimatan.net.torrent.client.message.MessageHandShake;
import net.hetimatan.net.torrent.client.message.MessageHave;
import net.hetimatan.net.torrent.client.message.MessageInterested;
import net.hetimatan.net.torrent.client.message.MessageKeepAlive;
import net.hetimatan.net.torrent.client.message.MessageNotInterested;
import net.hetimatan.net.torrent.client.message.MessageNull;
import net.hetimatan.net.torrent.client.message.MessagePiece;
import net.hetimatan.net.torrent.client.message.MessageRequest;
import net.hetimatan.net.torrent.client.message.MessageUnchoke;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.net.torrent.client.task.TorrentFrontChokerTask;
import net.hetimatan.net.torrent.client.task.TorrentFrontCloseTask;
import net.hetimatan.net.torrent.client.task.TorrentFrontConnectionTask;
import net.hetimatan.net.torrent.client.task.TorrentFrontInterestTask;
import net.hetimatan.net.torrent.client.task.TorrentFrontNotInterestTask;
import net.hetimatan.net.torrent.client.task.TorrentFrontReceiverTask;
import net.hetimatan.net.torrent.client.task.TorrentFrontRequestTask;
import net.hetimatan.net.torrent.client.task.TorrentFrontShakeHandTask;
import net.hetimatan.net.torrent.tracker.TrackerPeerInfo;
import net.hetimatan.net.torrent.util.piece.PieceInfo;
import net.hetimatan.util.bitfield.BitField;
import net.hetimatan.util.event.EventTaskRunner;
import net.hetimatan.util.log.Log;
import net.hetimatan.util.url.PercentEncoder;


public class TorrentFront {
	public static final String TAG = "TorrentFront";

	private MarkableReader mReader = null;
	private KyoroSocketOutputStream mOutput = null;
	private WeakReference<TorrentPeer> mTorrentPeer = null;
	private HelperLookAheadMessage mCurrentHelper = null;

	private TorrentFrontTargetInfo mTargetInfo = null;
	private TorrentFrontMyInfo mMyInfo = null;
	private KyoroSocket mSocket = null;

	// task
	private TorrentFrontShakeHandTask mStartTask = null;
	private TorrentFrontReceiverTask mReceiverTask = null;
	private TorrentFrontConnectionTask mConnection = null;
	private TorrentFrontCloseTask mCloseTask = null;
	private TorrentFrontInterestTask mInterestTask = null;
	private TorrentFrontNotInterestTask mNotInterestTask = null;
	private TorrentFrontRequestTask mRequestTask = null;
	
	private TrackerPeerInfo mPeer = null;

	public TorrentFront(TorrentPeer peer, KyoroSocket socket) throws IOException {
		mSocket = socket;
		mTargetInfo = new TorrentFrontTargetInfo(peer.getPieceLength());
		KyoroFileForKyoroSocket kf = new KyoroFileForKyoroSocket(socket, 512*30);
		kf.setSelector(peer.getSelector());
		mReader = new MarkableFileReader(kf, 512);
		mOutput = new KyoroSocketOutputStream(socket);
		mTorrentPeer = new WeakReference<TorrentPeer>(peer);
		mTargetInfo.mTargetBitField = new BitField(peer.getNumOfPieces());
		mTargetInfo.mTargetBitField.zeroClear();
		mMyInfo = new TorrentFrontMyInfo();
		mPeer = new TrackerPeerInfo(socket.getHost(), socket.getPort());
	}

	public void setPeer(TrackerPeerInfo peer) {
		mPeer = peer;
	}

	public TrackerPeerInfo getPeer() {
		return mPeer;
	}

	public BitField relativeBitfield() {
	 	TorrentPeer peer = mTorrentPeer.get();
		if(peer == null) {return mMyInfo.mRelative;}
		TorrentData data = peer.getTorrentData();
		BitField myInfo = data.getStockedDataInfo();
		if(mMyInfo.mRelative == null) {
			mMyInfo.mRelative = new BitField(myInfo.lengthPerBit());
		}
		BitField targetInfo = getTargetInfo().mTargetBitField;
		return BitField.relative(targetInfo, myInfo, mMyInfo.mRelative);
	}

	public KyoroSocket getSocket() {
		return mSocket;
	}

	public TorrentPeer getTorrentPeer() {
		return mTorrentPeer.get();
	}

	public TorrentFrontMyInfo getMyInfo() {
		return mMyInfo;
	}
	public TorrentFrontTargetInfo getTargetInfo() {
		return mTargetInfo;
	}

	public void startConnectForAccept() {
		if(Log.ON){Log.v(TAG, "start accept task");}
		EventTaskRunner runner = mTorrentPeer.get().getClientRunner();
		mStartTask = new TorrentFrontShakeHandTask(this, runner);
		runner.pushWork(mStartTask);
	}

	public void startConnect(String host, int port) throws IOException {
		if(Log.ON){Log.v(TAG, "start connection task");}
		mConnection = new TorrentFrontConnectionTask(this, mTorrentPeer.get().getClientRunner(), host, port);
		mStartTask = new TorrentFrontShakeHandTask(this, mTorrentPeer.get().getClientRunner());
		mConnection.nextAction(mStartTask);
		mTorrentPeer.get().getClientRunner().start(mConnection);
	}

	public void startReceliver() throws IOException {
		if(Log.ON){Log.v(TAG, "start receiver");}
		TorrentPeer peer = mTorrentPeer.get();
		if(peer == null) {return;}
		EventTaskRunner runner = peer.getClientRunner();
		mReceiverTask = new TorrentFrontReceiverTask(this, runner);
		mCloseTask = new TorrentFrontCloseTask(this, runner);
		mReceiverTask.errorAction(mCloseTask);
		peer.getSelector().wakeup();
		mSocket.regist(peer.getSelector(), KyoroSelector.READ);
		mSocket.setEventTaskAtWrakReference(mReceiverTask);
		runner.pushWork(mReceiverTask);
	}

	public void startInterest() {
		if(Log.ON){Log.v(TAG, "start interest");}
		TorrentPeer peer = mTorrentPeer.get();
		if(peer == null) {return;}
		if(mMyInfo.mInterest == true) {return;}
		if(mInterestTask == null) {
			mInterestTask = new TorrentFrontInterestTask(this, peer.getClientRunner());
		}
		peer.getClientRunner().pushWork(mInterestTask);
	}

	public void startNotInterest() {
		if(Log.ON){Log.v(TAG, "start notinterest");}
		TorrentPeer peer = mTorrentPeer.get();
		if(peer == null) {return;}
		if(mMyInfo.mInterest == false) {return;}
		if(mNotInterestTask == null) {
			mNotInterestTask = new TorrentFrontNotInterestTask(this, peer.getClientRunner());
		}
		peer.getClientRunner().pushWork(mNotInterestTask);
	}

	public void startDownload() throws IOException {
		TorrentPeer peer = mTorrentPeer.get();
		if(peer == null) {return;}
		if(peer.isSeeder()){return;}
		if(mTargetInfo.mTargetChoked){return;}
		if(Log.ON){Log.v(TAG, "startDownload");}
		mRequestTask = new TorrentFrontRequestTask(this, peer.getClientRunner());
		peer.getClientRunner().pushWork(mRequestTask);
	}

	private TorrentFrontChokerTask mChokerTask = null;
	public void startChoker(boolean isChoke) throws IOException {
		TorrentPeer peer = mTorrentPeer.get();
		if(peer == null) {return;}
		if(mChokerTask == null) {
			mChokerTask = new TorrentFrontChokerTask(this, peer.getClientRunner(), isChoke);
		}
		mChokerTask.isChoke(isChoke);
		peer.getClientRunner().pushWork(mChokerTask);
	}

	public void close() throws IOException {
		if(Log.ON){Log.v(TAG, "close");}
		TorrentPeer peer = mTorrentPeer.get();
		if(peer != null) {
			peer.removeTorrentFront(this);
		}
		if(mSocket != null) {	
			mSocket.close();
		}
	}

	public boolean isTargetComplete() {
		return mTargetInfo.mTargetBitField.isAllOn();
	}

	public void shakehand() throws IOException {
		if(Log.ON){Log.v(TAG, "TorrentFrontTask#shakehand");}
		try {
			mReader.setBlockOn(true);
			MessageHandShake recv = MessageHandShake.decode(mReader);
			recv.printLog();
		} finally {
			mReader.setBlockOn(false);
			Log.v(TAG, "/TorrentFrontTask#shakehand");
		}
	}

	public void connect(String hostname, int port) throws IOException {
		if(Log.ON){Log.v(TAG, "TorrentFront#connection() : "+hostname+ ":" +port);}
		mSocket.connect(hostname, port);
	}

	public boolean isConnect() throws IOException {
		int state = mSocket.getConnectionState();
		switch (state) {
		case KyoroSocket.CN_CONNECTED:
			if(Log.ON){Log.v(TAG, "TorrentFront#connected() : ");}
			return true;
		case KyoroSocket.CN_DISCONNECTED:
			if(Log.ON){Log.v(TAG, "TorrentFront#disconnected() : ");}
			throw new IOException();
		case KyoroSocket.CN_CONNECTING:
		default:
			return false;
		}
	}

	public void sendShakehand() throws IOException {
		if(Log.ON){Log.v(TAG, "TorrentFrontTask#sendShakehand");}
		PercentEncoder encoder = new PercentEncoder();
		TorrentPeer torentPeer = mTorrentPeer.get();
		byte[] infoHash = encoder.decode(torentPeer.getInfoHash().getBytes());
		byte[] peerId = encoder.decode(torentPeer.getPeerId().getBytes());
		MessageHandShake send = new MessageHandShake(infoHash, peerId);
		send.encode(mOutput);
		mOutput.flush();
	}
 
	public void sendBitfield() throws IOException {
		if(Log.ON){Log.v(TAG, "TorrentFrontTask#sendBitfield");}
		TorrentPeer torentPeer = mTorrentPeer.get();
		TorrentData torrentData = torentPeer.getTorrentData();
		MessageBitField bitfield = new MessageBitField(
				torrentData.getStockedDataInfo());
		bitfield.encode(mOutput);
		mOutput.flush();
		TorrentHistory.get().pushSend(this, bitfield);
	}

	public void uncoke() throws IOException {
		if(Log.ON){Log.v(TAG, "TorrentFrontTask#unchoke");}
		MessageUnchoke message = new MessageUnchoke();
		message.encode(mOutput);
		mOutput.flush();
		mMyInfo.mChoked = false;
		TorrentHistory.get().pushSend(this, message);
	}

	public void keepAlive() throws IOException {
		if(Log.ON){Log.v(TAG, "TorrentFrontTask#keepAlive");}
		MessageKeepAlive keepAlive = new MessageKeepAlive();
		keepAlive.encode(mOutput);
		mOutput.flush();
		TorrentHistory.get().pushSend(this, keepAlive);
	}

	public void choke() throws IOException {
		if(Log.ON){Log.v(TAG, "TorrentFrontTask#choke");}
		MessageChoke message = new MessageChoke();
		message.encode(mOutput);
		mOutput.flush();
		mMyInfo.mChoked = true;
		TorrentHistory.get().pushSend(this, message);
	}

	public void notinterest() throws IOException {
		if(Log.ON){Log.v(TAG, "TorrentFrontTask#notinterest");}
		MessageNotInterested message = new MessageNotInterested();
		message.encode(mOutput);
		mOutput.flush();
		mMyInfo.mInterest = false;
		TorrentHistory.get().pushSend(this, message);
	}

	public void interest() throws IOException {
		if(Log.ON){Log.v(TAG, "TorrentFrontTask#interest");}
		MessageInterested message = new MessageInterested();
		message.encode(mOutput);
		mOutput.flush();
		mMyInfo.mInterest = true;
		TorrentHistory.get().pushSend(this, message);
	}

	public boolean haveTargetRequest() {
		return mTargetInfo.haveRequest();
	}

	public void sendRequest() throws IOException {
		if(Log.ON){Log.v(TAG, "TorrentFront#sendRequest() ");}
		TorrentPeer peer = mTorrentPeer.get();
		if(peer==null){return;}
		int index = peer.getNextRequestPiece();
		int pieceLength = mTargetInfo.getPieceLength();
		MessageRequest request = new MessageRequest(index, 0, pieceLength);
		request.encode(mOutput);
		if(Log.ON){Log.v(TAG, "/TorrentFront#sendRequest() ");}
	}

	public void sendPiece() throws IOException {
		TorrentPeer peer = mTorrentPeer.get();
		if(peer==null){return;}
		PieceInfo info = mTargetInfo.popPieceInfo();
		long start = info.getStart();
		long end = info.getEnd();
		long index = start/peer.getPieceLength();//peer.getNumOfPieces();
		try {
			if(Log.ON){Log.v(TAG, "TorrentFront#sebdMessage start="+start+",end="+end+",index=" +index+"::"+peer.getPieceLength());}
			if(start == end) {
				TorrentHistory.get().pushMessage("error"+start+","+end+","+mTargetInfo.numOfPiece()+"\n");
				return;
			}

			KyoroFile piece = peer.getTorrentData().getPiece(start, end);
			MessagePiece message = new MessagePiece((int)index, (int)(start-(index*peer.getPieceLength())), piece);
			//mOutput.logon(true);
			message.encode(mOutput);
			//mOutput.logon(false);
			mOutput.flush();
			TorrentHistory.get().pushSend(this, message);
		} catch(IOException e) {
			TorrentHistory.get().pushMessage("ERROR: BROKEN");
			e.printStackTrace();
			close();
			throw e;
		}
	}

	public void receive() throws IOException {
		if(mCurrentHelper == null) {
			TorrentHistory.get().pushMessage("[receive start]\n");
			mCurrentHelper = new HelperLookAheadMessage(mReader.getFilePointer(), mReader);
		}
		mCurrentHelper.read();
		if(mReader.isEOF()) { close(); }
		if(mCurrentHelper.isEnd()) {
			TorrentHistory.get().pushMessage("[receive end]\n");
			//mCurrentHelper.printLog();
			onReceiveMessage(mCurrentHelper);
			mCurrentHelper.clear(mCurrentHelper.myMessageFP()+mCurrentHelper.getMessageSize()+4);
		}
		//
		if(mReader.length()>mReader.getFilePointer()) {
			if(!mTorrentPeer.get().getClientRunner().contains(mReceiverTask)) {
				mTorrentPeer.get().getClientRunner().pushWork(mReceiverTask);
			}			
		}
	}


	private TorrentMessage mLastMessage = null;
	public TorrentMessage getReceivedLastMessage() {
		return mLastMessage;
	}

	public void onReceiveMessage(HelperLookAheadMessage messageBase) throws IOException {
		if(Log.ON){Log.v(TAG, "distribute:"+messageBase.getMessageId());}
		TorrentMessage message = null;
		switch(messageBase.getMessageId()) {
		case TorrentMessage.SIGN_CHOKE:
			if(Log.ON){Log.v(TAG,"receive:choke");}
			mTargetInfo.mTargetChoked = true;
			mReader.seek(messageBase.myMessageFP());
			message = MessageChoke.decode(mReader);
			break;
		case TorrentMessage.SIGN_UNCHOKE:
			if(Log.ON){Log.v(TAG,"receive:unchoke");}
			mTargetInfo.mTargetChoked = false;
			mReader.seek(messageBase.myMessageFP());
			message = MessageUnchoke.decode(mReader);
			break;
		case TorrentMessage.SIGN_INTERESTED:
			if(Log.ON){Log.v(TAG,"receive:interested");}
			mTargetInfo.mTargetInterested = true;
			mReader.seek(messageBase.myMessageFP());
			message = MessageInterested.decode(mReader);
			break;
		case TorrentMessage.SIGN_NOTINTERESTED:
			if(Log.ON){Log.v(TAG,"receive:notinterested");}
			mTargetInfo.mTargetInterested = false;
			mReader.seek(messageBase.myMessageFP());
			message = MessageNotInterested.decode(mReader);
			break;
		case TorrentMessage.SIGN_HAVE:
			if(Log.ON){Log.v(TAG,"receive:have");}
			mReader.seek(messageBase.myMessageFP());
			MessageHave have = MessageHave.decode(mReader);
			mTargetInfo.mTargetBitField.isOn(have.getIndex());
			message = have;
			break;
		case TorrentMessage.SIGN_BITFIELD:
			mReader.seek(messageBase.myMessageFP());
			MessageBitField bitfieldMS = MessageBitField.decode(mReader);
			mTargetInfo.mTargetBitField.setBitfield(bitfieldMS.getBitField().getBinary());
			if(Log.ON){Log.v(TAG,"receive:bitfield:"+bitfieldMS.getBitField().lengthPerByte()
					+","+bitfieldMS.getBitField().toURLString());}
			message = bitfieldMS;
			break;
		case TorrentMessage.SIGN_REQUEST:
			if(Log.ON){Log.v(TAG,"receive:request:--");}
			mReader.seek(messageBase.myMessageFP());
			MessageRequest request = MessageRequest.decode(mReader);
			if(Log.ON){Log.v(TAG,"receive:request:"+request.getIndex()
					+","+request.getBegin()+","+request.getLength());}
			message = request;
			mTargetInfo.request(
					request.getIndex(), request.getBegin(), 
					request.getBegin()+request.getLength());
			break;
		case TorrentMessage.SIGN_PIECE:
			if(Log.ON){Log.v(TAG,"receive:piece");}
			mReader.seek(messageBase.myMessageFP());
			MessagePiece piece = MessagePiece.decode(mReader);
			message = piece;
			{
				TorrentPeer peer = mTorrentPeer.get();
				if(peer == null) {return;}
				TorrentData data = peer.getTorrentData();
				data.setPiece(piece.getIndex(), piece.getCotent());
			}
			
			break;
		case TorrentMessage.SIGN_CANCEL:
			if(Log.ON){Log.v(TAG,"receive:cancel");}
			mReader.seek(messageBase.myMessageFP());
			MessageCancel cancel = MessageCancel.decode(mReader);
			message = cancel;
			break;
		default:
			mReader.seek(messageBase.myMessageFP());
			message = MessageNull.decode(mReader);
			break;
		}

		if (null != message) {
			dispatch(message);
			mLastMessage = message;
		}
		TorrentPeer peer = mTorrentPeer.get();
		if(peer != null) {
			TorrentHistory.get().pushReceive(this, message);
			//peer.getHistory().sync();
		}
	}



	public boolean waitMessage(byte sign, int timeout) {
		TorrentMessage last = null;
		long start = System.currentTimeMillis();
		do{
			last = getReceivedLastMessage();
			if(timeout<(System.currentTimeMillis()-start)) {
				return false;
			}
			Thread.yield();
		} while(last==null||last.getType() != sign);
		return true;
	}


	private LinkedList<WeakReference<EventListener>> mObservers = 
			new LinkedList<WeakReference<EventListener>>();
	public void addObserverAtWeak(EventListener observer) {
		mObservers.add(new WeakReference<EventListener>(observer));
	}

	public static interface EventListener {
		void onReceiveMessage(TorrentFront front,TorrentMessage message);
	}

	public void dispatch(TorrentMessage message) {
		Iterator<WeakReference<EventListener>>	ite = mObservers.iterator();
		while(ite.hasNext()) {
			WeakReference<EventListener> observerref = ite.next();
			EventListener observer = observerref.get();
			if(null == observerref.get()) {
				mObservers.remove(observerref);
			}
			observer.onReceiveMessage(this, message);
		}
	}
}
