package info.kyorohiro.raider.util.torrent.tracker.server;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.net.http.request.HttpGetRequester;
import net.hetimatan.net.http.request.HttpGetResponse;
import net.hetimatan.net.torrent.tracker.TrackerRequest;
import net.hetimatan.net.torrent.tracker.TrackerServer;
import net.hetimatan.util.http.HttpObject;
import net.hetimatan.util.http.HttpRequestLine;
import net.hetimatan.util.io.ByteArrayBuilder;
import net.hetimatan.util.url.PercentEncoder;

import junit.framework.TestCase;


public class TestForTrackerServer extends TestCase {

	public void testHello() {
		assertTrue(tcpPortIsUseable(TrackerServer.DEFAULT_TRACKER_PORT));		
	}

	public void testGet001() throws InterruptedException, IOException {
		TrackerServer server = null;
		HttpGetRequester requester = null;
		try {
			server = new TrackerServer();
			server.setPort(TrackerServer.DEFAULT_TRACKER_PORT);
			server.startServer(null);

			while(!server.isBinded()){Thread.yield();}
			// create request 
			TrackerRequest builder = newBuilderTrackerRequest(1);

			// set server address
			requester = builder
			.createHttpGetRequester();
			requester.getUrlBuilder()
			.setHost("127.0.0.1")
			.setPort(TrackerServer.DEFAULT_TRACKER_PORT);

			System.out.println("###doRequest");
			// done
			HttpGetResponse response = requester.doRequest();

			
		} finally {
			server.close();
		}
	}

	public void testGet002_unmanaged_InfoHash() throws InterruptedException, IOException {
		TrackerServer server = null;
		HttpGetRequester requester = null;
		
		byte[] serverHash = "abc".getBytes();
		byte[] clientHash = "def".getBytes();
		PercentEncoder encoder = new PercentEncoder();

		try {
			server = new TrackerServer();
			server.setPort(TrackerServer.DEFAULT_TRACKER_PORT);
			server.startServer(null);
			server.addData(serverHash);
			while(!server.isBinded()){Thread.yield();}

			// create request 
			TrackerRequest builder = newBuilderTrackerRequest(1);
			builder.putInfoHash(encoder.encode(clientHash));

			// set server address
			requester = builder
			.createHttpGetRequester();
			requester.getUrlBuilder()

			.setHost("127.0.0.1")
			.setPort(TrackerServer.DEFAULT_TRACKER_PORT);

			// done
			HttpGetResponse response = requester.doRequest();
			System.out.println("offset::"+response.getVFOffset());
			CashKyoroFile vf = response.getVF();
			byte[] buffer = null;
			try {
				vf.seek(response.getVFOffset());
				buffer = new byte[(int)(vf.length()-response.getVFOffset())];
				vf.read(buffer);
			} finally {
				response.close();
				vf.close();
			}
			assertEquals("d14:failure_reason28:your ainfo_hash is unmanagede", new String(buffer, 0, buffer.length));
		} finally {
			server.close();
		}
	}


	public void testGet003_firstAccess() throws InterruptedException, IOException {
		TrackerServer server = null;
		HttpGetRequester requester = null;

		byte[] serverHash = "abc".getBytes();
		byte[] clientHash = "abc".getBytes();
		PercentEncoder encoder = new PercentEncoder();

		try {
			server = new TrackerServer();
			server.setPort(TrackerServer.DEFAULT_TRACKER_PORT);
			server.startServer(null);
			server.addData(serverHash);
			while(!server.isBinded()){Thread.yield();}

			// create request 
			TrackerRequest builder = newBuilderTrackerRequest(1);
			builder.putInfoHash(encoder.encode(clientHash));

			// set server address
			requester = builder
			.createHttpGetRequester();
			requester.getUrlBuilder()
			.setHost("127.0.0.1")
			.setPort(TrackerServer.DEFAULT_TRACKER_PORT);

			// done
			HttpGetResponse response = requester.doRequest();
			CashKyoroFile vf = response.getVF();
			byte[] buffer = null;
			try {
				vf.seek(response.getVFOffset());
				buffer = new byte[(int)(vf.length()-response.getVFOffset())];
				vf.read(buffer);
			} finally {
				vf.isCashMode(false);
				vf.syncWrite();
				response.close();
			}

			System.out.println("##"+new String(buffer,0,buffer.length));

			ByteArrayBuilder expected = new ByteArrayBuilder();
//			expected.append("d8:intervali1800e10:tracker id38:%9A%08%FFA%5D%E5%DFEqS%11%B6I%F6%EA%8F5:peers6:".getBytes());
			expected.append("d8:intervali1800e8:completei0e10:incompletei1e5:peers6:".getBytes());
			ByteBuffer bbuf = ByteBuffer.allocate(6);
			bbuf.order(ByteOrder.BIG_ENDIAN);
			bbuf.put(HttpObject.aton("127.0.0.1"),0 ,4);
			bbuf.putShort((short)6861);
			expected.append(bbuf.array());
			expected.append("e".getBytes());
			byte[] expectedBuffer = new byte[expected.length()];
			System.arraycopy(expected.getBuffer(), 0,
					expectedBuffer, 0, expected.length());
			assertEquals(expectedBuffer, buffer);
		} finally {
			server.close();
		}
	}

	public void testGet003_compactOff() throws InterruptedException, IOException {
		testHello();
		TrackerServer server = null;
		HttpGetRequester requester = null;

		byte[] serverHash = "abc".getBytes();
		byte[] clientHash = "abc".getBytes();
		PercentEncoder encoder = new PercentEncoder();

		try {
			server = new TrackerServer();
			server.setPort(TrackerServer.DEFAULT_TRACKER_PORT);
			server.startServer(null);
			server.addData(serverHash);
			while(!server.isBinded()){Thread.yield();}

			// create request 
			TrackerRequest builder = newBuilderTrackerRequest(0);
			builder.putInfoHash(encoder.encode(clientHash));

			// set server address

			requester = builder
			.createHttpGetRequester();
			requester.getUrlBuilder()
			.setHost("127.0.0.1")
			.setPort(TrackerServer.DEFAULT_TRACKER_PORT);

			// done
			HttpGetResponse response = requester.doRequest();
			CashKyoroFile vf = response.getVF();
			byte[] buffer = null;
			try {
				vf.seek(response.getVFOffset());
				buffer = new byte[(int)(vf.length()-response.getVFOffset())];
				vf.read(buffer);
			} finally {
				vf.isCashMode(false);
				vf.syncWrite();
				response.close();
			}

			System.out.println("##"+new String(buffer,0,buffer.length));
			assertEquals("d8:intervali1800e8:completei0e10:incompletei1e5:peersld7:peer_id4:xxxx2:ip9:127.0.0.14:porti6861eeee", new String(buffer,0,buffer.length));
		} finally {
			server.close();
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
		for(int i=0;i<expected.length||i<actual.length;i++) {
			assertEquals(expected[i], actual[i]);
		}
	}


	private TrackerRequest newBuilderTrackerRequest(int compact) {
		return new TrackerRequest()
		.putPath("/announce")
		.putHttpVersion(HttpRequestLine.HTTP10)
		.putCompact(compact)
		.putEvent(TrackerRequest.EVENT_STARTED)
		.putTrackerHost("127.0.0.1") //client ip
		.putHeaderUserAgent("Raider/1.0")
		.putInfoHash("NNNNNNNNN")
		.putPeerId("xxxx")
//		.putPort(6861)
		.putClientPort(6861)
//		builder.putTrackerId(trackerId)
		.putDownloaded(0)
		.putLeft(0)
		.putUploaded(0);
	}


	public static boolean tcpPortIsUseable(int port) {
		try { 
			Socket s = new Socket("localhost", port);
			s.close();
			return false;
		} catch(IOException e) {
			return true;
		}
	}
}
