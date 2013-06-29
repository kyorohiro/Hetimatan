package net.hetimatan.hetimatan;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;

import net.hetimatan.io.filen.KFNextHelper;
import net.hetimatan.io.filen.RACashFile;
import net.hetimatan.net.torrent.client.TorrentPeer;
import net.hetimatan.net.torrent.client.task.TorrentPeerStopTracker;
import net.hetimatan.net.torrent.tracker.TrackerClient;
import net.hetimatan.net.torrent.tracker.TrackerPeerInfo;
import net.hetimatan.net.torrent.tracker.TrackerRequest;
import net.hetimatan.net.torrent.tracker.TrackerServer.StatusCheck;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.net.torrent.util.metafile.MetaFileCreater;
import net.hetimatan.util.event.CloseTask;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;

public class HtanPeer {

	private File mTorrentFile = null;
	private TorrentPeer mPeer = null;
	private String mPeerId = getPeerId();
	private EventTaskRunner mRunner = null;
	private StatusCheck mObserver = null;
	private MetaFile mMetafile = null;
	public HtanPeer() {
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

		mPeer = new TorrentPeer(mMetafile, mPeerId);
		mRunner = mPeer.startTask(null);
		mPeer.getTracker().setStatusCheck(new TrackerStatus());
	}

	public void stop() {
		if(mPeer != null) {
			mPeer.close();
		}
		if(mRunner != null) {
			EventTask closeTask = new CloseTask(mRunner, null);
			mPeer.startTracker(TrackerRequest.EVENT_STOPPED, closeTask);
		}
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

	public static final File sPeerIdSt = new File("peerid");
	public static String getPeerId() {
		RACashFile cash = null;

		String peerid = null;
		try {
			cash = new RACashFile(sPeerIdSt,10,2);
			if(cash.length() == 0) {
				peerid = TorrentPeer.createPeerId();
				cash.addChunk(peerid.getBytes());
				cash.syncWrite();
			} else {
				peerid = new String(KFNextHelper.newBinary(cash));
			}
		} catch (IOException e) {
			peerid=TorrentPeer.createPeerId();
		} finally {
			if(cash != null) {
				try {cash.close();
				} catch (IOException e) {e.printStackTrace();}
			}
		}
		return peerid;
	}
}
