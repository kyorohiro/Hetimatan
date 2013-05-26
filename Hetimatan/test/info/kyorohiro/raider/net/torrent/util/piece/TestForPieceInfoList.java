package info.kyorohiro.raider.net.torrent.util.piece;

import net.hetimatan.net.torrent.util.piece.PieceInfo;
import net.hetimatan.net.torrent.util.piece.PieceInfoList;
import junit.framework.TestCase;

public class TestForPieceInfoList extends TestCase {

	public void testAppend() {
		PieceInfoList infolist = new PieceInfoList();
		{
			infolist.append(10, 20);
			assertEquals(infolist.size(), 1);
			PieceInfo info000 = infolist.getPieceInfo(0);
			assertEquals(10, info000.getStart());
			assertEquals(20, info000.getEnd());
		}
		{
			infolist.append(21, 30);
			assertEquals(infolist.size(), 2);
			PieceInfo info000 = infolist.getPieceInfo(0);
			assertEquals(10, info000.getStart());
			assertEquals(20, info000.getEnd());
			PieceInfo info001 = infolist.getPieceInfo(1);
			assertEquals(21, info001.getStart());
			assertEquals(30, info001.getEnd());
		}

		{
			infolist.append(30, 40);
			assertEquals(infolist.size(), 2);
			PieceInfo info000 = infolist.getPieceInfo(0);
			assertEquals(10, info000.getStart());
			assertEquals(20, info000.getEnd());
			PieceInfo info001 = infolist.getPieceInfo(1);
			assertEquals(21, info001.getStart());
			assertEquals(40, info001.getEnd());
		}
		{
			infolist.append(15, 25);
			assertEquals(infolist.size(), 1);
			PieceInfo info000 = infolist.getPieceInfo(0);
			assertEquals(10, info000.getStart());
			assertEquals(40, info000.getEnd());
		}
		{
			infolist.append(15, 50);
			assertEquals(infolist.size(), 1);
			PieceInfo info000 = infolist.getPieceInfo(0);
			assertEquals(10, info000.getStart());
			assertEquals(50, info000.getEnd());
		}
		{
			infolist.append(15, 20);
			assertEquals(infolist.size(), 1);
			PieceInfo info000 = infolist.getPieceInfo(0);
			assertEquals(10, info000.getStart());
			assertEquals(50, info000.getEnd());
		}
	}

	public void testRemove() {
		PieceInfoList infolist = new PieceInfoList();
		infolist.append(10, 20);
		infolist.append(21, 30);
		infolist.append(40, 50);
		{
			assertEquals(infolist.size(), 3);
			PieceInfo info000 = infolist.getPieceInfo(0);
			assertEquals(10, info000.getStart());
			assertEquals(20, info000.getEnd());
			PieceInfo info001 = infolist.getPieceInfo(1);
			assertEquals(21, info001.getStart());
			assertEquals(30, info001.getEnd());
			PieceInfo info002 = infolist.getPieceInfo(2);
			assertEquals(40, info002.getStart());
			assertEquals(50, info002.getEnd());
		}
		{
			infolist.remove(12, 18);
			assertEquals(infolist.size(), 4);
			PieceInfo info000a = infolist.getPieceInfo(0);
			assertEquals(10, info000a.getStart());
			assertEquals(12, info000a.getEnd());
			PieceInfo info000b = infolist.getPieceInfo(1);
			assertEquals(18, info000b.getStart());
			assertEquals(20, info000b.getEnd());

			PieceInfo info001 = infolist.getPieceInfo(2);
			assertEquals(21, info001.getStart());
			assertEquals(30, info001.getEnd());
			PieceInfo info002 = infolist.getPieceInfo(3);
			assertEquals(40, info002.getStart());
			assertEquals(50, info002.getEnd());	
		}
	
		{
			infolist.remove(10, 12);
			assertEquals(infolist.size(), 3);
			PieceInfo info000b = infolist.getPieceInfo(0);
			assertEquals(18, info000b.getStart());
			assertEquals(20, info000b.getEnd());

			PieceInfo info001 = infolist.getPieceInfo(1);
			assertEquals(21, info001.getStart());
			assertEquals(30, info001.getEnd());
			PieceInfo info002 = infolist.getPieceInfo(2);
			assertEquals(40, info002.getStart());
			assertEquals(50, info002.getEnd());	
		}
		{
			infolist.remove(19, 25);
			assertEquals(infolist.size(), 3);
			PieceInfo info000b = infolist.getPieceInfo(0);
			assertEquals(18, info000b.getStart());
			assertEquals(19, info000b.getEnd());

			PieceInfo info001 = infolist.getPieceInfo(1);
			assertEquals(25, info001.getStart());
			assertEquals(30, info001.getEnd());
			PieceInfo info002 = infolist.getPieceInfo(2);
			assertEquals(40, info002.getStart());
			assertEquals(50, info002.getEnd());	
		}

		{
			infolist.remove(10, 22);
			assertEquals(infolist.size(), 2);
			PieceInfo info001 = infolist.getPieceInfo(0);
			assertEquals(25, info001.getStart());
			assertEquals(30, info001.getEnd());
			PieceInfo info002 = infolist.getPieceInfo(1);
			assertEquals(40, info002.getStart());
			assertEquals(50, info002.getEnd());	
		}

		{
			infolist.remove(27, 45);
			assertEquals(infolist.size(), 2);
			PieceInfo info001 = infolist.getPieceInfo(0);
			assertEquals(25, info001.getStart());
			assertEquals(27, info001.getEnd());
			PieceInfo info002 = infolist.getPieceInfo(1);
			assertEquals(45, info002.getStart());
			assertEquals(50, info002.getEnd());	
		}

	}

	public void testRemovePB() {
		{
			PieceInfoList infolist = new PieceInfoList();
			infolist.append(10, 20);

			infolist.remove(11, 19);
			assertEquals(infolist.size(), 2);

			PieceInfo info000 = infolist.getPieceInfo(0);
			assertEquals(10, info000.getStart());
			assertEquals(11, info000.getEnd());

			PieceInfo info001 = infolist.getPieceInfo(1);
			assertEquals(19, info001.getStart());
			assertEquals(20, info001.getEnd());

		}

	}

	public void testRemovePC() {
		{
			PieceInfoList infolist = new PieceInfoList();
			infolist.append(10, 20);
			assertEquals(infolist.size(), 1);
			PieceInfo info000 = infolist.getPieceInfo(0);
			assertEquals(10, info000.getStart());
			assertEquals(20, info000.getEnd());
			infolist.remove(10, 20);
			assertEquals(infolist.size(), 0);
		}
		{
			PieceInfoList infolist = new PieceInfoList();
			infolist.append(10, 20);
			infolist.append(20, 30);

			infolist.remove(9, 20);
			assertEquals(infolist.size(), 1);

			PieceInfo info000 = infolist.getPieceInfo(0);
			assertEquals(20, info000.getStart());
			assertEquals(30, info000.getEnd());
		}

		{
			PieceInfoList infolist = new PieceInfoList();
			infolist.append(10, 20);
			infolist.append(22, 30);

			infolist.remove(10, 21);
			assertEquals(infolist.size(), 1);

			PieceInfo info000 = infolist.getPieceInfo(0);
			assertEquals(22, info000.getStart());
			assertEquals(30, info000.getEnd());
		}

		{
			PieceInfoList infolist = new PieceInfoList();
			infolist.append(10, 20);
			infolist.append(22, 30);

			infolist.remove(9, 21);
			assertEquals(infolist.size(), 1);

			PieceInfo info000 = infolist.getPieceInfo(0);
			assertEquals(22, info000.getStart());
			assertEquals(30, info000.getEnd());
		}
	}

	public void testRemovePA() {
		{
			PieceInfoList infolist = new PieceInfoList();
			infolist.append(10, 20);
			infolist.append(22, 30);

			infolist.remove(9, 21);
			assertEquals(infolist.size(), 1);

			PieceInfo info000 = infolist.getPieceInfo(0);
			assertEquals(22, info000.getStart());
			assertEquals(30, info000.getEnd());
		}
		{
			PieceInfoList infolist = new PieceInfoList();
			infolist.append(10, 20);
			infolist.append(22, 30);

			infolist.remove(11, 25);
			assertEquals(infolist.size(), 2);

			PieceInfo info000 = infolist.getPieceInfo(0);
			assertEquals(10, info000.getStart());
			assertEquals(11, info000.getEnd());

			PieceInfo info001 = infolist.getPieceInfo(1);
			assertEquals(25, info001.getStart());
			assertEquals(30, info001.getEnd());
		}

		{
			PieceInfoList infolist = new PieceInfoList();
			infolist.append(10, 20);
			infolist.append(22, 30);

			infolist.remove(25, 35);
			assertEquals(infolist.size(), 2);

			PieceInfo info000 = infolist.getPieceInfo(0);
			assertEquals(10, info000.getStart());
			assertEquals(20, info000.getEnd());

			PieceInfo info001 = infolist.getPieceInfo(1);
			assertEquals(22, info001.getStart());
			assertEquals(25, info001.getEnd());
		}

		{
			PieceInfoList infolist = new PieceInfoList();
			infolist.append(10, 20);
			infolist.append(22, 30);

			infolist.remove(5, 35);
			assertEquals(infolist.size(), 0);
		}

		{
			PieceInfoList infolist = new PieceInfoList();
			infolist.append(10, 20);
			infolist.append(22, 30);

			infolist.remove(9, 25);
			assertEquals(infolist.size(), 1);

			PieceInfo info000 = infolist.getPieceInfo(0);
			assertEquals(25, info000.getStart());
			assertEquals(30, info000.getEnd());
		}
	}

	public void testExtra_story() {
		PieceInfoList infolist = new PieceInfoList();
		{
			infolist.append(6*16384, 7*16384);
			assertEquals(infolist.size(), 1);
			PieceInfo info000 = infolist.getPieceInfo(0);
			assertEquals(6*16384, info000.getStart());
			assertEquals(7*16384, info000.getEnd());
			infolist.remove(6*16384, 7*16384);
			assertEquals(infolist.size(), 0);

			infolist.append(22*16384, 23*16384);
			assertEquals(infolist.size(), 1);
			info000 = infolist.getPieceInfo(0);
			assertEquals(22*16384, info000.getStart());
			assertEquals(23*16384, info000.getEnd());
			infolist.remove(22*16384, 23*16384);
			assertEquals(infolist.size(), 0);

		}
	}
}
