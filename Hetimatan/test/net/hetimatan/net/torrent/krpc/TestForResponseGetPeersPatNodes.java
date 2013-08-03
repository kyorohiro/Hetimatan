package net.hetimatan.net.torrent.krpc;

import java.io.IOException;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.io.filen.CashKyoroFileHelper;
import junit.framework.TestCase;

public class TestForResponseGetPeersPatNodes extends TestCase {

	public void testDecode() throws IOException {
		MarkableReader reader = new MarkableFileReader("d1:rd2:id20:abcdefghij01234567895:nodes26:A123456789B123456789C123455:token8:aoeusnthe1:t2:aa1:y1:re".getBytes());
		try {
			ResponseGetPeersPatNodes response = ResponseGetPeersPatNodes.decode(reader);
			assertEquals("abcdefghij0123456789", response.getId());
			assertEquals("A123456789B123456789C12345", response.getNodes());
			assertEquals("aoeusnth", response.getToken());
			assertEquals("aa", response.getTransactionId());
		} finally {
			reader.close();
		}
	}

	public void testEncode() throws IOException {
		String transactionId = "aa";
		String id = "abcdefghij0123456789";
		String token = "aoeusnth";
		String nodes = "A123456789B123456789C12345";
		ResponseGetPeersPatNodes ping = new ResponseGetPeersPatNodes(transactionId, id, token, nodes);
		CashKyoroFile output = new CashKyoroFile(1*1024);
		try {
			ping.encode(output.getLastOutput());
			assertEquals("d1:rd2:id20:abcdefghij01234567895:nodes26:A123456789B123456789C123455:token8:aoeusnthe1:t2:aa1:y1:re", new String(CashKyoroFileHelper.newBinary(output)));
		} finally {
			output.close();
		}
	}

}
