package net.hetimatan.net.torrent.krpc;

import java.io.IOException;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.io.filen.CashKyoroFileHelper;
import junit.framework.TestCase;

public class TestForResponseFindNode extends TestCase {

	public void testDecode() throws IOException {
		MarkableReader reader = new MarkableFileReader("d1:rd2:id20:0123456789abcdefghij5:nodes26:A123456789B123456789C12345e1:t2:aa1:y1:re".getBytes());
		try {
			ResponseFindNode response = ResponseFindNode.decode(reader);
			assertEquals("0123456789abcdefghij", response.getId());
			assertEquals("A123456789B123456789C12345", response.getNodes());
			assertEquals("aa", response.getTransactionId());
		} finally {
			reader.close();
		}
	}

	public void testEncode() throws IOException {
		String transactionId = "aa";
		String id = "0123456789abcdefghij";
		String nodes = "A123456789B123456789C12345";
		ResponseFindNode ping = new ResponseFindNode(transactionId, id, nodes);
		CashKyoroFile output = new CashKyoroFile(1*1024);
		try {
			ping.encode(output.getLastOutput());
			assertEquals("d1:rd2:id20:0123456789abcdefghij5:nodes26:A123456789B123456789C12345e1:t2:aa1:y1:re", new String(CashKyoroFileHelper.newBinary(output)));
		} finally {
			output.close();
		}
	}

}
