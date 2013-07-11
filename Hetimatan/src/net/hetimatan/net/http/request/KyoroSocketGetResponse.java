package net.hetimatan.net.http.request;


import java.io.FileNotFoundException;
import java.io.IOException;

import net.hetimatan.io.file.KyoroFileForKyoroSocket;
import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.io.net.KyoroSelector;
import net.hetimatan.io.net.KyoroSocket;
import net.hetimatan.util.http.LookaheadHttpHeader;
import net.hetimatan.util.http.HttpHeader;
import net.hetimatan.util.http.HttpResponse;

public class KyoroSocketGetResponse implements GetResponseInter {
	private CashKyoroFile mVF = null;
	private int mVfOffset = 0;
	private KyoroSocket mSocket = null;
	private LookaheadHttpHeader mHeaderChunk = null;
	private LookaheadHttpHeader mBodyChunk = null;
	private KyoroFileForKyoroSocket mBase = null;
	private MarkableFileReader mReader = new MarkableFileReader(mBase, 512);
	private long mContentLength = Integer.MAX_VALUE;
	private HttpResponse mResponse = null;

	public KyoroSocketGetResponse(KyoroSocket socket, KyoroSelector selector) throws IOException {
		mSocket = socket;
		mBase = new KyoroFileForKyoroSocket(mSocket, 512*30);
		mBase.setSelector(selector);
		mReader = new MarkableFileReader(mBase, 512*30);
		mVfOffset = 0;
	}

	@Override
	public int getVFOffset() {
		return mVfOffset;
	}

	@Override
	public CashKyoroFile getVF() {
		return mVF;
	}

	@Override
	public void read() throws IOException, InterruptedException {
		readHeader();
		readBody();
	}

	@Override
	public void close() {
		try {
			mVF.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean headerIsReadable() throws IOException {		
		boolean prev = mReader.setBlockOn(false);
		try {
			if(mHeaderChunk == null) {
				mHeaderChunk = new LookaheadHttpHeader(mReader, Integer.MAX_VALUE);
			}
			return LookaheadHttpHeader.readByEndOfHeader(mHeaderChunk, mReader);
		} finally {
			mReader.setBlockOn(prev);
		}
	}

	@Override
	public boolean bodyIsReadable() throws IOException {
		boolean prev = mReader.setBlockOn(false);
		try {
			mBodyChunk = new LookaheadHttpHeader(mReader, (int)mContentLength);
			return LookaheadHttpHeader.readByEndOfHeader(mBodyChunk, mReader);
		} finally {
			mReader.setBlockOn(prev);
		}
	}

	@Override
	public void readHeader() throws IOException, InterruptedException {
		try {
			mReader.seek(0);
			mResponse = HttpResponse.decode(mReader, false);
			mVfOffset = (int)mReader.getFilePointer();
			mContentLength = mResponse.getContentSizeFromHeader();
			{
				mReader.seek(0);
				byte[] bu = new byte[mVfOffset];
				mReader.read(bu);
				System.out.println("++++"+new String(bu)+"#####");
				mReader.seek(mVfOffset);
			}
			for(HttpHeader h :mResponse.getHeader()) {
				System.out.print("[##]"+h.getKey()+","+h.getValue());
			}
		} catch(Exception e) {
			e.printStackTrace();
			mVfOffset = 0;
		}
	}

	@Override
	public void readBody() throws IOException, InterruptedException {
		try {
			int datam = 0;
			int num=0;
			do {
				datam = mReader.read();
				if(datam < 0) {
					break;
				}
				num++;
				Thread.yield();
			} while((num<mContentLength));
		} finally {
			mReader.baseFileIsClosedWhenCallClose(true);
			mBase.baseFileIsClosedWhenCallClose(false);
			mReader.close();
		}
		mVF = mBase.getVF();		
	}

	@Override
	public HttpResponse getHttpResponse() throws IOException {
		return mResponse;
	}
}
