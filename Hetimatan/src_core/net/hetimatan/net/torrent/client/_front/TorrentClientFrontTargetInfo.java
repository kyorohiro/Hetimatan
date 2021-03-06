package net.hetimatan.net.torrent.client._front;

import net.hetimatan.net.http.PieceInfo;
import net.hetimatan.net.http.PieceInfoList;
import net.hetimatan.net.torrent.client.TorrentClientFront;
import net.hetimatan.util.bitfield.BitField;

public class TorrentClientFrontTargetInfo {
	public BitField mTargetBitField = null;
	public PieceInfoList mRequestList = new PieceInfoList();

	private boolean mTargetInterested = false;
	private int mTargetChoked = TorrentClientFront.NONE;
	private long mTargetUploaded = 0;
	private long mTargetDownloaded = 0;
	private long mFrontStartRequestTime = 0;
	private long mFrontRequestTime = 0;

	
	private int mPieceLength = 0;
	public TorrentClientFrontTargetInfo(int pieceLength) {
		mPieceLength = pieceLength;
	}

	public void updateFrontRequestTime() {
		if(mFrontStartRequestTime == 0) {
			mFrontStartRequestTime = System.currentTimeMillis();
			return;
		}
		long curTime = System.currentTimeMillis();
		mFrontRequestTime += curTime-mFrontStartRequestTime;
		mFrontStartRequestTime= curTime;
	}

	
	public void taregtRequested(int index, int start, int end) {
		long pieceStart = (long)index*(long)mPieceLength;
		mRequestList.append(pieceStart+start, pieceStart+end);
	}

	public long getTargetUploadded() {
		return mTargetUploaded;
	}

	public long getTargetDownloaded() {
		return mTargetDownloaded;
	}

	public long getFrontReuqstedTime() {
		return mFrontRequestTime;
	}

	public void updateTargetUploaded(long sizePerByte) {
		mTargetUploaded += sizePerByte;
		updateFrontRequestTime();
	}

	public void updateTargetDownloaded(long sizePerByte) {
		mTargetUploaded += sizePerByte;
	}

	public BitField getBitField() {
		return mTargetBitField;
	}

	public int getPieceLength() {
		return mPieceLength;
	}

	public int numOfPiece() {
		return mRequestList.size();
	}

	public PieceInfo popTargetRequestedPieceInfo() {
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

	public boolean isInterested() {
		return mTargetInterested;
	}

	public void isInterested(boolean v) {
		mTargetInterested = v;
	}

	public int isChoked() {
		return mTargetChoked;
	}

	public void isChoke(boolean v) {
		if(v) {
			mTargetChoked = TorrentClientFront.TRUE;
		} else {
			mTargetChoked = TorrentClientFront.FALSE;			
		}
	}

}
