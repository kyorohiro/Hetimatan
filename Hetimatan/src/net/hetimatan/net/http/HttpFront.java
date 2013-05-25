package net.hetimatan.net.http;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.sql.NClob;
import java.util.LinkedList;

import net.hetimatan.io.file.KyoroFile;
import net.hetimatan.io.file.KyoroFileForKyoroSocket;
import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.io.filen.KFNextHelper;
import net.hetimatan.io.net.KyoroSelectable;
import net.hetimatan.io.net.KyoroSelector;
import net.hetimatan.io.net.KyoroSocket;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.http.HttpChunkHelper;
import net.hetimatan.util.http.HttpRequestURI;
import net.hetimatan.util.io.ByteArrayBuilder;
import net.hetimatan.util.log.Log;
import net.hetimatan.util.net.KyoroSocketEventRunner;
import net.hetimatan.util.url.PercentEncoder;

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
		if(Log.ON){Log.v(TAG, "HttpFront#/parseHeader()");};
	}

	public void doResponse() throws IOException {
		if(Log.ON){Log.v(TAG, "HttpFront#doRespose");}
		HttpFront info = this;
		HttpServer server = mServer.get();
		if(info == null || server == null) {
			return;
		} 
		KyoroFile responce = server.createResponse(info.mSocket, info.mUri);
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.append(("HTTP/1.1 200 OK\r\n").getBytes());
		builder.append(("Content-Length: "+responce.length()+"\r\n").getBytes());
		builder.append(("Content-Type: text/plain\r\n").getBytes());
		builder.append(("\r\n").getBytes());
		builder.append(KFNextHelper.newBinary(responce));

		// まとめて送信するのが良い
		Log.v(TAG, ">>"+ new String(builder.getBuffer(), 0, builder.length())+"[EOF]");
		if(Log.ON){Log.v(TAG, "--1--");}
		mSocket.write(builder.getBuffer(), 0, builder.length());
		close();
	}

	public void action() throws Throwable {
		if(Log.ON){Log.v(TAG, "HttpServer#doRequestTask()");}
		while(!isOkToParseHeader()){
			Thread.yield();Thread.sleep(0);}
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
		if(Log.ON){Log.v(TAG, "--3--");}
	}
}