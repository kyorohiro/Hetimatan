package net.hetimatan.net.torrent.krpc;

import java.io.IOException;

import junit.framework.TestCase;

import net.hetimatan.io.file.KyoroByteOutput;
import net.hetimatan.io.file.KyoroFile;
import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.io.filen.ByteKyoroFile;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.io.filen.CashKyoroFileHelper;

public class TestForRequestPing extends TestCase {
	public void testDecode() throws IOException {
		MarkableReader reader = new MarkableFileReader("d1:ad2:id20:abcdefghij0123456789e1:q4:ping1:t2:aa1:y1:qe".getBytes());
		RequestPing pingRequest = RequestPing.decode(reader);
		assertEquals("aa", pingRequest.getTransactionId());
		assertEquals("abcdefghij0123456789", pingRequest.getId());
	}

	public void testEncode() throws IOException {
		RequestPing ping = new RequestPing("abcdefghij0123456789", "aa");
		CashKyoroFile output = new CashKyoroFile(12*1000);
		ping.encode(output.getLastOutput());
		assertEquals("d1:ad2:id20:abcdefghij0123456789e1:q4:ping1:t2:aa1:y1:qe",
				new String(CashKyoroFileHelper.newBinary(output)));
		output.close();
	}
}
