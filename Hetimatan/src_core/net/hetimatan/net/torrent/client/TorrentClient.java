package net.hetimatan.net.torrent.client;


import java.io.File;
import java.io.IOException;
import java.util.Random;

import net.hetimatan.net.torrent.client._client.TorrentClientGetPeerList;
import net.hetimatan.net.torrent.client._client.TorrentClientMessageDispatcher;
import net.hetimatan.net.torrent.client._client.TorrentClientTimeDispatcher;
import net.hetimatan.net.torrent.client._client.TorrentClientUploadSenario;
import net.hetimatan.net.torrent.client._client.TorrentClientChoker;
import net.hetimatan.net.torrent.client._client.TorrentClientRequester;
import net.hetimatan.net.torrent.client.task.TorrentPeerAcceptTask;
import net.hetimatan.net.torrent.client.task.TorrentPeerBootTask;
import net.hetimatan.net.torrent.tracker.TrackerClient;
import net.hetimatan.net.torrent.tracker.TrackerPeerInfo;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;
import net.hetimatan.util.event.net.KyoroSocketEventRunner;
import net.hetimatan.util.event.net.io.KyoroSelector;
import net.hetimatan.util.event.net.io.KyoroServerSocket;
import net.hetimatan.util.event.net.io.KyoroServerSocketImpl;
import net.hetimatan.util.event.net.io.KyoroSocket;
import net.hetimatan.util.event.net.io.KyoroSocketImpl;
import net.hetimatan.util.url.PercentEncoder;

/**
 * - start torrent client
 *   TorrentClient c = new TorrentClient()
 *   c.startTorrentClient(null);
 * 
 * 
 */
public class TorrentClient {

	/**
	 * peerid is random 20 byte string.  
	 * the first character in the peer-id is PEERID_HEAD
	 * BEP20
	 */
	private static int d = 0;
	public static String createPeerIdAsPercentEncode() {
		byte[] peerId = new byte[20]; 
		Random random = new Random(System.currentTimeMillis()+d);
		d++;
		random.nextBytes(peerId);
		System.arraycopy(PEERID_HEAD.getBytes(), 0, peerId, 0, 8);
		PercentEncoder encoder = new PercentEncoder();
		return encoder.encode(peerId);
	}

	public static byte[] createPeerId() {
		byte[] peerId = new byte[20]; 
		Random random = new Random(System.currentTimeMillis());
		random.nextBytes(peerId);
		System.arraycopy(PEERID_HEAD.getBytes(), 0, peerId, 0, 8);
		return peerId;
	}

	public static final String TAG              = "TorrentPeer";
	public static final String PEERID_HEAD      = "-KY0114-";
	public String sId 							= "none";
	public static final int TORRENT_PORT_BEGIN  = 6881;
	public static final int TORRENT_PORT_END    = 6889;


	private KyoroServerSocket mServerSocket         = null;
	private KyoroSocketEventRunner mMasterRunner    = null;

	// ---
	// property
	//
	private int mPort                           = TORRENT_PORT_BEGIN;
	private boolean mIsBooted                   = false;
	private long mDownloaded                    = 0;
	private long mUploaded                      = 0;
	private String mPeerId                      = "";
	private String mInfoHash                    = "";
	private MetaFile mMetaFile                  = null;
	private TorrentData mData                   = null; 

	// ---
	// delegate 
	//
	private TorrentClientSetting mSetting         = new TorrentClientSetting();
	private TorrentClientChoker mChoker           = new TorrentClientChoker(this);
	private TorrentClientRequester mRequester     = new TorrentClientRequester(this);
	private TorrentClientUploadSenario mPieceScenario    = new TorrentClientUploadSenario(this);
	private TorrentClientGetPeerList mGetPeerListSenario = null;
	private TorrentClientFrontManager mFrontManager = new TorrentClientFrontManager();
	private TorrentClientTimeDispatcher mIntervalTimer = new TorrentClientTimeDispatcher(this, 30*1000); // 30sec

	
	// ---
	// task
	//
	private TorrentPeerAcceptTask mAcceptTask   = null;
	private static int num = 0;

	//
	TorrentClientMessageDispatcher mDispatcher = new TorrentClientMessageDispatcher();

	public TorrentClient(MetaFile metafile, String peerId) throws IOException {
		mPeerId = peerId;
		mInfoHash = metafile.getInfoSha1AsPercentString();
		mGetPeerListSenario = new TorrentClientGetPeerList(this, metafile, peerId);
		mData = new TorrentData(metafile);
		mMetaFile = metafile;

		getDispatcher().addObserverAtWeak(mPieceScenario);
		getDispatcher().addObserverAtWeak(mRequester);
		getDispatcher().addObserverAtWeak(mChoker);
		getDispatcher().addObserverAtWeak(TorrentHistory.get());
		mIntervalTimer.setInterval(getSetting().getUpdateTime());
		sId = "["+(num++)+"]"+peerId;
	}

	public TorrentClientMessageDispatcher getDispatcher() {
		return mDispatcher;
	}
	
	public void startTracker(String event, EventTask last) {
		TorrentHistory.get().pushMessage("tracker:"+event+","+ mDownloaded+","+ mUploaded+"\n");
		mGetPeerListSenario.startTracker(mMasterRunner, event, last,  mDownloaded, mUploaded);
	}

	public void startTracker(String event) {
		mGetPeerListSenario.startToGetPeerListFromTracker(mMasterRunner, this, event, mDownloaded, mUploaded);
	}


	/**
	 * start Torrent Client. 
	 *  - start torrent server
	 *  - request torrent list to tracker
	 *  - start connect another peer.
	 *  - upload/download data
	 * @param runner
	 * @return
	 * @throws IOException 
	 */
	public KyoroSocketEventRunner startTorrentClient(KyoroSocketEventRunner runner) throws IOException {
		System.out.println("TorrentPeer#startTask:");
		if(runner == null) {runner = new KyoroSocketEventRunner();}
		mMasterRunner = runner;
		runner.waitIsSelect(true);//todo
		// regist boot task, request tacker task, accept event
		TorrentPeerBootTask bootTask = new TorrentPeerBootTask(this);
		bootTask.nextAction(mGetPeerListSenario.getStartTrackerTask());
		//mTrackerTask = new TorrentPeerStartTracker(this));
		mServerSocket = new KyoroServerSocketImpl();
		mServerSocket.setEventTaskAtWrakReference(mMasterRunner.getSelector(), mAcceptTask= new TorrentPeerAcceptTask(this), KyoroSelector.ACCEPT);
		//
		runner.start(bootTask);
		return runner; 
	}

	public void startIntervalTimerTask() throws IOException {
		mIntervalTimer.start(getClientRunner());
	}

	/**
	 * start connection to args peer.
	 * and start download/upload 
	 * @param peer
	 * @throws IOException
	 */
	public void startConnect(TrackerPeerInfo peer) throws IOException {
		if(getTorrentPeerManager().contain(peer)) {return;}
		TorrentClientFront front = createFront(peer);
		if(getTorrentPeerManager().addTorrentFront(peer, front)){
			TorrentHistory.get().pushMessage("TorrentPeer#connect()"+peer.toString()+"\n");
			front.startConnect(peer.getHostName(), peer.getPort());
		}
	}

	public void addDownloaded(int downloaded) {
		mDownloaded += downloaded;
	}

	public void addUploaded(int uploaded) {
		mUploaded += uploaded;
	}

	public KyoroSelector getSelector() {
		return mMasterRunner.getSelector();
	}

	public TorrentClientSetting getSetting() {
		return mSetting;
	}

	public void setMasterFile(File[] master) throws IOException {
		mData.setMaster(master);
	}

	public boolean isSeeder() {
		if(getTorrentData().isComplete())
		{return true;} else {return false;}
	}

	public int getNumOfPieces() {
		byte[] pieces = mMetaFile.getPieces().toByte();
		return pieces.length/MetaFile.SHA1_LENGTH;
	}

	public int getPieceLength() {
		return (int)mMetaFile.getPieceLength();
	}

	public MetaFile getMetaFile() {
		return mMetaFile;
	}

	public TorrentData getTorrentData() {
		return mData;
	}

	public TrackerClient getTracker() {
		return mGetPeerListSenario.getTrackerClient();
	}

	public String getPeerId() {
		return mPeerId;
	}

	public String getInfoHash() {
		return mInfoHash;
	}

	public void addClientTask(EventTask task) {
		mMasterRunner.pushTask(task);
	}

	public EventTaskRunner getClientRunner() {
		return mMasterRunner;
	}

	public int getServerPort() {
		try { return mServerSocket.getPort();
		} catch (IOException e) { return 0;}
	}

	/**
	 * boot torrent server. 
	 * 
	 * @throws IOException
	 */
	public void boot() throws IOException {
		TorrentHistory.get().pushMessage(""+sId+"TorrentPeer#boot()\n");
		mIsBooted = false;

		// todo delete null checl
		if(mMasterRunner == null) {mMasterRunner = new KyoroSocketEventRunner();}
		if(mServerSocket == null) {
			mServerSocket = new KyoroServerSocketImpl();
			mServerSocket.regist(mMasterRunner.getSelector(), KyoroSelector.ACCEPT);
		}

		// boot
		do {
			try {
				mServerSocket.bind(mPort);
				mGetPeerListSenario.setClientPort(mPort);
				mIsBooted = true;
				return;
			} catch(IOException e) {}
			mPort++;
		} while(mPort<=TORRENT_PORT_END);
		throw new IOException("failed to bind.");
	}

	public boolean isBooted() {
		return mIsBooted;
	}

	/**
	 * accept peer request
	 * @throws IOException
	 */
	public void accept() throws IOException {
		while(true) {
			KyoroSocket socket = mServerSocket.accept();
			if(socket == null) {break;}
			socket.setDebug("TorrentPeer"+socket.getHost()+":"+socket.getPort());
			TorrentHistory.get().pushMessage("TorrentPeer#accepted()\n");
			TorrentClientFront front = new TorrentClientFront(this, socket);
			getTorrentPeerManager().addTorrentFront(front);
			front.startConnectForAccept();
		}
	}

	public TorrentClientFront createFront(TrackerPeerInfo peer) throws IOException {
		KyoroSocketImpl s = new KyoroSocketImpl();
		s.setDebug("TorrentFront:"+s.getHost()+":"+s.getPort());
		TorrentClientFront front = createFront(s);
		front.setPeer(peer);
		return front;
	}

	public TorrentClientFront createFront(KyoroSocket s) throws IOException {
		TorrentClientFront front = new TorrentClientFront(this, s);
		return front;
	}

	public void close() {
		try { if(mServerSocket != null){mServerSocket.close();}} catch (IOException e) { }
		try { getTorrentData().save();} catch (IOException e) { }
		try { getDispatcher().dispatchClose(this);}catch(IOException e){}
	}

	public int getNextRequestPiece(TorrentClientFront front) {
		return mRequester.nextPieceId(front);
	}

	public TorrentClientFrontManager getTorrentPeerManager() {
		return mFrontManager;
	}

}

