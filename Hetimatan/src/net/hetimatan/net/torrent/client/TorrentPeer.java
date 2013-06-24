package net.hetimatan.net.torrent.client;


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Set;

import net.hetimatan.io.net.KyoroSelector;
import net.hetimatan.io.net.KyoroServerSocket;
import net.hetimatan.io.net.KyoroServerSocketImpl;
import net.hetimatan.io.net.KyoroSocket;
import net.hetimatan.io.net.KyoroSocketImpl;
import net.hetimatan.net.torrent.client._peer.TorrentPeerChoker;
import net.hetimatan.net.torrent.client._peer.TorrentPeerInterest;
import net.hetimatan.net.torrent.client._peer.TorrentPeerRequester;
import net.hetimatan.net.torrent.client._peer.TorrentPeerSetting;
import net.hetimatan.net.torrent.client._peer.TorrentPeerPiecer;
import net.hetimatan.net.torrent.client.scenario.task.ScenarioFinTracker;
import net.hetimatan.net.torrent.client.task.TorrentPeerAcceptTask;
import net.hetimatan.net.torrent.client.task.TorrentPeerBootTask;
import net.hetimatan.net.torrent.client.task.TorrentPeerStartTracker;
import net.hetimatan.net.torrent.tracker.TrackerClient;
import net.hetimatan.net.torrent.tracker.TrackerPeerInfo;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;
import net.hetimatan.util.log.Log;
import net.hetimatan.util.net.KyoroSocketEventRunner;
import net.hetimatan.util.url.PercentEncoder;


public class TorrentPeer {
	public static final String TAG = "TorrentPeer";
	public static final String PEERID_HEAD      = "-Kn0000-";//"-KY0114-";
	public static final int TORRENT_PORT_BEGIN  = 6881;
	public static final int TORRENT_PORT_END    = 6889;


	private KyoroServerSocket mServerSocket         = null;
	private LinkedHashMap<TrackerPeerInfo, TorrentFront> mFrontList = new LinkedHashMap<TrackerPeerInfo, TorrentFront>();
	private KyoroSocketEventRunner mMasterRunner    = null;
	private KyoroSelector mAcceptSelector           = null;

	// ---
	// property
	//
	private int mPort                           = TORRENT_PORT_BEGIN;
	private boolean mIsBooted                   = false;
	private long mDownloaded                    = 0;
	private long mUploaded                      = 0;

	// ---
	// this class's delegation
	//
	private MetaFile mMetaFile                  = null;
	private TorrentData mData                   = null; 
	private TrackerClient mTrackerClient        = null;
	private TorrentPeerSetting mSetting         = new TorrentPeerSetting();
	private TorrentPeerChoker mChoker           = new TorrentPeerChoker(this);
	private TorrentPeerRequester mRequester     = new TorrentPeerRequester(this);
	private TorrentPeerPiecer mPieceScenario    = new TorrentPeerPiecer(this);
	private TorrentPeerInterest mInterest       = new TorrentPeerInterest(this);

	// ---
	// task
	//
	private TorrentPeerAcceptTask mAcceptTask   = null;


	// ---
	//
	//
	private ScenarioFinTracker mFinTrackerTask = null;
	private TorrentPeerStartTracker mTrackerTask = null;

	public TorrentPeer(MetaFile metafile, String peerId) throws URISyntaxException, IOException {
		mTrackerClient = new TrackerClient(metafile, peerId);
		mData = new TorrentData(metafile);
		mMetaFile = metafile;
	}

	public void addDownloaded(int downloaded) {
		mDownloaded += downloaded;
	}

	public void addUploaded(int uploaded) {
		mUploaded += uploaded;
	}

	public void startTracker(String event, EventTask last) {
//		startTracker(event, new ScenarioFinTracker(mPieceScenario, mMasterRunner));
		mTrackerClient.update(event, mDownloaded, mUploaded);
		mTrackerClient.startTask(mMasterRunner, last);
	}


	public void startTracker(String event) {
		startTracker(event, mFinTrackerTask = new ScenarioFinTracker(mPieceScenario, mMasterRunner));
	}

	public EventTaskRunner startTask(KyoroSocketEventRunner runner) {
		System.out.println("TorrentPeer#startTask:");
		if(runner == null) {
			mMasterRunner = runner = new KyoroSocketEventRunner();
		}
		runner.waitIsSelect(true);//todo
		mAcceptSelector = runner.getSelector();
		TorrentPeerBootTask bootTask = new TorrentPeerBootTask(this, runner);
		bootTask.nextAction(mTrackerTask = new TorrentPeerStartTracker(this, runner));
		runner.start(bootTask);
		return runner; 
	}

	public void setTrackerTask(int timeout) {
		if(mTrackerTask == null) {
			mTrackerTask = new TorrentPeerStartTracker(this, getClientRunner());
		}
		getClientRunner().releaseTask(mTrackerTask);		
		getClientRunner().pushWork(mTrackerTask, timeout);
	}

	public void startConnect(TrackerPeerInfo peer) throws IOException {
		if(contain(peer)) {return;}
		TorrentFront front = createFront(peer);
		if(addTorrentFront(peer, front)){
			TorrentHistory.get().pushMessage("TorrentPeer#connect()"+peer.toString()+"\n");
			front.startConnect(peer.getHostName(), peer.getPort());
			addObserver(front);
		}
	}

	public KyoroSelector getSelector() {
		return mAcceptSelector;
	}

	public TorrentPeerSetting getSetting() {
		return mSetting;
	}

	public void updateOptimusUnchokePeer(TorrentFront front) throws IOException {
		mChoker.onStartTorrentFront(front);
	}

	public void updateOptimusUnchokePeer() {
		try {
			mChoker.updateOptimusUnchokePeer();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		return mTrackerClient;
	}

	public String getPeerId() {
		return mTrackerClient.getPeerId();
	}

	public String getInfoHash() {
		return mTrackerClient.getInfoHash();
	}

	public void addClientTask(EventTask task) {
		mMasterRunner.pushWork(task);
	}

	public void boot() throws IOException {
		if(Log.ON){Log.v(TAG, "TorrentPeer#boot");}
		mIsBooted = false;
		KyoroServerSocket serverSocket = new KyoroServerSocketImpl();
		if(mMasterRunner == null) {
			mMasterRunner = new KyoroSocketEventRunner();
		}
		if(mAcceptSelector ==null) {
			mAcceptSelector = mMasterRunner.getSelector();
		}
		serverSocket.regist(mAcceptSelector, KyoroSelector.ACCEPT);
		mAcceptTask = new TorrentPeerAcceptTask(this, mMasterRunner);
		serverSocket.setEventTaskAtWrakReference(mAcceptTask);
		do {
			try {
				serverSocket.bind(mPort);
				mServerSocket = serverSocket;
				mTrackerClient.setClientPort(mPort);
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

	public void accept() throws IOException {
		while(true) {
			KyoroSocket socket = mServerSocket.accept();
			socket.setDebug("TorrentPeer"+socket.getHost()+":"+socket.getPort());
			if(socket == null) {
				break;
			}
			TorrentHistory.get().pushMessage("TorrentPeer#accepted()\n");
			TorrentFront front = new TorrentFront(this, socket);
			addObserver(front);
			addTorrentFront(front);
			front.startConnectForAccept();
		}
	}

	private void addObserver(TorrentFront front) {
		front.addObserverAtWeak(mPieceScenario);
		front.addObserverAtWeak(mRequester);//mRequestScenario);
		front.addObserverAtWeak(mInterest);
	}
	public TorrentFront createFront(TrackerPeerInfo peer) throws IOException {
		KyoroSocketImpl s = new KyoroSocketImpl();
		s.setDebug("TorrentFront:"+s.getHost()+":"+s.getPort());
		TorrentFront front = createFront(s);
		front.setPeer(peer);
		return front;
	}

	public TorrentFront createFront(KyoroSocket s) throws IOException {
		TorrentFront front = new TorrentFront(this, s);
		return front;
	}

	public boolean addTorrentFront(TorrentFront front) throws IOException {
		String host = front.getSocket().getHost();
		int port = front.getSocket().getPort();
		TrackerPeerInfo peer = new TrackerPeerInfo(host, port);
		return addTorrentFront(peer, front);
	}


	public boolean contain(TrackerPeerInfo peer) {
		return mFrontList.containsKey(peer);
	}

	public boolean addTorrentFront(TrackerPeerInfo peer, TorrentFront front) throws IOException {
		if(mFrontList.containsKey(peer)) {
			return false;
		} else {
			mFrontList.put(peer, front);
			return true;
		}
	}

	public void removeTorrentFront(TorrentFront front) {
		mFrontList.remove(front.getPeer());
	}

	public EventTaskRunner getClientRunner() {
		return mMasterRunner;
	}

	public void close() {
		try { mServerSocket.close();} catch (IOException e) { }
	}

	public int getServerPort() {
		try { return mServerSocket.getPort();
		} catch (IOException e) { return 0;}
	}

	public synchronized int numOfFront() {
		return mFrontList.size();
	}	

	public TorrentFront getTorrentFront(int i) {
		TrackerPeerInfo key = getFrontPeer(i);
		return getTorrentFront(key);
	}
	public TorrentFront getTorrentFront(TrackerPeerInfo peer) {
		if(peer == null) {return null;}
		return mFrontList.get(peer);
	}

	public TrackerPeerInfo getFrontPeer(int index) {
		Set<TrackerPeerInfo> keys = mFrontList.keySet();
		if(index<keys.size()) {
			return (TrackerPeerInfo)keys.toArray()[index];
		} else {
			return null;
		}
	}

	// peerid is random 20 byte string.  
	// the first character in the peer-id is PEERID_HEAD
	// BEP20
	public static String createPeerId() {
		byte[] peerId = new byte[20]; 
		Random random = new Random(System.currentTimeMillis());
		random.nextBytes(peerId);
		System.arraycopy(PEERID_HEAD.getBytes(), 0, peerId, 0, 8);
		PercentEncoder encoder = new PercentEncoder();
		return encoder.encode(peerId);
	}

	public int getNextRequestPiece() {
		return mRequester.nextPieceId();
	}
}
