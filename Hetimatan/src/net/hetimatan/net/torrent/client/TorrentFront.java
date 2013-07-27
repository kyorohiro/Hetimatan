package net.hetimatan.net.torrent.client;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;

import net.hetimatan.io.file.KyoroFile;
import net.hetimatan.io.file.KyoroFileForKyoroSocket;
import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.io.net.KyoroSocket;
import net.hetimatan.net.torrent.client._front.TorrentFrontMyInfo;
import net.hetimatan.net.torrent.client._front.TorrentFrontTargetInfo;
import net.hetimatan.net.torrent.client._front.TorrentFrontTaskManager;
import net.hetimatan.net.torrent.client.message.HelperLookAheadMessage;
import net.hetimatan.net.torrent.client.message._HelperLookAheadMessage;
import net.hetimatan.net.torrent.client.message.HelperLookAheadShakehand;
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
import net.hetimatan.net.torrent.tracker.TrackerPeerInfo;
import net.hetimatan.net.torrent.util.piece.PieceInfo;
import net.hetimatan.util.bitfield.BitField;
import net.hetimatan.util.log.Log;
import net.hetimatan.util.net.MessageSendTask;
import net.hetimatan.util.url.PercentEncoder;


public class TorrentFront {
	public static final String TAG = "TorrentFront";
	public static final int TRUE  = 0;
	public static final int FALSE = 1;
	public static final int NONE  = -1;

	private MarkableReader mReader = null;
//	private KyoroSocketOutputStream mOutput = null;
	private WeakReference<TorrentPeer> mTorrentPeer = null;
	private HelperLookAheadMessage mCurrentMessage = null;
	private HelperLookAheadShakehand mCurrentSHHelper = null;

	private TorrentFrontTargetInfo mTargetInfo = null;
	private TorrentFrontMyInfo mMyInfo = null;
	private KyoroSocket mSocket = null;

	// task
	private TorrentFrontTaskManager mTaskManager = new TorrentFrontTaskManager();
	private MessageSendTask mTaskChanin = null;

	
	private TrackerPeerInfo mPeer = null;
	private String mDebug = "--";
	private int mRequestPiece = -1;

	// cash
	private CashKyoroFile mSendCash = null;

	public CashKyoroFile getSendCash() {
		return mSendCash;
	}

	public TorrentFront(TorrentPeer peer, KyoroSocket socket) throws IOException {
		mSocket = socket;
		mTargetInfo = new TorrentFrontTargetInfo(peer.getPieceLength());
		KyoroFileForKyoroSocket kf = new KyoroFileForKyoroSocket(socket, 512*30);
		kf.setSelector(peer.getSelector());
		mReader = new MarkableFileReader(kf, 512);
//		mOutput = new KyoroSocketOutputStream(socket);
		mTorrentPeer = new WeakReference<TorrentPeer>(peer);
		mTargetInfo.mTargetBitField = new BitField(peer.getNumOfPieces());
		mTargetInfo.mTargetBitField.zeroClear();
		mMyInfo = new TorrentFrontMyInfo();
		mPeer = new TrackerPeerInfo(socket.getHost(), socket.getPort());
		mDebug = ""+socket.getHost()+":"+socket.getPort();
		mSendCash = new CashKyoroFile(1024, 3);
	}
	
	public String getDebug() {
		return mDebug;
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

	public TorrentFrontTaskManager getTaskManager() {
		return mTaskManager;
	}

	public void close() throws IOException {
		TorrentHistory.get().pushMessage("["+mDebug+"]"+"TorrentFront#close()\n");
		TorrentPeer peer = mTorrentPeer.get();
		if(peer != null) {
			peer.getTorrentPeerManager().removeTorrentFront(this);
		}
		if(mSocket != null) {	
			mSocket.close();
		}
		if(mSendCash != null) {
			mSendCash.close();
		}
	}

	public boolean isTargetComplete() {
		return mTargetInfo.mTargetBitField.isAllOn();
	}

	public void revcShakehand() throws IOException {
		if(Log.ON){Log.v(TAG, "["+mDebug+"]"+"TorrentFrontTask#shakehand");}
		try {
			mReader.setBlockOn(true);
			MessageHandShake recv = MessageHandShake.decode(mReader);
			TorrentHistory.get().pushReceive(this, recv);
//			recv.printLog();
			{//todo
				TorrentPeer peer = getTorrentPeer();
				PercentEncoder encoder = new PercentEncoder();
				if(
						peer.getPeerId()
						.equals(encoder.encode(recv.getPeerId()))){	
					throw new IOException();
				}
			}
		} finally {
			mReader.setBlockOn(false);
			Log.v(TAG, "/TorrentFrontTask#shakehand");
		}
	}

	public void connect(String hostname, int port) throws IOException {
//		if(Log.ON){Log.v(TAG, "TorrentFront#connection() : "+hostname+ ":" +port);}
		mDebug =""+hostname+":"+port;
		TorrentHistory.get().pushMessage("connection() : "+hostname+ ":" +port);
		mSocket.connect(hostname, port);
	}

	public boolean isConnect() throws IOException {
		int state = mSocket.getConnectionState();
		switch (state) {
		case KyoroSocket.CN_CONNECTED:
			if(Log.ON){Log.v(TAG, "["+mDebug+"]"+"TorrentFront#connected() : ");}
			return true;
		case KyoroSocket.CN_DISCONNECTED:
			if(Log.ON){Log.v(TAG, "["+mDebug+"]"+"TorrentFront#disconnected() : ");}
			throw new IOException();
		case KyoroSocket.CN_CONNECTING:
		default:
			return false;
		}
	}

	public void pushflushSendTask() throws IOException {	
		getTaskManager().startSendTask(getTorrentPeer(), this);
	}

	public void sendShakehand() throws IOException {
		PercentEncoder encoder = new PercentEncoder();
		TorrentPeer torentPeer = mTorrentPeer.get();
		byte[] infoHash = encoder.decode(torentPeer.getInfoHash().getBytes());
		byte[] peerId = encoder.decode(torentPeer.getPeerId().getBytes());
		MessageHandShake send = new MessageHandShake(infoHash, peerId);
		TorrentHistory.get().pushSend(this, send);
		send.encode(mSendCash.getLastOutput());
		pushflushSendTask();
	}

	public void flushSendTask() throws IOException {
		mTaskManager.flushSendTask(getTorrentPeer());
	}

	public void sendBitfield() throws IOException {
		TorrentPeer torentPeer = mTorrentPeer.get();
		TorrentData torrentData = torentPeer.getTorrentData();
		MessageBitField bitfield = new MessageBitField(torrentData.getStockedDataInfo());
		bitfield.encode(mSendCash.getLastOutput());
		pushflushSendTask();
		TorrentHistory.get().pushSend(this, bitfield);
	}

	public void sendHave(int index) throws IOException {
		MessageHave message = new MessageHave(index);
		message.encode(mSendCash.getLastOutput());
		pushflushSendTask();
		TorrentHistory.get().pushSend(this, message);
	}


	public void sendKeepAlive() throws IOException {
		MessageKeepAlive keepAlive = new MessageKeepAlive();
		keepAlive.encode(mSendCash.getLastOutput());
		pushflushSendTask();
		TorrentHistory.get().pushSend(this, keepAlive);
	}

	public void sendUncoke() throws IOException {
		MessageUnchoke message = new MessageUnchoke();
		message.encode(mSendCash.getLastOutput());
		pushflushSendTask();
		if(mMyInfo.isChoked() != TorrentFront.FALSE) {
			mMyInfo.isChoke(false);
		}
		TorrentHistory.get().pushSend(this, message);
	}

	public void sendChoke() throws IOException {
		MessageChoke message = new MessageChoke();
		message.encode(mSendCash.getLastOutput());
		pushflushSendTask();
		if(mMyInfo.isChoked() != TorrentFront.TRUE) {
			mMyInfo.isChoke(true);
		}
		TorrentHistory.get().pushSend(this, message);
	}

	public void sendNotinterest() throws IOException {
		MessageNotInterested message = new MessageNotInterested();
		message.encode(mSendCash.getLastOutput());
		pushflushSendTask();
		mMyInfo.mInterest = false;
		TorrentHistory.get().pushSend(this, message);
	}


	public void sendInterest() throws IOException {
		MessageInterested message = new MessageInterested();
		message.encode(mSendCash.getLastOutput());
		pushflushSendTask();
		mMyInfo.mInterest = true;
		TorrentHistory.get().pushSend(this, message);
	}

	public boolean haveTargetRequest() {
		return mTargetInfo.haveRequest();
	}

	public void sendRequest() throws IOException {
		int index = -1;
		int pieceLength = -1;
		try {
			if(Log.ON){Log.v(TAG, "["+mDebug+"]"+"TorrentFront#sendRequest() ");}
			TorrentPeer peer = mTorrentPeer.get();
			if(peer==null){if(Log.ON){Log.v(TAG, "--1--");}
				return;}
			if(getTargetInfo().isChoked() == TorrentFront.TRUE) {if(Log.ON){Log.v(TAG, "--2--");}
				return;}
			if(mRequestPiece != -1) {if(Log.ON){Log.v(TAG, "--3--");}
				return;}
			index = peer.getNextRequestPiece();
			mRequestPiece = index;
			pieceLength = mTargetInfo.getPieceLength();
			MessageRequest request = new MessageRequest(index, 0, pieceLength);
			request.encode(mSendCash.getLastOutput());
			pushflushSendTask();
			TorrentHistory.get().pushSend(this, request);
		} finally {
			if(Log.ON){Log.v(TAG, "/TorrentFront#sendRequest() "+index+","+pieceLength);}
		}
	}

	public void sendPiece() throws IOException {
		TorrentPeer peer = mTorrentPeer.get();
		if(peer==null){return;}
		PieceInfo info = mTargetInfo.popPieceInfo();
		long start = info.getStart();
		long end = info.getEnd();
		long index = start/peer.getPieceLength();//peer.getNumOfPieces();
		try {
			if(Log.ON){Log.v(TAG, "["+mDebug+"]"+"TorrentFront#sebdMessage start="+start+",end="+end+",index=" +index+"::"+peer.getPieceLength());}
			if(start == end) {
				TorrentHistory.get().pushMessage("error"+start+","+end+","+mTargetInfo.numOfPiece()+"\n");
				return;
			}

			KyoroFile piece = peer.getTorrentData().getPiece(start, end);
			MessagePiece message = new MessagePiece((int)index, (int)(start-(index*peer.getPieceLength())), piece);
			message.encode(mSendCash.getLastOutput());
			pushflushSendTask();
			peer.addUploaded((int)piece.length());
			TorrentHistory.get().pushSend(this, message);
		} catch(IOException e) {
			TorrentHistory.get().pushMessage("ERROR: BROKEN");
			e.printStackTrace();
			close();
			throw e;
		}
	}

	public boolean parseableShakehand() throws IOException {
		if(Log.ON){Log.v(TAG, "["+mDebug+"]"+"TorrentFront#revieceSH()");}
		if(mCurrentSHHelper == null) {
			TorrentHistory.get().pushMessage("[receive start]\n");
			mCurrentSHHelper = 
					new HelperLookAheadShakehand(mReader.getFilePointer(), mReader);
		}
		mCurrentSHHelper.read();
		if(mReader.isEOF()){close(); return true;}
		
	 	TorrentPeer peer = mTorrentPeer.get();
		if(mCurrentSHHelper.isEnd()) {
			return true;
		} else {
			return false;
		}
	}

	//
	// -1 eof
	//  0 parseable
	//  1 end
	public int parseableMessage() throws IOException {
		if(Log.ON){Log.v(TAG, "["+mDebug+"]"+"TorrentFront#parseableMessage()");}
		if(mCurrentMessage == null) {
			TorrentHistory.get().pushMessage("[receive start]\n");
			mCurrentMessage = new HelperLookAheadMessage(mReader);
		}
		boolean isEnd = mCurrentMessage.read();
		if(isEnd) {return 0;}
		else if(mReader.isEOF()){ return -1;}
		else{return 1;}
	}

	public void receive() throws IOException {
		int parseable = parseableMessage();
		if(parseable == -1) {
			close();
		} else if(parseable == 0) {
			TorrentHistory.get().pushMessage("[receive end]\n");
			onReceiveMessage(mCurrentMessage.getMessageNull());
		}
		if(mReader.length()>mReader.getFilePointer()) {
			if(!mTorrentPeer.get().getClientRunner().contains(mTaskManager.mReceiverTask)) {
				mTorrentPeer.get().getClientRunner().pushWork(mTaskManager.mReceiverTask);
			}			
		}
	}


	private TorrentMessage mLastMessage = null;
	public TorrentMessage getReceivedLastMessage() {
		return mLastMessage;
	}

	public void onReceiveMessage(MessageNull nullMessage) throws IOException {
		if(Log.ON){Log.v(TAG, "["+mDebug+"]"+"distribute:"+nullMessage.getSign()+":"+nullMessage.getMessageLength());}
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
				TorrentPeer peer = mTorrentPeer.get();
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

		TorrentPeer peer = mTorrentPeer.get();
		if(peer != null) {
			TorrentHistory.get().pushReceive(this, message);
		}

		if (null != message) {
			dispatch(message);
			mLastMessage = message;
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


	// ------------------------------------------------
	//
	// ------------------------------------------------
	public void startConnectForAccept() {
		if(Log.ON){Log.v(TAG, "["+mDebug+"]"+"start accept task");}
		mTaskManager.startConnectForAccept(mTorrentPeer.get(), this);
	}

	public void startConnect(String host, int port) throws IOException {
		if(Log.ON){Log.v(TAG, "["+mDebug+"]"+"start connection task");}
		mTaskManager.startConnect(mTorrentPeer.get(), this, host, port);
	}

	public void startReceliver() throws IOException {
		if(Log.ON){Log.v(TAG, "["+mDebug+"]"+"start receiver");}
		mTaskManager.startReceliver(mTorrentPeer.get(), this);
	}

	public void startInterest() {
		if(Log.ON){Log.v(TAG, "["+mDebug+"]"+"start interest");}
		mTaskManager.startInterest(mTorrentPeer.get(), this);
	}

	public void startNotInterest() {
		if(Log.ON){Log.v(TAG, "["+mDebug+"]"+"start notinterest");}
		mTaskManager.startNotInterest(mTorrentPeer.get(), this);
	}

	public void startDownload() throws IOException {
		if(Log.ON){Log.v(TAG, "["+mDebug+"]"+"start Download");}
		mTaskManager.startDownload(mTorrentPeer.get(), this);
	}

	public void startChoker(boolean isChoke) throws IOException {
		if(Log.ON){Log.v(TAG, "["+mDebug+"]"+"start Choker:" +isChoke);}
		mTaskManager.startChoker(mTorrentPeer.get(), this, isChoke);
	}

	public void startHave(int index) throws IOException {
		if(Log.ON){Log.v(TAG, "["+mDebug+"]"+"start Have");}
		mTaskManager.startHave(mTorrentPeer.get(), this, index);
	}
}