package net.hetimatan.net.torrent.krpc;

import java.io.IOException;

import junit.framework.TestCase;
import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.io.filen.CashKyoroFileHelper;
import net.hetimatan.net.torrent.krpc.message.QueryAnnouncePeer;
import net.hetimatan.net.torrent.util.bencode.BenString;

public class TestForQueryAnnouncePeer extends TestCase {
	public void testDecode() throws IOException {
		MarkableReader reader = new MarkableFileReader(
				"d1:ad2:id20:abcdefghij01234567899:info_hash20:mnopqrstuvwxyz1234564:porti6881e5:token8:aoeusnthe1:q13:announce_peer1:t2:aa1:y1:qe".getBytes());
		try {
			QueryAnnouncePeer query = QueryAnnouncePeer.decode(reader);
			assertEquals("aa", query.getTransactionId().toString());
			assertEquals("abcdefghij0123456789", query.getId().toString());
			assertEquals("mnopqrstuvwxyz123456", query.getInfoHash().toString());
			assertEquals(6881, query.getPort());
			assertEquals("aoeusnth", query.getToken().toString());
		} finally {
			reader.close();
		}
	}

	public void testEncode() throws IOException {
		QueryAnnouncePeer query = new QueryAnnouncePeer(new BenString("aa"), new BenString("abcdefghij0123456789"), new BenString("mnopqrstuvwxyz123456"), 6881, new BenString("aoeusnth"));
		CashKyoroFile output = new CashKyoroFile(12*1000);
		try {
			query.encode(output.getLastOutput());
			assertEquals("d1:ad2:id20:abcdefghij01234567899:info_hash20:mnopqrstuvwxyz1234564:porti6881e5:token8:aoeusnthe1:q13:announce_peer1:t2:aa1:y1:qe",
					new String(CashKyoroFileHelper.newBinary(output)));
		} finally {
			output.close();
		}
	}

}
