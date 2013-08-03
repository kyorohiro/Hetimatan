package net.hetimatan.net.torrent.krpc;

import java.io.IOException;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.io.filen.CashKyoroFileHelper;
import junit.framework.TestCase;

public class TestForResponseAnnouncePeer extends TestCase {

	public void testDecode() throws IOException {
		MarkableReader reader = new MarkableFileReader("d1:rd2:id20:mnopqrstuvwxyz123456e1:t2:aa1:y1:re".getBytes());
		try {
			ResponseAnnouncePeer response = ResponseAnnouncePeer.decode(reader);
			assertEquals("mnopqrstuvwxyz123456", response.getId());
			assertEquals("aa", response.getTransactionId());
		} finally {
			reader.close();
		}
	}

	public void testEncode() throws IOException {
		ResponseAnnouncePeer response = new ResponseAnnouncePeer("aa", "mnopqrstuvwxyz123456");
		CashKyoroFile output = new CashKyoroFile(1*1024);
		try {
			response.encode(output.getLastOutput());
			assertEquals("d1:rd2:id20:mnopqrstuvwxyz123456e1:t2:aa1:y1:re", new String(CashKyoroFileHelper.newBinary(output)));
		} finally {
			output.close();
		}
	}
	
}
