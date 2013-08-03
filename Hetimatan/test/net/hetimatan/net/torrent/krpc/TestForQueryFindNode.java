package net.hetimatan.net.torrent.krpc;

import java.io.IOException;

import junit.framework.TestCase;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.io.filen.CashKyoroFileHelper;
import net.hetimatan.net.torrent.krpc.message.QueryFindNode;

public class TestForQueryFindNode extends TestCase {
	public void testDecode() throws IOException {
		MarkableReader reader = new MarkableFileReader(
				"d1:ad2:id20:abcdefghij01234567896:target20:mnopqrstuvwxyz123456e1:q9:find_node1:t2:aa1:y1:qe".getBytes());
		try {
			QueryFindNode query = QueryFindNode.decode(reader);
			assertEquals("aa", query.getTransactionId());
			assertEquals("abcdefghij0123456789", query.getId());
			assertEquals("mnopqrstuvwxyz123456", query.getTarget());
		} finally {
			reader.close();
		}
	}

	public void testEncode() throws IOException {
		QueryFindNode query = new QueryFindNode("aa", "abcdefghij0123456789", "mnopqrstuvwxyz123456");
		CashKyoroFile output = new CashKyoroFile(12*1000);
		try {
			query.encode(output.getLastOutput());
			assertEquals("d1:ad2:id20:abcdefghij01234567896:target20:mnopqrstuvwxyz123456e1:q9:find_node1:t2:aa1:y1:qe",
					new String(CashKyoroFileHelper.newBinary(output)));
		} finally {
			output.close();
		}
	}

}
