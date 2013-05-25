package info.kyorohiro.raider.net.torrent.client;

import info.kyorohiro.raider.net.torrent.client._front.TorrentFrontTargetInfo;
import info.kyorohiro.raider.net.torrent.util.piece.PieceInfo;
import junit.framework.TestCase;

public class TestForTorrentInfo extends TestCase  {

	public void test001() {
		TorrentFrontTargetInfo torrentInfo = new TorrentFrontTargetInfo(16384);
		torrentInfo.request(6, 0, 16384);
		PieceInfo info = torrentInfo.popPieceInfo();
		assertEquals(6*16384, info.getStart());
		assertEquals(7*16384, info.getEnd());

		torrentInfo.request(22, 0, 16384);
		info = torrentInfo.popPieceInfo();
		assertEquals(22*16384, info.getStart());
		assertEquals(23*16384, info.getEnd());

	}
}
