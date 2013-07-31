package net.hetimatan.net.torrent.krpc;

import java.io.IOException;

import junit.framework.TestCase;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.io.filen.CashKyoroFileHelper;

public class TestForQueryAnnouncePeer extends TestCase {
	public void testDecode() throws IOException {
		MarkableReader reader = new MarkableFileReader(
				"d1:ad2:id20:abcdefghij01234567899:info_hash20:mnopqrstuvwxyz1234564:porti6881e5:token8:aoeusnthe1:q13:announce_peer1:t2:aa1:y1:qe".getBytes());
		try {
			QueryAnnouncePeer query = QueryAnnouncePeer.decode(reader);
			assertEquals("aa", query.getTransactionId());
			assertEquals("abcdefghij0123456789", query.getId());
			assertEquals("mnopqrstuvwxyz123456", query.getInfoHash());
			assertEquals(6881, query.getPort());
			assertEquals("aoeusnth", query.getToken());
		} finally {
			reader.close();
		}
	}

	public void testEncode() throws IOException {
		QueryAnnouncePeer query = new QueryAnnouncePeer("aa", "abcdefghij0123456789", "mnopqrstuvwxyz123456", 6881, "aoeusnth");
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
