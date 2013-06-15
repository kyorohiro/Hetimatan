package net.hetimatan.hetimatan;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import net.hetimatan.net.torrent.client.TorrentPeer;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.net.torrent.util.metafile.MetaFileCreater;
import net.hetimatan.util.event.EventTaskRunner;

public class HtanPeer {

	private File mTorrentFile = null;
	private TorrentPeer mPeer = null;
	private String mPeerId = TorrentPeer.createPeerId();
	private EventTaskRunner mRunner = null;
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
}
