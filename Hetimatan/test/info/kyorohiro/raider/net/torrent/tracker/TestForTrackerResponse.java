package info.kyorohiro.raider.net.torrent.tracker;

import java.io.IOException;

import net.hetimatan.io.file.KyoroFile;
import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.net.torrent.client.TorrentClient;
import net.hetimatan.net.torrent.tracker.TrackerRequest;
import net.hetimatan.net.torrent.tracker.TrackerResponse;
import net.hetimatan.net.torrent.tracker.db.TrackerData;
import net.hetimatan.net.torrent.tracker.db.TrackerDatam;
import net.hetimatan.net.torrent.util.bencode.BenDiction;
import net.hetimatan.net.torrent.util.bencode.BenObject;
import net.hetimatan.net.torrent.util.bencode.BenString;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.util.http.HttpRequest;
import junit.framework.TestCase;

public class TestForTrackerResponse extends TestCase {

	public void testHello() {
		
	}

	public void testCreateResponce_compactOn001() throws IOException {
		byte[] infoHashAsBytes = "テスト".getBytes();
		BenString infoHash = MetaFile
				.createInfoSHA1(new MarkableFileReader(infoHashAsBytes));
		TrackerData data = new TrackerData(infoHash.toByte());
		{
			TrackerDatam info = createPeerInfo(infoHashAsBytes);
			BenDiction response = TrackerResponse.createResponce(data, info, 1);
			assertEquals(1800, response.getBenValue(TrackerResponse.KEY_INTERVAL).toInteger());
			assertEquals(BenObject.TYPE_STRI, response.getBenValue(TrackerResponse.KEY_PEERS).getType());
			assertEquals(0, response.getBenValue(TrackerResponse.KEY_PEERS).toByte().length);
			show(response.getBenValue(TrackerResponse.KEY_PEERS).toByte());
		}
		{
			TrackerDatam info = createPeerInfo(infoHashAsBytes);
			data.putPeerInfo(info);

			byte[] expected = {127, 0, 0, 1, 26, -44};
			BenDiction response = TrackerResponse.createResponce(data, info, 1);
			assertEquals(1800, response.getBenValue(TrackerResponse.KEY_INTERVAL).toInteger());
			assertEquals(BenObject.TYPE_STRI, response.getBenValue(TrackerResponse.KEY_PEERS).getType());
			assertEquals(6, response.getBenValue(TrackerResponse.KEY_PEERS).toByte().length);
			show(response.getBenValue(TrackerResponse.KEY_PEERS).toByte());
			assertEquals(expected, response.getBenValue(TrackerResponse.KEY_PEERS).toByte());
		}

		{
			TrackerDatam info = createPeerInfo(infoHashAsBytes);
			data.putPeerInfo(info);

			byte[] expected = {127, 0, 0, 1, 26, -44, 127, 0, 0, 1, 26, -44};
			BenDiction response = TrackerResponse.createResponce(data, info, 1);
			assertEquals(1800, response.getBenValue(TrackerResponse.KEY_INTERVAL).toInteger());
			assertEquals(BenObject.TYPE_STRI, response.getBenValue(TrackerResponse.KEY_PEERS).getType());
			assertEquals(12, response.getBenValue(TrackerResponse.KEY_PEERS).toByte().length);
			show(response.getBenValue(TrackerResponse.KEY_PEERS).toByte());
			assertEquals(expected, response.getBenValue(TrackerResponse.KEY_PEERS).toByte());
		}

	}

	public void testCreateResponce_compactOff001() throws IOException {
		byte[] infoHashAsBytes = "テスト".getBytes();
		BenString infoHash = MetaFile
				.createInfoSHA1(new MarkableFileReader(infoHashAsBytes));
		TrackerData data = new TrackerData(infoHash.toByte());
		{
			TrackerDatam info = createPeerInfo(infoHashAsBytes);
			BenDiction response = TrackerResponse.createResponce(data, info, 0);
			assertEquals(1800, response.getBenValue(TrackerResponse.KEY_INTERVAL).toInteger());
			assertEquals(BenObject.TYPE_LIST, response.getBenValue(TrackerResponse.KEY_PEERS).getType());
			assertEquals(0, response.getBenValue(TrackerResponse.KEY_PEERS).size());
//			show(response.getBenValue(TrackerResponse.KEY_PEERS).toByte());
		}
		{
			TrackerDatam info = createPeerInfo(infoHashAsBytes);
			data.putPeerInfo(info);

			byte[] expected = {127, 0, 0, 1, 26, -44};
			BenDiction response = TrackerResponse.createResponce(data, info, 0);
			assertEquals(1800, response.getBenValue(TrackerResponse.KEY_INTERVAL).toInteger());
			assertEquals(BenObject.TYPE_LIST, response.getBenValue(TrackerResponse.KEY_PEERS).getType());
			assertEquals(1, response.getBenValue(TrackerResponse.KEY_PEERS).size());
			BenObject peers = response.getBenValue(TrackerResponse.KEY_PEERS);
			assertEquals(BenObject.TYPE_DICT, peers.getBenValue(0).getType());
			assertEquals(BenObject.TYPE_STRI, peers.getBenValue(0).getBenValue(TrackerResponse.KEY_PEER_ID).getType());
			assertEquals(info.getPeerId(), peers.getBenValue(0).getBenValue(TrackerResponse.KEY_PEER_ID).toString());	
			assertEquals(BenObject.TYPE_STRI, peers.getBenValue(0).getBenValue(TrackerResponse.KEY_IP).getType());	
			assertEquals("127.0.0.1", peers.getBenValue(0).getBenValue(TrackerResponse.KEY_IP).toString());	
			assertEquals(BenObject.TYPE_INTE, peers.getBenValue(0).getBenValue(TrackerResponse.KEY_PORT).getType());
			assertEquals(6868, peers.getBenValue(0).getBenValue(TrackerResponse.KEY_PORT).toInteger());
		}
	}

	public void testCreateResponce_remove001() throws IOException {
		byte[] infoHashAsBytes = "テスト".getBytes();
		BenString infoHash = MetaFile
				.createInfoSHA1(new MarkableFileReader(infoHashAsBytes));
		TrackerData data = new TrackerData(infoHash.toByte());
		{
			TrackerDatam info001 = createPeerInfo(infoHashAsBytes);
			TrackerDatam info002 = createPeerInfo(infoHashAsBytes);
//			PeerInfo info = createPeerInfo(infoHashAsBytes);
			data.putPeerInfo(info001);
			data.putPeerInfo(info002);
			data.removePeerInfo(info002);
//			data.removePeerInfo(info002);
			byte[] expected = {127, 0, 0, 1, 26, -44};

			BenDiction response = TrackerResponse.createResponce(data, info001, 0);
			assertEquals(1800, response.getBenValue(TrackerResponse.KEY_INTERVAL).toInteger());
			assertEquals(BenObject.TYPE_LIST, response.getBenValue(TrackerResponse.KEY_PEERS).getType());
			assertEquals(1, response.getBenValue(TrackerResponse.KEY_PEERS).size());
			BenObject peers = response.getBenValue(TrackerResponse.KEY_PEERS);
			
			assertEquals(BenObject.TYPE_DICT, peers.getBenValue(0).getType());
			assertEquals(BenObject.TYPE_STRI, peers.getBenValue(0).getBenValue(TrackerResponse.KEY_PEER_ID).getType());
			assertEquals(info001.getPeerId(), peers.getBenValue(0).getBenValue(TrackerResponse.KEY_PEER_ID).toString());	
			assertEquals(BenObject.TYPE_STRI, peers.getBenValue(0).getBenValue(TrackerResponse.KEY_IP).getType());	
			assertEquals("127.0.0.1", peers.getBenValue(0).getBenValue(TrackerResponse.KEY_IP).toString());	
			assertEquals(BenObject.TYPE_INTE, peers.getBenValue(0).getBenValue(TrackerResponse.KEY_PORT).getType());
			assertEquals(6868, peers.getBenValue(0).getBenValue(TrackerResponse.KEY_PORT).toInteger());
			
		}
	}

	private void assertEquals(byte[] expected, byte[] actual) {
		System.out.println(""+expected.length+"*"+new String(expected));
		System.out.println(""+actual.length+"*"+new String(actual));

		for(int i=0;i<expected.length;i++) {
			System.out.print(","+expected[i]);
		}
		System.out.println("");
		for(int i=0;i<actual.length;i++) {
			System.out.print(","+actual[i]);
		}
		System.out.println("");

		assertEquals(expected.length, actual.length);
		for(int i=0;i<expected.length;i++) {
			assertEquals(expected[i], actual[i]);
		}
	}

	public void show(byte[] data) {
		StringBuilder builder = new StringBuilder();
		for(int i=0;i<data.length;i++) {
			builder.append(""+data[i]+"("+((int)data[i])+")");
			builder.append(",");
		}
		System.out.println("#"+builder.toString());
	}

	public TrackerDatam createPeerInfo(byte[] targetFileContent) throws IOException {
		String targetFileHash = MetaFile
				.createPieces(new MarkableFileReader(targetFileContent))
				.toPercentString();
		String peerId = TorrentClient.createPeerId();
		HttpRequest uri = HttpRequest.newInstance("dummy", "/adsdannounce", "dummy")
		.putValue(TrackerRequest.KEY_COMPACT, "1")
		.putValue(TrackerRequest.KEY_DOWNLOADED, "1")
		.putValue(TrackerRequest.KEY_UPLOADED, "2")
		.putValue(TrackerRequest.KEY_LEFT, "3")
		.putValue(TrackerRequest.KEY_EVENT, TrackerRequest.EVENT_STARTED)
		.putValue(TrackerRequest.KEY_INFO_HASH, targetFileHash)
		.putValue(TrackerRequest.KEY_PEER_ID, peerId)
		.putValue(TrackerRequest.KEY_PORT, ""+6868);

		TrackerDatam peerinfo = TrackerDatam.createPeerInfo(uri, "127.0.0.1",6868);
		
		return peerinfo;
	}
}
