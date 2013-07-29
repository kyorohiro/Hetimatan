package net.hetimatan.net.torrent.krpc;

import java.io.IOException;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.file.MarkableReader;
import junit.framework.TestCase;

public class TestForResponsePing extends TestCase {

	public void testDecode() throws IOException {
		MarkableReader reader = new MarkableFileReader("d1:rd2:id20:mnopqrstuvwxyz123456e1:t2:aa1:y1:re".getBytes());
		ResponsePing response = ResponsePing.decode(reader);
		assertEquals("mnopqrstuvwxyz123456", response.getId());
		assertEquals("aa", response.getTransactionId());
	}
}
