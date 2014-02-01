package net.hetimatan.net.torrent.client;

import net.hetimatan.net.http.PieceInfo;
import net.hetimatan.net.torrent.client._front.TorrentClientFrontTargetInfo;
import junit.framework.TestCase;

public class TestForTorrentInfo extends TestCase  {

	public void test001() {
		TorrentClientFrontTargetInfo torrentInfo = new TorrentClientFrontTargetInfo(16384);
		torrentInfo.taregtRequested(6, 0, 16384);
		PieceInfo info = torrentInfo.popTargetRequestedPieceInfo();
		assertEquals(6*16384, info.getStart());
		assertEquals(7*16384, info.getEnd());

		torrentInfo.taregtRequested(22, 0, 16384);
		info = torrentInfo.popTargetRequestedPieceInfo();
		assertEquals(22*16384, info.getStart());
		assertEquals(23*16384, info.getEnd());

	}
}
