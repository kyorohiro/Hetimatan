package net.hetimatan.net.torrent.client._front;

import net.hetimatan.net.torrent.client.TorrentFront;
import net.hetimatan.net.torrent.util.piece.PieceInfo;
import net.hetimatan.net.torrent.util.piece.PieceInfoList;
import net.hetimatan.util.bitfield.BitField;

public class TorrentFrontTargetInfo {
	public BitField mTargetBitField = null;
	public boolean mTargetInterested = false;
	private int mTargetChoked = TorrentFront.NONE;
	public PieceInfoList mRequestList = new PieceInfoList();

	private int mPieceLength = 0;
	public TorrentFrontTargetInfo(int pieceLength) {
		mPieceLength = pieceLength;
	}

	public void request(int index, int start, int end) {
		long pieceStart = (long)index*(long)mPieceLength;
		mRequestList.append(pieceStart+start, pieceStart+end);
	}

	public int getPieceLength() {
		return mPieceLength;
	}

	public int numOfPiece() {
		return mRequestList.size();
	}

	public PieceInfo popPieceInfo() {
		if(mRequestList.size()<=0) {
			return new PieceInfo(0, 0);
		}
		PieceInfo pieceInfo = mRequestList.getPieceInfo(0);
		int index = (int)(pieceInfo.getStart()/mPieceLength);
		long start = pieceInfo.getStart();
		long end = pieceInfo.getEnd();
		long _end = (index+1) * mPieceLength;
		if(_end>end) {
			_end = end;
		}
		mRequestList.remove(start, _end);
		return new PieceInfo(start, _end);
	}

	public boolean haveRequest() {
		if(mRequestList.size() == 0) {
			return false;
		} else {
			return true;
		}
	}

	public int isChoked() {
		return mTargetChoked;
	}

	public void isChoke(boolean v) {
		if(v) {
			mTargetChoked = TorrentFront.TRUE;
		} else {
			mTargetChoked = TorrentFront.FALSE;			
		}
	}
}
