package _02_tracker;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;

import net.hetimatan.net.torrent.client.TorrentPeer;
import net.hetimatan.net.torrent.tracker.TrackerClient;
import net.hetimatan.net.torrent.tracker.TrackerPeerInfo;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.net.torrent.util.metafile.MetaFileCreater;
import net.hetimatan.util.event.CloseRunnerTask;
import net.hetimatan.util.net.KyoroSocketEventRunner;

//
//
//[課題]
// TracckerにアクセスしてPeerの一覧を取得せよ。
//
//
public class GetPeersFromTracker {
	public static void main(String[] args) {
		try {
			start();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void start() throws IOException, URISyntaxException, InterruptedException {
		File mTorrent = new File("./testdata/1k.txt.torrent");
		String peerId = TorrentPeer.createPeerId();
		MetaFile metafile = MetaFileCreater.createFromTorrentFile(mTorrent);

		// ----------------------------------------------------
		// request peerinfo tracker
		// ----------------------------------------------------
		TrackerClient client = new TrackerClient(metafile, peerId);
		client.setClientPort(18080);
		KyoroSocketEventRunner runner = client.startTask(null, new CloseRunnerTask(null));
		runner.waitByClose(Integer.MAX_VALUE);

		Iterator<TrackerPeerInfo> peerInfos = client.getPeer32();
		TrackerPeerInfo info = null;
		while(peerInfos.hasNext()) {
			// ----------------------------------------------------
			// show peer
			// ----------------------------------------------------
			info = peerInfos.next();
			System.out.println("peers:"+info.getHostName()+":"+info.getPort());
		}
	}
}
