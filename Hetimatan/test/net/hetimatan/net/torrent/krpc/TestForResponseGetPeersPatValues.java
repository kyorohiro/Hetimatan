package net.hetimatan.net.torrent.krpc;

import java.io.IOException;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.io.filen.CashKyoroFileHelper;
import net.hetimatan.net.torrent.util.bencode.BenList;
import net.hetimatan.net.torrent.util.bencode.BenObject;
import net.hetimatan.net.torrent.util.bencode.BenString;
import junit.framework.TestCase;

public class TestForResponseGetPeersPatValues extends TestCase {

	public void testDecode() throws IOException {
		MarkableReader reader = new MarkableFileReader("d1:rd2:id20:abcdefghij01234567895:token8:aoeusnth6:valuesl6:axje.u6:idhtnmee1:t2:aa1:y1:re".getBytes());
		try {
			ResponseGetPeersPatValues response = ResponseGetPeersPatValues.decode(reader);
			assertEquals("abcdefghij0123456789", response.getId());
			assertEquals(2, response.getValues().size());
			assertEquals("axje.u", response.getValues().getBenValue(0).toString());
			assertEquals("idhtnm", response.getValues().getBenValue(1).toString());
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
		BenList list = new BenList();
		list.append(new BenString("axje.u"));
		list.append(new BenString("idhtnm"));
		ResponseGetPeersPatValues ping = new ResponseGetPeersPatValues(transactionId, id, token, list);
		CashKyoroFile output = new CashKyoroFile(1*1024);
		try {
			ping.encode(output.getLastOutput());
			assertEquals("d1:rd2:id20:abcdefghij01234567895:token8:aoeusnth6:valuesl6:axje.u6:idhtnmee1:t2:aa1:y1:re", new String(CashKyoroFileHelper.newBinary(output)));
		} finally {
			output.close();
		}
	}
}
