package net.hetimatan.net.torrent.client;

import java.io.File;
import java.io.IOException;

import net.hetimatan.io.file.KyoroFile;
import net.hetimatan.io.filen.CashKyoroFileHelper;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.util.bitfield.BitField;
import net.hetimatan.util.event.GlobalAccessProperty;
import net.hetimatan.util.url.PercentEncoder;


/**
 * save received data and piece data. 
 * 
 */
public class TorrentData {
	private BitField mStockedDataInfo = null;
	private BitField mRequestDataInfo = null;

	private String mInfoHash = "_";
	private CashKyoroFile mCash = null;
	private int mPieceLength =0;
	private File mContent = null;
	private File mHead = null;
	private File mSource = null;
	private long mDataLength = 0;
	private MetaFile mMetafile = null;

	public TorrentData(MetaFile file) throws IOException{
		mMetafile = file;
		int numOfPiece = file.numOfPiece();
		mPieceLength =(int)file.getPieceLength();

		//
		mStockedDataInfo = new BitField(numOfPiece);
		mRequestDataInfo = new BitField(numOfPiece);

		mInfoHash = file.getInfoSha1AsPercentString();
		File dir = getPieceDir();
		dir.mkdirs();
		mContent = new File(dir, "_content");
		mHead = new File(dir, "_header");
		mSource = new File(dir, "_source");
		mCash = new CashKyoroFile(mContent, 256, 10);
		for(Long l:file.getFileLengths()) {
			mDataLength += l;
		}
		mStockedDataInfo.zeroClear();
		mRequestDataInfo.zeroClear();
	}

	public void load() throws IOException {
		CashKyoroFile cash = null;
		PercentEncoder encoder = new PercentEncoder();
		try {
			cash = new CashKyoroFile(mHead, 256, 2);
			byte[] buffer = CashKyoroFileHelper.newBinary(cash);
			mStockedDataInfo.setBitfield(encoder.decode(buffer));
			mRequestDataInfo.setBitfield(encoder.decode(buffer));
		} finally {
			if(cash != null) {
				cash.close();
			}
		}
	}

	public void save() throws IOException {
		CashKyoroFile cash = null;
		try {
			PercentEncoder encoder = new PercentEncoder();
			cash = new CashKyoroFile(mHead, 256, 2);
			cash.write(
					encoder.encode(
					mStockedDataInfo.getBinary()).getBytes());
			cash.syncWrite();
		} finally {
			if(cash != null) {
				cash.close();
			}
		}
		try {
			cash = new CashKyoroFile(mSource, 256, 2);
			mMetafile.save(cash);
			cash.syncWrite();
		} finally {
			if(cash != null) {
				cash.close();
			}
		}		
	}

	public boolean isComplete() {
		return mStockedDataInfo.isAllOn();
	}

	public long getDataLength() {
		return mDataLength;
	}

	public int getPieceLengthPer(int index) {
		long len = (index+1)*mPieceLength;
		if(mDataLength <=len) {
			int ret = (int)(mDataLength-(index*mPieceLength));
			if(ret<=mPieceLength && ret>=0) {
				return ret;
			}
		}
		return mPieceLength;
	}

	public void setMaster(File[] srcs) throws IOException {
		int len = srcs.length;
		if(srcs.length == 0) {
			//todo clear bitfiled per srcs length
			return;
		}
		long fp = mCash.getFilePointer();
		try {
			CashKyoroFileHelper.xcopy(srcs, mCash);
			mStockedDataInfo.oneClear();
			mRequestDataInfo.oneClear();
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

	public BitField getRequestedDataInfo() {
		return mStockedDataInfo;
	}

	public KyoroFile getPiece(int index) throws IOException {
		return  CashKyoroFileHelper.subSequence(mCash, index*mPieceLength,(index+1)*mPieceLength);
	}

	public KyoroFile getPiece(long start, long end) throws IOException {
		return  CashKyoroFileHelper.subSequence(mCash, start, end);
	}

	public void setPiece(int index, KyoroFile content) throws IOException {
		mCash.seek(index*mPieceLength);
		content.seek(0);
		CashKyoroFileHelper.copy(content, mCash);
		mStockedDataInfo.isOn(index, true);
		mRequestDataInfo.isOn(index, true);
		//following code is for test
		mCash.syncWrite();
	}

	public void setRequest(int index) {
		mRequestDataInfo.isOn(index, true);		
	}

	public File getPieceDir() {
		String currentDir = (new File("dummy")).getAbsoluteFile().getParent();
		String homeAsSt = GlobalAccessProperty.getInstance().get("my.home", currentDir);
		File home = new File(homeAsSt);
		return new File(home, mInfoHash);
	}

}
