package net.hetimatan.hetimatan;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;

import net.hetimatan.net.torrent.client.TorrentPeer;
import net.hetimatan.net.torrent.tracker.TrackerClient;
import net.hetimatan.net.torrent.tracker.TrackerPeerInfo;
import net.hetimatan.net.torrent.tracker.TrackerServer.StatusCheck;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.net.torrent.util.metafile.MetaFileCreater;
import net.hetimatan.util.event.EventTaskRunner;

public class HtanPeer {

	private File mTorrentFile = null;
	private TorrentPeer mPeer = null;
	private String mPeerId = TorrentPeer.createPeerId();
	private EventTaskRunner mRunner = null;
	private StatusCheck mObserver = null;

	public void setTorrentFile(File torrentFile) {
		mTorrentFile = torrentFile;
	}

	public boolean isStarted() {
		if(mPeer == null) { return false;}
		return mRunner.isAlive();
	}

	public void start() throws IOException, URISyntaxException {
		if(!easyCheck()) {
			throw new IOException("unsupported file");
		}
		MetaFile metafile = MetaFileCreater.createFromTorrentFile(mTorrentFile);
		mPeer = new TorrentPeer(metafile, mPeerId);
		mRunner = mPeer.startTask(null);
		mPeer.getTracker().setStatusCheck(new TrackerStatus());
	}

	public void stop() {
		if(mPeer != null) {
			mPeer.close();
		}
		if(mRunner != null) {
			mRunner.close();
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
			while(infos.hasNext()) {
				TrackerPeerInfo info = infos.next();
				b.append(info.getHostName()+"\r\n");
			}
			mTrackerStatus = b.toString();
			kickObserver();
		}
	}
}
