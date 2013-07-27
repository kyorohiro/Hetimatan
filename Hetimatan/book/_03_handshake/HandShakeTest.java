package _03_handshake;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;

import com.sun.jndi.url.corbaname.corbanameURLContextFactory;

import net.hetimatan.io.net.KyoroSocket;
import net.hetimatan.io.net.KyoroSocketImpl;
import net.hetimatan.net.torrent.client.TorrentFront;
import net.hetimatan.net.torrent.client.TorrentPeer;
import net.hetimatan.net.torrent.tracker.TrackerClient;
import net.hetimatan.net.torrent.tracker.TrackerPeerInfo;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.net.torrent.util.metafile.MetaFileCreater;
import net.hetimatan.util.bitfield.BitField;
import net.hetimatan.util.event.CloseRunnerTask;
import net.hetimatan.util.net.KyoroSocketEventRunner;

//
//[課題]
// Torrentクライアントとハンドシェークせよ。次にBitfieldメッセージを受信せよ。
// 相手Torrentが保持しているPieceデータを保持せよ。
//
public class HandShakeTest {
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
		// boot TorrentClient Server
		// ----------------------------------------------------
		TorrentPeer peer = new TorrentPeer(metafile, peerId);
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
		client.getPeer32();
		KyoroSocket socket = new KyoroSocketImpl();
		TorrentFront front = peer.createFront(socket);
		front.connect(info.getHostName(), info.getPort());
		while(!front.isConnect()) {Thread.yield();}

		front.sendShakehand();
		front.flushSendTask();

		while(!front.reveiveSH()){Thread.yield();}
		front.revcShakehand();
		
		BitField field = front.getTargetInfo().getBitField();
		System.out.println("bitfield:"+field.toURLString());
	}
}
