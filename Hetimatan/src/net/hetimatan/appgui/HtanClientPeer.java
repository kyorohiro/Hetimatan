package net.hetimatan.appgui;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;

import net.hetimatan.io.filen.CashKyoroFileHelper;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.net.torrent.client.TorrentClient;
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
		mPeer.getTracker().setStatusCheck(new TrackerStatus());
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
	}

	public synchronized void setStatusCheck(StatusCheck observer) {
		mObserver = observer;
	}

	public synchronized void kickObserver() {
		if(mObserver != null) {
			mObserver.onUpdateTracker();
		}
	}

	private String mTrackerStatus = "";
	public String getTrackerStatus() {
		return mTrackerStatus;
	}

	public class TrackerStatus implements TrackerClient.StatusCheck {
		@Override
		public void onUpdate(TrackerClient client) {
			Iterator<TrackerPeerInfo> infos = client.getPeer32();
			StringBuilder b = new StringBuilder();
			b.append("[tracker info]"+"--"+"\r\n");
			while(infos.hasNext()) {
				TrackerPeerInfo info = infos.next();
				b.append(info.getHostName()+":"+info.getPort()+"\r\n");
			}
			mTrackerStatus = b.toString();
			kickObserver();
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
