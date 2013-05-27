package net.hetimatan.net.http;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.LinkedList;

import net.hetimatan.io.file.KyoroFile;
import net.hetimatan.io.file.KyoroFileForFiles;
import net.hetimatan.io.file.KyoroFileForKyoroSocket;
import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.io.filen.ByteKyoroFile;
import net.hetimatan.io.filen.KFNextHelper;
import net.hetimatan.io.net.KyoroSelector;
import net.hetimatan.io.net.KyoroSocket;
import net.hetimatan.net.http.task.HttpFrontCloseTask;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.EventTaskRunner;
import net.hetimatan.util.http.HttpChunkHelper;
import net.hetimatan.util.http.HttpObject;
import net.hetimatan.util.http.HttpRequestURI;
import net.hetimatan.util.io.ByteArrayBuilder;
import net.hetimatan.util.log.Log;
import net.hetimatan.util.net.KyoroSocketEventRunner;
import net.hetimatan.util.net.MessageSendTask;

public class HttpFront {
	public static final String TAG = "HttpFront";

	private WeakReference<HttpServer> mServer = null;
	private KyoroSocket mSocket = null;
	private HttpRequestURI mUri = null;
	private MarkableReader mCurrentReader = null;
	private HttpChunkHelper mHeaderChunk = null;
	private KyoroFileForKyoroSocket mKFKSocket = null;

	public HttpFront(HttpServer server, KyoroSocket socket) throws IOException {
		if(Log.ON){Log.v(TAG, "HttpFront#new()");}
		mServer = new WeakReference<HttpServer>(server);
		mSocket = socket;
		mKFKSocket = new KyoroFileForKyoroSocket(socket, 1024);
		mCurrentReader = new MarkableFileReader(mKFKSocket,1024);
	}

	LinkedList<EventTask> mMyTask = new LinkedList<EventTask>();
	public void addMyTask(EventTask task) {
		mMyTask.add(task);
	}
	long time = 0;
	int num =0;
	public boolean isOkToParseHeader() throws IOException {
		//
		//rewrite
		//
		KyoroSocketEventRunner runner = KyoroSocketEventRunner.getYourWorker(); 
		KyoroSelector seletor = runner.getSelector();
		if(seletor == null) {
			Log.v("########","selector == null");
			mKFKSocket.setSelector(seletor);
		}
		boolean prev = mCurrentReader.setBlockOn(false);
		try {
			if(mHeaderChunk == null) {
				if(Log.ON){Log.v(TAG, "HttpFront#isOkToParseHeader()");}
				mHeaderChunk = new HttpChunkHelper(mCurrentReader, Integer.MAX_VALUE);
				time = System.currentTimeMillis();;
				num =0;
			}
			num++;
			boolean ret =HttpChunkHelper.readByEndOfHeader(mHeaderChunk, mCurrentReader);
			if(ret == true) {
				if(Log.ON){Log.v(TAG, "HttpFront#/isOkToParseHeader()");};
				long fp = mCurrentReader.getFilePointer();
				long st = mHeaderChunk.getStart();
				mCurrentReader.seek(st);
				mHeaderChunk = null;
			}

			return ret;
		} finally {
			mCurrentReader.setBlockOn(prev);
		}
	}

	public void parseHeader() throws IOException {
		if(Log.ON){Log.v(TAG, "HttpFront#parseHeader()");}
//		mCurrentReader.setBlockOn(true);
		this.mUri = HttpRequestURI.decode(mCurrentReader);
		if(Log.ON){Log.v(TAG, HttpObject.createEncode(mUri));};		
		if(Log.ON){Log.v(TAG, "HttpFront#/parseHeader()");};
	}

	private MessageSendTask mtask = null;
	public void doResponse() throws IOException {
		if(Log.ON){Log.v(TAG, "HttpFront#doRespose");}
		HttpFront info = this;
		HttpServer server = mServer.get();
		if(info == null || server == null) {
			return;
		} 
		KyoroFile responce = server.createResponse(info.mSocket, info.mUri);
		ByteArrayBuilder builder = server.createHeader(info.mSocket, info.mUri, responce);
		KyoroFile[] files = new KyoroFile[2];
		files[0] = new ByteKyoroFile(builder);
		files[1] = responce;
		KyoroFileForFiles kfiles = new KyoroFileForFiles(files);
		MessageSendTask task = new MessageSendTask(server.getEventRunner(), mSocket, kfiles);
		server.getEventRunner().pushWork(task);
		task.nextAction(new HttpFrontCloseTask(this, server.getEventRunner()));
		mtask = task;

		/*
		if(responce.length()<1024) {
			builder.append(KFNextHelper.newBinary(responce));
			// まとめて送信するのが良い
			if(Log.ON){Log.v(TAG, ">>"+ new String(builder.getBuffer(), 0, builder.length())+"[EOF]");}
			mSocket.write(builder.getBuffer(), 0, builder.length());
		} else {
			if(Log.ON){Log.v(TAG, ">>"+ new String(builder.getBuffer(), 0, builder.length())+"[EOF]");}
			mSocket.write(builder.getBuffer(), 0, builder.length());
			responce.seek(0);
			int len = 0;
			int t = 0;
			ByteArrayBuilder bufferFromThread = EventTaskRunner.getByteArrayBuilder();
			bufferFromThread.setBufferLength(1024);
			byte[] buffer = bufferFromThread.getBuffer();
			do {
				t = responce.read(buffer);
				if(t<0) {
					break;
				}
				if(t>0) {
					mSocket.write(buffer, 0, t);
				}
				len += t;
				//System.out.println("="+len+"/"+responce.length());
			} while(len<responce.length());
		}
		close();
		*/
	}

	public void action() throws Throwable {
		if(Log.ON){Log.v(TAG, "HttpServer#doRequestTask()");}
		if(!isOkToParseHeader()){return;}
		parseHeader();
		doResponse();
	}

	public void close() throws IOException {
		if(Log.ON){Log.v(TAG, "HttpFront#close()");}
		HttpServer server = mServer.get();
		if(server != null) {
			// remove from selectable
			server.removeList(this);
		}
		if(mSocket != null) {
			mSocket.close();
		}
		if(mCurrentReader != null) {
			mCurrentReader.close();
		}
		mMyTask.clear();
		mMyTask = null;
		mKFKSocket = null;
		mSocket = null;
	}
}