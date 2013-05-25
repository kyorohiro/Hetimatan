package net.hetimatan.net.torrent.client;

import java.io.File;
import java.io.IOException;

import net.hetimatan.io.file.KyoroFile;
import net.hetimatan.io.filen.KFNextHelper;
import net.hetimatan.io.filen.RACashFile;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.util.bitfield.BitField;


public class TorrentData {
	private BitField mStockedDataInfo = null;
	private String mInfoHash = "_";
	private RACashFile mCash = null;
	private int mPieceLength =0;
	private File mContent = null;
	private long mDataLength = 0;

	public TorrentData(MetaFile file) throws IOException{//(int pieceLength) {
		int numOfPiece = file.numOfPiece();
		mPieceLength =(int)file.getPieceLength();
		mStockedDataInfo = new BitField(numOfPiece);
		mInfoHash = file.getInfoSha1AsPercentString();
		File dir = getPieceDir();
		dir.mkdirs();
		mContent = new File(dir, "_content");
		mCash = new RACashFile(mContent, 256, 10);
		for(Long l:file.getFileLengths()) {
			mDataLength += l;
		}
		mStockedDataInfo.zeroClear();
	}

	public boolean isComplete() {
		return mStockedDataInfo.isAllOn();
	}

	public long getDataLength() {
		return mDataLength;
	}

	public void setMaster(File[] srcs) throws IOException {
		int len = srcs.length;
		if(srcs.length == 0) {
			//todo clear bitfiled per srcs length
			return;
		}
		long fp = mCash.getFilePointer();
		try {
			KFNextHelper.xcopy(srcs, mCash);
			mStockedDataInfo.oneClear();
			mCash.syncWrite();
		} finally {
			for(File f: srcs) {
				len += f.length();
			}
			mCash.seek(fp+len);
		}
	}

	public BitField getStockedDataInfo() {
		return mStockedDataInfo;
	}

	public KyoroFile getPiece(int index) throws IOException {
		return  KFNextHelper.subSequence(mCash, index*mPieceLength,(index+1)*mPieceLength);
	}

	public KyoroFile getPiece(long start, long end) throws IOException {
		return  KFNextHelper.subSequence(mCash, start, end);
	}

	public void setPiece(int index, KyoroFile content) throws IOException {
		mCash.seek(index*mPieceLength);
		content.seek(0);
		KFNextHelper.copy(content, mCash);
		mStockedDataInfo.isOn(index, true);
		//following code is for test
		mCash.syncWrite();
	}

	public File getPieceDir() {
		return new File(mInfoHash);
	}

}
