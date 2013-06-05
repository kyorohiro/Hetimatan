package net.hetimatan.net.torrent.client;


import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

import net.hetimatan.io.net.KyoroSelector;
import net.hetimatan.io.net.KyoroServerSocket;
import net.hetimatan.io.net.KyoroServerSocketImpl;
import net.hetimatan.io.net.KyoroSocket;
import net.hetimatan.io.net.KyoroSocketImpl;
import net.hetimatan.net.torrent.client.scenario.TorrentPieceScenario;
import net.hetimatan.net.torrent.client.scenario.TorrentRequestScenario;
import net.hetimatan.net.torrent.client.scenario.task.ScenarioFinTracker;
import net.hetimatan.net.torrent.client.task.TorrentPeerAcceptTask;
import net.hetimatan.net.torrent.client.task.TorrentPeerBootTask;
import net.hetimatan.net.torrent.client.task.TorrentPeerStartTracker;
import net.hetimatan.net.torrent.tracker.TrackerClient;
import net.hetimatan.net.torrent.tracker.TrackerClient.Peer;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;
import net.hetimatan.util.log.Log;
import net.hetimatan.util.net.KyoroSocketEventRunner;
import net.hetimatan.util.url.PercentEncoder;


public class TorrentPeer {
	public static final String TAG = "TorrentPeer";
	public static final String PEERID_HEAD      = "-KY0114-";
	public static final int TORRENT_PORT_BEGIN  = 6881;
	public static final int TORRENT_PORT_END    = 6889;

	private int mPort                           = TORRENT_PORT_BEGIN;
	private boolean mIsBooted = false;
	private TrackerClient mTrackerClient        = null;
	private KyoroServerSocket mServerSocket     = null;
	private TorrentData mData                   = null; 
	private MetaFile mMetaFile                  = null;

	private KyoroSocketEventRunner mMasterRunner       = null;//new EventTaskRunnerImple();
	private TorrentPieceScenario mPieceScenario = null;
	private TorrentRequestScenario mRequestScenario = null;
	private KyoroSelector mAcceptSelector       = null;
	private TorrentPeerAcceptTask mAcceptTask   = null;
	private LinkedList<WeakReference<Peer>> mOptimusUnchokePeer = new LinkedList<>();
	private LinkedHashMap<Peer, TorrentFront> mFrontList = new LinkedHashMap<TrackerClient.Peer, TorrentFront>();

	
	public TorrentPeer(MetaFile metafile, String peerId) throws URISyntaxException, IOException {
		mTrackerClient = new TrackerClient(metafile, peerId);
		mData = new TorrentData(metafile);
		mMetaFile = metafile;
		mPieceScenario = new TorrentPieceScenario(this);
		mRequestScenario = new TorrentRequestScenario(this);
	}

	public void startTracker() {
		ScenarioFinTracker request = new ScenarioFinTracker(mPieceScenario, mMasterRunner);
		mTrackerClient.startTask(mMasterRunner, request);		
	}

	public EventTaskRunner startTask(KyoroSocketEventRunner runner) {
		System.out.println("TorrentPeer#startTask:");
		if(runner == null) {
			mMasterRunner = runner = new KyoroSocketEventRunner();
		}
		runner.waitIsSelect(true);//todo
		mAcceptSelector = runner.getSelector();
		TorrentPeerBootTask bootTask = new TorrentPeerBootTask(this, runner);
		bootTask.nextAction(new TorrentPeerStartTracker(this, runner));
		runner.start(bootTask);
		return runner; 
	}

	public void startConnect(Peer peer) throws IOException {
		if(!contain(peer)) {return;}
		TorrentFront front = createFront(peer);
		if(addTorrentFront(peer, front)){
			TorrentHistory.get().pushMessage("TorrentPeer#connect()"+peer.toString()+"\n");
			front.startConnect(peer.getHostName(), peer.getPort());
			front.addObserverAtWeak(mPieceScenario);
			front.addObserverAtWeak(mRequestScenario);
		}
	}

	public KyoroSelector getSelector() {
		return mAcceptSelector;
	}

	public void updateOptimusUnchokePeer() {
		int len = mOptimusUnchokePeer.size();
		for(int i=0;i<len;i++) {
			WeakReference<Peer> peer = mOptimusUnchokePeer.get(i);
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
			if(socket == null) {
				break;
			}
			TorrentHistory.get().pushMessage("TorrentPeer#accepted()\n");
			TorrentFront front = new TorrentFront(this, socket);
			front.addObserverAtWeak(mPieceScenario);
			front.addObserverAtWeak(mRequestScenario);
			addTorrentFront(front);
			front.startConnectForAccept();
		}
	}

	public TorrentFront createFront(Peer peer) throws IOException {
		KyoroSocketImpl s = new KyoroSocketImpl();
		TorrentFront front = new TorrentFront(this, s);
		return front;
	}

	public boolean addTorrentFront(TorrentFront front) throws IOException {
		String host = front.getSocket().getHost();
		int port = front.getSocket().getPort();
		Peer peer = new Peer(host, port);
		return addTorrentFront(peer, front);
	}


	public boolean contain(Peer peer) {
		return mFrontList.containsKey(peer);
	}

	public boolean addTorrentFront(Peer peer, TorrentFront front) throws IOException {
		if(mFrontList.containsKey(peer)) {
			return false;
		} else {
			mFrontList.put(peer, front);
			return true;
		}
	}

	public void removeTorrentFront(TorrentFront front) {
		mFrontList.remove(front);
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

	public TorrentFront getTorrentFront(Peer peer) {
		return mFrontList.get(peer);
	}

	public Peer getFrontPeer(int index) {
		Set<Peer> keys = mFrontList.keySet();
		if(index<keys.size()) {
			return (Peer)keys.toArray()[index];
		} else {
			return null;
		}
	}

	@Deprecated
	public synchronized int getFront(int index, TorrentFront[] out) {
		int len = out.length;
		if(len>mFrontList.size()) {
			len=mFrontList.size();
		}
		int ret = len-index;
		if(ret<=0) {
			return 0;
		}
		Set<Peer> keys = mFrontList.keySet();
		Iterator<Peer> ki = keys.iterator();
		for(int i=index;ki.hasNext();i++) {
			out[i] = mFrontList.get(ki.next());
		}
		return ret;
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
}
