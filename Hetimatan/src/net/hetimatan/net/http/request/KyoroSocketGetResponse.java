package net.hetimatan.net.http.request;


import java.io.FileNotFoundException;
import java.io.IOException;

import net.hetimatan.io.net.KyoroSelector;
import net.hetimatan.io.net.KyoroSocket;
import net.hetimatan.ky.io.KyoroFileForKyoroSocket;
import net.hetimatan.ky.io.MarkableFileReader;
import net.hetimatan.ky.io.next.RACashFile;
import net.hetimatan.util.http.HttpChunkHelper;
import net.hetimatan.util.http.HttpHeader;
import net.hetimatan.util.http.HttpResponse;

public class KyoroSocketGetResponse implements GetResponseInter {
	private RACashFile mVF = null;
	private int mVfOffset = 0;
	private KyoroSocket mSocket = null;
	private HttpChunkHelper mHeaderChunk = null;
	private HttpChunkHelper mBodyChunk = null;
	private KyoroFileForKyoroSocket mBase = null;
	private MarkableFileReader mReader = new MarkableFileReader(mBase, 512);
	private long mContentLength = Integer.MAX_VALUE;

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
	public RACashFile getVF() {
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
				mHeaderChunk = new HttpChunkHelper(mReader, Integer.MAX_VALUE);
			}
			return HttpChunkHelper.readByEndOfHeader(mHeaderChunk, mReader);
		} finally {
			mReader.setBlockOn(prev);
		}
	}

	@Override
	public boolean bodyIsReadable() throws IOException {
		boolean prev = mReader.setBlockOn(false);
		try {
			mBodyChunk = new HttpChunkHelper(mReader, (int)mContentLength);
			return HttpChunkHelper.readByEndOfHeader(mBodyChunk, mReader);
		} finally {
			mReader.setBlockOn(prev);
		}
	}

	@Override
	public void readHeader() throws IOException, InterruptedException {
		try {
			mReader.seek(0);
			HttpResponse response = HttpResponse.decode(mReader, false);
			mVfOffset = (int)mReader.getFilePointer();
			mContentLength = response.getContentSizeFromHeader();
			{
				mReader.seek(0);
				byte[] bu = new byte[mVfOffset];
				mReader.read(bu);
				System.out.println("++++"+new String(bu)+"#####");
				mReader.seek(mVfOffset);
			}
			for(HttpHeader h :response.getHeader()) {
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
}
