package net.hetimatan.net.torrent.krpc;

import java.io.IOException;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.io.filen.CashKyoroFileHelper;
import net.hetimatan.net.torrent.krpc.message.ResponsePing;
import net.hetimatan.net.torrent.util.bencode.BenString;
import junit.framework.TestCase;

public class TestForResponsePing extends TestCase {

	public void testDecode() throws IOException {
		MarkableReader reader = new MarkableFileReader("d1:rd2:id20:mnopqrstuvwxyz123456e1:t2:aa1:y1:re".getBytes());
		try {
			ResponsePing response = ResponsePing.decode(reader);
			assertEquals("mnopqrstuvwxyz123456", response.getId().toString());
			assertEquals("aa", response.getTransactionId().toString());
		} finally {
			reader.close();
		}
	}

	public void testEncode() throws IOException {
		ResponsePing ping = new ResponsePing(new BenString("aa"), new BenString("mnopqrstuvwxyz123456"));
		CashKyoroFile output = new CashKyoroFile(1*1024);
		try {
			ping.encode(output.getLastOutput());
			assertEquals("d1:rd2:id20:mnopqrstuvwxyz123456e1:t2:aa1:y1:re", new String(CashKyoroFileHelper.newBinary(output)));
		} finally {
			output.close();
		}
	}
	
}
