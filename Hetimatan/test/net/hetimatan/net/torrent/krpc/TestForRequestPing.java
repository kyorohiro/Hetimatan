package net.hetimatan.net.torrent.krpc;

import java.io.IOException;

import junit.framework.TestCase;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.file.MarkableReader;

public class TestForRequestPing extends TestCase {
	public void testEncode() throws IOException {
		MarkableReader reader = new MarkableFileReader("d1:ad2:id20:abcdefghij0123456789e1:q4:ping1:t2:aa1:y1:qe".getBytes());
		RequestPing pingRequest = RequestPing.decode(reader);
		assertEquals("aa", pingRequest.getTransactionId());
		assertEquals("abcdefghij0123456789", pingRequest.getId());
	}
}
