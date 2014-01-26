package _05_senddata;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Iterator;

import com.sun.jndi.url.corbaname.corbanameURLContextFactory;

import net.hetimatan.net.torrent.client.TorrentClientFront;
import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.client.message.MessageNull;
import net.hetimatan.net.torrent.tracker.TrackerClient;
import net.hetimatan.net.torrent.tracker.TrackerPeerInfo;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.net.torrent.util.metafile.MetaFileCreater;
import net.hetimatan.util.bitfield.BitField;
import net.hetimatan.util.event.CloseRunnerTask;
import net.hetimatan.util.event.net.KyoroSocketEventRunner;
import net.hetimatan.util.event.net.io.KyoroSelector;
import net.hetimatan.util.event.net.io.KyoroSocket;
import net.hetimatan.util.event.net.io.KyoroSocketImpl;

//
//[課題]
// Torrentクライアントとハンドシェークせよ。次にBitfieldメッセージを受信せよ。
// 相手Torrentが保持しているPieceデータを保持せよ。
// todo now creating
public class SendDataTest {
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
		String peerId = TorrentClient.createPeerIdAsPercentEncode();
		MetaFile metafile = MetaFileCreater.createFromTorrentFile(mTorrent);


		// ----------------------------------------------------
		// boot TorrentClient Server
		// ----------------------------------------------------
		TorrentClient peer = new TorrentClient(metafile, peerId);
		peer.boot();


		// ----------------------------------------------------
		// request peerinfo to tracker
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
		TorrentClientFront front = peer.createFront(socket);
		front.connect(info.getHostName(), info.getPort());
		while(!front.isConnect()) {Thread.yield();}

		front.sendShakehand();
		front.flushSendTask();

		while(!front.parseableShakehand()){Thread.yield();}
		front.revcShakehand();

		// ----------------------------------------------------
		// recv message
		// ----------------------------------------------------
		front.getSocket().regist(front.getSelector(), KyoroSelector.READ);
		do {
			do{Thread.yield();front.getSelector().select(10000);}
			while(1==front.parseableMessage());
			MessageNull message = MessageNull.decode(front.getReader());
			System.out.println("[[sign]]:"+message.getSign());
		}while(true);
	}
}
