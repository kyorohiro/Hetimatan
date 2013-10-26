package _03_handshake;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;

import net.hetimatan.io.net.KyoroSocket;
import net.hetimatan.io.net.KyoroSocketImpl;
import net.hetimatan.net.torrent.client.TorrentFront;
import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.tracker.TrackerClient;
import net.hetimatan.net.torrent.tracker.TrackerPeerInfo;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.net.torrent.util.metafile.MetaFileCreater;
import net.hetimatan.util.event.CloseRunnerTask;
import net.hetimatan.util.event.net.KyoroSocketEventRunner;

//
//[課題]
// Torrentクライアントとハンドシェークせよ。
//
public class HandShakeTest {
	//
	// このアプリを起動する前に、DummyTrackerを起動してください
	//
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
		String peerId = TorrentClient.createPeerId();
		MetaFile metafile = MetaFileCreater.createFromTorrentFile(mTorrent);


		// ----------------------------------------------------
		// boot TorrentClient Server
		// ----------------------------------------------------
		TorrentClient peer = new TorrentClient(metafile, peerId);
		peer.boot();


		// ----------------------------------------------------
		// request peerinfo tracker
		// ----------------------------------------------------
		TrackerClient client = new TrackerClient(metafile, peerId);
		client.setClientPort(peer.getServerPort());
		KyoroSocketEventRunner runner = client.startTask(null, new CloseRunnerTask(null));
		runner.waitByClose(Integer.MAX_VALUE);

		Iterator<TrackerPeerInfo> peerInfos = client.getPeer32();
		TrackerPeerInfo info = null;
		while(peerInfos.hasNext()) {
			info = peerInfos.next();
			if(peer.getServerPort()==info.getPort()) {
				continue;
			} else {
				break;
			}
		}


		// ----------------------------------------------------
		// send shakehand
		// ----------------------------------------------------
		KyoroSocket socket = new KyoroSocketImpl();
		TorrentFront front = peer.createFront(socket);
		front.connect(info.getHostName(), info.getPort());
		while(!front.isConnect()) {Thread.yield();}

		front.sendShakehand();
		front.flushSendTask();

		// ----------------------------------------------------
		// receive shakehand
		// ----------------------------------------------------
		while(!front.parseableShakehand()){Thread.yield();}
		front.revcShakehand();
	}
}
