package net.hetimatan.net.torrent.krpc;

import info.kyorohiro.raider.util.TestUtil;

import java.io.IOException;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.io.filen.CashKyoroFileHelper;
import net.hetimatan.net.torrent.krpc.message.ResponseFindNode;
import net.hetimatan.net.torrent.util.bencode.BenString;
import net.hetimatan.util.http.HttpObject;
import net.hetimatan.util.io.ByteArrayBuilder;
import junit.framework.TestCase;

public class TestForResponseFindNode extends TestCase {

	public void testDecode() throws IOException {
		MarkableReader reader = new MarkableFileReader("d1:rd2:id20:0123456789abcdefghij5:nodes26:A123456789B123456789C12345e1:t2:aa1:y1:re".getBytes());
		try {
			ResponseFindNode response = ResponseFindNode.decode(reader);
			assertEquals("0123456789abcdefghij", response.getId().toString());
			assertEquals("A123456789B123456789C12345", response.getNodes().toString());
			assertEquals("aa", response.getTransactionId().toString());
		} finally {
			reader.close();
		}
	}

	public void testEncode() throws IOException {
		BenString transactionId = new BenString("aa");
		BenString id = new BenString("0123456789abcdefghij");
		BenString nodes = new BenString("A123456789B123456789C12345");
		ResponseFindNode ping = new ResponseFindNode(transactionId, id, nodes);
		CashKyoroFile output = new CashKyoroFile(1*1024);
		try {
			ping.encode(output.getLastOutput());
			assertEquals("d1:rd2:id20:0123456789abcdefghij5:nodes26:A123456789B123456789C12345e1:t2:aa1:y1:re", new String(CashKyoroFileHelper.newBinary(output)));
		} finally {
			output.close();
		}
	}

	public void testNodeInfo() throws IOException {
		BenString transactionId = new BenString("aa");
		BenString id = new BenString("0123456789abcdefghij");
		byte[] nodesBuff = new byte[26*3];
		System.arraycopy("a123456789b123456789".getBytes(), 0, nodesBuff, 0, 20);
		System.arraycopy("c123456789d123456789".getBytes(), 0, nodesBuff, 26*1, 20);
		System.arraycopy("e123456789f123456789".getBytes(), 0, nodesBuff, 26*2, 20);
		System.arraycopy(HttpObject.aton("127.0.0.1"), 0, nodesBuff, 20, 4);
		System.arraycopy(HttpObject.aton("127.0.1.2"), 0, nodesBuff, 20+26*1, 4);
		System.arraycopy(HttpObject.aton("255.0.0.1"), 0, nodesBuff, 20+26*2, 4);

		System.arraycopy(ByteArrayBuilder.parseShort(8080, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN), 0, nodesBuff, 4+20, 2);
		System.arraycopy(ByteArrayBuilder.parseShort(18080, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN), 0, nodesBuff, 4+20+26*1, 2);
		System.arraycopy(ByteArrayBuilder.parseShort(28080, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN), 0, nodesBuff, 4+20+26*2, 2);

		assertEquals(8080, ByteArrayBuilder.parseShort(ByteArrayBuilder.parseShort(8080, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN), 0, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN));
		BenString nodes = new BenString(nodesBuff, 0, nodesBuff.length, "utf8");
		ResponseFindNode response = new ResponseFindNode(transactionId, id, nodes);
		CashKyoroFile output = new CashKyoroFile(1*1024);
		try {
			assertEquals(3, response.numOfNode());
			TestUtil.assertArrayEquals(this, "a123456789b123456789", "a123456789b123456789".getBytes(), response.getNodeId(0));
			TestUtil.assertArrayEquals(this, "c123456789d123456789", "c123456789d123456789".getBytes(), response.getNodeId(1));
			TestUtil.assertArrayEquals(this, "e123456789f123456789", "e123456789f123456789".getBytes(), response.getNodeId(2));
			TestUtil.assertArrayEquals(this, "", new byte[]{(byte)127,(byte)0,(byte)0,(byte)1}, response.getNodeIP(0));
			TestUtil.assertArrayEquals(this, "", new byte[]{(byte)127,(byte)0,(byte)1,(byte)2}, response.getNodeIP(1));
			TestUtil.assertArrayEquals(this, "", new byte[]{(byte)255,(byte)0,(byte)0,(byte)1}, response.getNodeIP(2));

			assertEquals(8080, response.getNodePort(0));
			assertEquals(18080, response.getNodePort(1));
			assertEquals(28080, response.getNodePort(2));

		} finally {
			output.close();
		}
		
	}
}
