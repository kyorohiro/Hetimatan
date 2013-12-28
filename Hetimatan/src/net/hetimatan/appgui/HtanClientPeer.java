package net.hetimatan.appgui;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;

import net.hetimatan.io.filen.CashKyoroFileHelper;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.client.TorrentClientFront;
import net.hetimatan.net.torrent.client.TorrentClientFrontManager;
import net.hetimatan.net.torrent.client.TorrentClientListener;
import net.hetimatan.net.torrent.client.message.TorrentMessage;
import net.hetimatan.net.torrent.tracker.TrackerClient;
import net.hetimatan.net.torrent.tracker.TrackerPeerInfo;
import net.hetimatan.net.torrent.tracker.TrackerRequest;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.net.torrent.util.metafile.MetaFileCreater;
import net.hetimatan.util.event.CloseRunnerTask;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;
import net.hetimatan.util.event.GlobalAccessProperty;

public class HtanClientPeer {

	private File mTorrentFile = null;
	private TorrentClient mPeer = null;
	private EventTaskRunner mRunner = null;
	private StatusCheck mObserver = null;
	private MetaFile mMetafile = null;
	
	public static final File sPeerIdSt = new File("peerid");

	public HtanClientPeer() {
	}

	public void setTorrentFile(File torrentFile) throws IOException {
		mTorrentFile = torrentFile;
		mMetafile = MetaFileCreater.createFromTorrentFile(mTorrentFile);
	}

	public boolean isStarted() {
		if(mPeer == null) { return false;}
		return mRunner.isAlive();
	}

	private PeerStatus mPeerStatus = new PeerStatus();
	public void start() throws IOException, URISyntaxException {
		if(!easyCheck()) {
			throw new IOException("unsupported file");
		}

		if(mPeer == null) {
			String peerId = getPeerId();
			mPeer = new TorrentClient(mMetafile, peerId);
		}
		try {
		mPeer.getTorrentData().load();
		} catch(IOException e) {e.printStackTrace();}
		mRunner = mPeer.startTorrentClient(null);
		mPeer.getDispatcher().addObserverAtWeak(mPeerStatus);
	}

	public void stop() {
		if(mPeer != null) {
			mPeer.close();
		}
		if(mRunner != null) {
			EventTask closeTask = new CloseRunnerTask(null);
			mPeer.startTracker(TrackerRequest.EVENT_STOPPED, closeTask);
		}
	}

	public void setSource(File source) throws IOException, URISyntaxException {
		File[] fs = new File[1];
		fs[0] = source;
		if(mPeer == null) {
			String peerId = getPeerId();
			mPeer = new TorrentClient(mMetafile, peerId);
		}
		mPeer.setMasterFile(fs);
	}

	private boolean easyCheck() {
		if(mTorrentFile == null) {return false;}
		if(!mTorrentFile.exists()) {return false;}
		if(!mTorrentFile.isFile()) {return false;}
		return true;
	}
	
	interface StatusCheck {
		public void onUpdateTracker();
		public void onUpdatePeer();
	}

	public synchronized void setStatusCheck(StatusCheck observer) {
		mObserver = observer;
	}

	public synchronized void kickTrackerObserver() {
		if(mObserver != null) {
			mObserver.onUpdateTracker();
		}
	}

	public synchronized void kickPeerObserver() {
		if(mObserver != null) {
			mObserver.onUpdateTracker();
		}
	}


	private String mPeerStatusString = "";
	public String getPeerStatus() {
		return mPeerStatusString;
	}

	public class PeerStatus implements TorrentClientListener {

		@Override
		public void onConnection(TorrentClientFront front) throws IOException {
		}

		@Override
		public void onClose(TorrentClientFront front) throws IOException {
		}

		@Override
		public void onClose(TorrentClient client) throws IOException {
		}

		@Override
		public void onShakeHand(TorrentClientFront front) throws IOException {
			updatePeerInfo(front.getTorrentPeer());
			kickPeerObserver();
		}

		@Override
		public void onSendMessage(TorrentClientFront front, TorrentMessage message) throws IOException {
		}

		@Override
		public void onReceiveMessage(TorrentClientFront front, TorrentMessage message) throws IOException {
		}

		@Override
		public void onResponsePeerList(TorrentClient client, TrackerClient tracker) throws IOException {
		}

		@Override
		public void onInterval(TorrentClient client) {
			updatePeerInfo(client);
			kickPeerObserver();
		}

		public void updatePeerInfo(TorrentClient client) {
			TorrentClientFrontManager manager = client.getTorrentPeerManager();
			int num = manager.numOfFront();

			StringBuilder b = new StringBuilder();
			b.append("[peer info]"+"--"+"\r\n");
			for(int i=0;i<num;i++) {
				TorrentClientFront front = manager.getTorrentFront(i);
				b.append("["+front.getPeer().getHostName()+":"+front.getPeer().getPort()+"]"+
				"td="+front.getTargetInfo().getTargetDownloaded()+","+
				"tu="+front.getTargetInfo().getTargetUploadded()+","+
				"tp="+(front.getTargetInfo().getTargetUploadded()/(front.getTargetInfo().getFrontReuqstedTime()+1))+"\r\n");
			}
			mPeerStatusString = b.toString();
		}
	}

	public static String getPeerId() {
		CashKyoroFile cash = null;
		File parent = (new File("dummy")).getAbsoluteFile().getParentFile();
		String path = GlobalAccessProperty.getInstance().get("my.home", parent.getAbsolutePath());
		File home = new File(path);
		File sPeerIdSt = new File(home,"peerid");
		String peerid = null;
		try {
			cash = new CashKyoroFile(sPeerIdSt,10,2);
			if(cash.length() == 0) {
				peerid = TorrentClient.createPeerIdAsPercentEncode();
				cash.addChunk(peerid.getBytes());
				cash.syncWrite();
			} else {
				peerid = new String(CashKyoroFileHelper.newBinary(cash));
			}
		} catch (IOException e) {
			peerid=TorrentClient.createPeerIdAsPercentEncode();
		} finally {
			if(cash != null) {
				try {cash.close();
				} catch (IOException e) {e.printStackTrace();}
			}
		}
		return peerid;
	}
	
}
