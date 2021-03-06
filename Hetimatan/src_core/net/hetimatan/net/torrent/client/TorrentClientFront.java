package net.hetimatan.net.torrent.client;

import java.io.IOException;
import java.lang.ref.WeakReference;

import net.hetimatan.io.file.KyoroFile;
import net.hetimatan.io.file.KyoroFileForKyoroSocket;
import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.net.http.PieceInfo;
import net.hetimatan.net.torrent.client._front.TorrentFrontMyInfo;
import net.hetimatan.net.torrent.client._front.TorrentFrontReceiveMessage;
import net.hetimatan.net.torrent.client._front.TorrentFrontShakeHand;
import net.hetimatan.net.torrent.client._front.TorrentClientFrontTargetInfo;
import net.hetimatan.net.torrent.client._front.TorrentFrontTaskManager;
import net.hetimatan.net.torrent.client.message.MessageBitField;
import net.hetimatan.net.torrent.client.message.MessageChoke;
import net.hetimatan.net.torrent.client.message.MessageHave;
import net.hetimatan.net.torrent.client.message.MessageInterested;
import net.hetimatan.net.torrent.client.message.MessageKeepAlive;
import net.hetimatan.net.torrent.client.message.MessageNotInterested;
import net.hetimatan.net.torrent.client.message.MessagePiece;
import net.hetimatan.net.torrent.client.message.MessageRequest;
import net.hetimatan.net.torrent.client.message.MessageUnchoke;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.net.torrent.tracker.TrackerPeerInfo;
import net.hetimatan.util.bitfield.BitField;
import net.hetimatan.util.event.net.io.KyoroSelector;
import net.hetimatan.util.event.net.io.KyoroSocket;
import net.hetimatan.util.log.Log;

/**
 * TorrentClient have TorrentClientForntClass per connection.
 *
 */
public class TorrentClientFront {
	public static final String TAG = "TorrentFront";
	public static final int TRUE  = 0;
	public static final int FALSE = 1;
	public static final int NONE  = -1;

	private MarkableReader mReader = null;
	private WeakReference<TorrentClient> mTorrentPeer = null;

	private boolean mIsOneself = false;
	public boolean isOneself() {
		return mIsOneself;
	}
	public void isOneself(boolean v) {
		mIsOneself = v;
	}

	private boolean mConnectable = true;
	public boolean isConnectable() {
		return mConnectable;
	}
	public void isConnectable(boolean v) {
		mConnectable = v;
	}

	private TorrentClientFrontTargetInfo mTargetInfo = null;
	private TorrentFrontMyInfo mMyInfo = null;
	private KyoroSocket mSocket = null;

	// task
	private TorrentFrontTaskManager mTaskManager = new TorrentFrontTaskManager();

	// delegate
	private TorrentFrontShakeHand mShakeHand = new TorrentFrontShakeHand();
	private TorrentFrontReceiveMessage mMessageSenario = new TorrentFrontReceiveMessage();
	
	private TrackerPeerInfo mPeer = null;
	public String mDebug = "--";

	//
	// TODO next work following args is for test
	//
	private int mRequestPiece = -1;

	// cash
	private CashKyoroFile mSendCash = null;
	private int mRequestedNum = 0;

	public CashKyoroFile getSendCash() {
		return mSendCash;
	}

	public TorrentClientFront(TorrentClient peer, KyoroSocket socket) throws IOException {
		mSocket = socket;
		mTargetInfo = new TorrentClientFrontTargetInfo(peer.getPieceLength());
		KyoroFileForKyoroSocket kf = new KyoroFileForKyoroSocket(socket, 512*30);
		mReader = new MarkableFileReader(kf, 512);
		mTorrentPeer = new WeakReference<TorrentClient>(peer);
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

	public MarkableReader getReader() {
		return mReader;
	}

	public BitField relativeBitfield() {
		return mMyInfo.mRelative;
	}
	public void updateRelativeBitfield() {
	 	TorrentClient peer = mTorrentPeer.get();
		if(peer == null) {return ;}
		TorrentData data = peer.getTorrentData();
		BitField myInfo = data.getStockedDataInfo();
		if(mMyInfo.mRelative == null) {
			mMyInfo.mRelative = new BitField(myInfo.lengthPerBit());
		}
		BitField targetInfo = getTargetInfo().mTargetBitField;
		BitField.relative(targetInfo, myInfo, mMyInfo.mRelative);
	}

	public KyoroSocket getSocket() {
		return mSocket;
	}

	public KyoroSelector getSelector() {
		return getTorrentPeer().getSelector();
	}

	public TorrentClient getTorrentPeer() {
		return mTorrentPeer.get();
	}

	public TorrentFrontMyInfo getMyInfo() {
		return mMyInfo;
	}
	public TorrentClientFrontTargetInfo getTargetInfo() {
		return mTargetInfo;
	}

	public TorrentFrontTaskManager getTaskManager() {
		return mTaskManager;
	}

	public void close() throws IOException {
		TorrentHistory.get().pushMessage("["+mDebug+"]"+"TorrentFront#close()\n");
		TorrentClient peer = mTorrentPeer.get();
		if(peer != null) {
			peer.getTorrentPeerManager().removeTorrentFront(this);
		}
		
		peer.getDispatcher().dispatchCloseFront(this);
		
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


	public void connect(String hostname, int port) throws IOException {
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
			isConnectable(false);
			throw new IOException();
		case KyoroSocket.CN_CONNECTING:
		default:
			return false;
		}
	}

	public void pushFlushSendTask(TorrentMessage message) throws IOException {
		TorrentClient client = getTorrentPeer();
		if(client == null) { return; }
		message.encode(mSendCash.getLastOutput());
		pushflushSendTask();
		client.getDispatcher().dispatchSendTorrentMessage(this, message);
	}

	public void pushflushSendTask() throws IOException {	
		getTaskManager().startSendTask(getTorrentPeer(), this);
	}

	public boolean isShakehanded() {
		return mShakeHand.isShakehanded();
	}

	public boolean parseableShakehand() throws IOException {
		return mShakeHand.parseableShakehand(this);
	}

	public void revcShakehand() throws IOException {
		mShakeHand.revcShakehand(this);
	}

	public void sendShakehand() throws IOException {
		mShakeHand.sendShakehand(this);
	}

	public void flushSendTask() throws IOException {
		mTaskManager.flushSendTask(getTorrentPeer());
	}

	public void sendBitfield() throws IOException {
		TorrentClient torentPeer = mTorrentPeer.get();
		TorrentData torrentData = torentPeer.getTorrentData();
		MessageBitField bitfield = new MessageBitField(torrentData.getStockedDataInfo());
		pushFlushSendTask(bitfield);
	}

	public void sendHave(int index) throws IOException {
		MessageHave message = new MessageHave(index);
		pushFlushSendTask(message);
	}


	public void sendKeepAlive() throws IOException {
		MessageKeepAlive message = new MessageKeepAlive();
		pushFlushSendTask(message);
	}

	public void sendUncoke() throws IOException {
		MessageUnchoke message = new MessageUnchoke();
		pushFlushSendTask(message);
		if(mMyInfo.isChoked() != TorrentClientFront.FALSE) {
			mMyInfo.isChoke(false);
		}
	}

	public void sendChoke() throws IOException {
		MessageChoke message = new MessageChoke();
		pushFlushSendTask(message);
		if(mMyInfo.isChoked() != TorrentClientFront.TRUE) {
			mMyInfo.isChoke(true);
		}
	}

	public void sendNotinterest() throws IOException {
		MessageNotInterested message = new MessageNotInterested();
		pushFlushSendTask(message);
		mMyInfo.mInterest = false;
	}


	public void sendInterest() throws IOException {
		MessageInterested message = new MessageInterested();
		pushFlushSendTask(message);
		mMyInfo.mInterest = true;
	}

	public boolean haveTargetRequest() {
		return mTargetInfo.haveRequest();
	}

	public void sendRequest() throws IOException {
		int index = -1;
		int pieceLength = -1;
		try {
			if(Log.ON){Log.v(TAG, "["+mDebug+"]"+"TorrentFront#sendRequest() ");}
			TorrentClient peer = mTorrentPeer.get();
			if(peer==null){return;}
			if(getTargetInfo().isChoked() == TorrentClientFront.TRUE) {if(Log.ON){Log.v(TAG, "choked");} return;}
			if(mRequestedNum >= peer.getSetting().getNumOfRequestPerFront()) {
				Log.v(TAG, "already requested [B]:"+mRequestedNum);return;}

			index = peer.getNextRequestPiece(this);
			if(index < 0) {
				// target don't have interest piece
				return;
			}
			mRequestPiece = index;
			pieceLength = peer.getTorrentData().getPieceLengthPer(index);
			MessageRequest request = new MessageRequest(index, 0, pieceLength);
			pushFlushSendTask(request);
			mRequestedNum++;
			if(Log.ON){Log.v(TAG, "requested:"+index+","+pieceLength);}
		} finally {
			if(Log.ON){Log.v(TAG, "/TorrentFront#sendRequest() ");}
		}
	}

	public void sendPiece() throws IOException {
		TorrentClient peer = mTorrentPeer.get();
		if(peer==null){return;}
		PieceInfo info = mTargetInfo.popTargetRequestedPieceInfo();
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
			pushFlushSendTask(message);

			//
			// update download/update data size
			getTargetInfo().updateTargetDownloaded(end-start);

			peer.addUploaded((int)piece.length());
		} catch(IOException e) {
			TorrentHistory.get().pushMessage("ERROR: BROKEN");
			e.printStackTrace();
			close();
			throw e;
		}
	}


	// -1 eof
	//  0 parseable
	//  1 end
	public int parseableMessage() throws IOException {
		return mMessageSenario.parseableMessage(this);
	}

	//
	//
	//
	public void receive() throws IOException {
		mMessageSenario.receive(this);
	}

	public void onReceiveMessage(TorrentMessage nullMessage) throws IOException {
		updateTargetInfo(nullMessage) ;
	}

	public void updateTargetInfo(TorrentMessage nullMessage) throws IOException {
		switch(nullMessage.getType()) {
		case TorrentMessage.SIGN_CHOKE:
			mTargetInfo.isChoke(true);
			break;
		case TorrentMessage.SIGN_UNCHOKE:
			mTargetInfo.isChoke(false);
			break;
		case TorrentMessage.SIGN_INTERESTED:
			mTargetInfo.isInterested(true);
			break;
		case TorrentMessage.SIGN_NOTINTERESTED:
			mTargetInfo.isInterested(false);
			break;
		case TorrentMessage.SIGN_HAVE:
			MessageHave have = (MessageHave)nullMessage;
			mTargetInfo.mTargetBitField.isOn(have.getIndex());
			updateRelativeBitfield();
			break;
		case TorrentMessage.SIGN_BITFIELD:
			MessageBitField bitfieldMS = (MessageBitField)nullMessage;
			mTargetInfo.mTargetBitField.setBitfield(bitfieldMS.getBitField().getBinary());
			updateRelativeBitfield();
			break;
		case TorrentMessage.SIGN_REQUEST:
			MessageRequest request = (MessageRequest)nullMessage;
			mTargetInfo.taregtRequested(
					request.getIndex(), request.getBegin(), request.getBegin()+request.getLength());
			break;
		case TorrentMessage.SIGN_PIECE:
			mRequestedNum--;
			MessagePiece piece = (MessagePiece)nullMessage;
			TorrentClient peer = mTorrentPeer.get();
			if(peer == null) {return;}
			{
				// save piece data
				TorrentData data = peer.getTorrentData();
				data.setPiece(piece.getIndex(), piece.getCotent());
			}
			{
				// upload uploaded downloaded size
				peer.addDownloaded((int)piece.getCotent().length());
				getTargetInfo().updateTargetUploaded((int)piece.getCotent().length());
			}
			{
				if(mRequestPiece == piece.getIndex()) {
					mRequestPiece = -1;
				}
			}
			break;
		case TorrentMessage.SIGN_CANCEL:break;
		default:break;
		}
	}





	// ------------------------------------------------
	//
	// ------------------------------------------------
	public void startConnectForAccept() throws IOException {
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

	
}