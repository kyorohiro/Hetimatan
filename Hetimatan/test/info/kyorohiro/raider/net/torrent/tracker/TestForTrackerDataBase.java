package info.kyorohiro.raider.net.torrent.tracker;

import java.io.IOException;

import info.kyorohiro.helloworld.io.MarkableFileReader;
import info.kyorohiro.raider.net.torrent.client.TorrentPeer;
import info.kyorohiro.raider.net.torrent.tracker.db.TrackerDB;
import info.kyorohiro.raider.net.torrent.tracker.db.TrackerPeerInfo;
import info.kyorohiro.raider.net.torrent.util.MetaFile;
import info.kyorohiro.raider.util.bencode.BenString;
import info.kyorohiro.raider.util.http.HttpRequestURI;
import junit.framework.TestCase;

public class TestForTrackerDataBase extends TestCase {

	public void testHello() {
		
	}

	public void testPeerInfo_001() throws IOException {
		byte[] targetFileContent = "test".getBytes();
		String targetFileHash = MetaFile
				.createPieces(new MarkableFileReader(targetFileContent))
				.toPercentString();
		String peerId = TorrentPeer.createPeerId();
		HttpRequestURI uri = HttpRequestURI.newInstance("dummy", "/announce", "dummy")
		.putValue(TrackerRequest.KEY_COMPACT, "1")
		.putValue(TrackerRequest.KEY_DOWNLOADED, "1")
		.putValue(TrackerRequest.KEY_UPLOADED, "2")
		.putValue(TrackerRequest.KEY_LEFT, "3")
		.putValue(TrackerRequest.KEY_EVENT, TrackerRequest.EVENT_STARTED)
		.putValue(TrackerRequest.KEY_INFO_HASH, targetFileHash)
		.putValue(TrackerRequest.KEY_PEER_ID, peerId)
		.putValue(TrackerRequest.KEY_PORT, "6868");

		TrackerPeerInfo peerinfo = TrackerPeerInfo.createPeerInfo(uri, "127.0.0.1",6868);

		assertEquals(1, peerinfo.getDownloaded());
		assertEquals(2, peerinfo.getUploaded());
		assertEquals(3, peerinfo.getLeft());
		assertEquals(TrackerRequest.EVENT_STARTED, peerinfo.getEvent());
		assertEquals(targetFileHash, peerinfo.getInfoHash());
		assertEquals(peerId, peerinfo.getPeerId());
		assertEquals("127.0.0.1", peerinfo.getIP());
		assertEquals(6868, peerinfo.getPort());
		assertEquals("", peerinfo.getTrackerId());		
	}

	public void testTrackerDataBase_001() throws IOException {
		byte[] targetFileContent = "test".getBytes();
		BenString targetFileHash = MetaFile
				.createPieces(new MarkableFileReader(targetFileContent));

		TrackerDB database = new TrackerDB();
		database.addManagedData(targetFileHash.toByte());
		TrackerServer server = new TrackerServer();
	}

}
