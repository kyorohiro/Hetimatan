package net.hetimatan.net.http;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.LinkedList;

import net.hetimatan.io.file.KyoroFile;
import net.hetimatan.io.file.KyoroFileForKyoroSocket;
import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.io.net.KyoroSelector;
import net.hetimatan.io.net.KyoroSocket;
import net.hetimatan.net.http.task.server.HttpFrontCloseTask;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.http.LookaheadHttpHeader;
import net.hetimatan.util.http.HttpObject;
import net.hetimatan.util.http.HttpRequest;
import net.hetimatan.util.log.Log;
import net.hetimatan.util.net.KyoroSocketEventRunner;
import net.hetimatan.util.net.MessageSendTask;

public class HttpFront {
	public static final String TAG = "HttpFront";

	public String sId = ""; 
	private WeakReference<HttpServer> mServer = null;
	private KyoroSocket mSocket = null;
	private HttpRequest mUri = null;
	private MarkableReader mCurrentReader = null;
	private LookaheadHttpHeader mHeaderChunk = null;
	private KyoroFileForKyoroSocket mKFKSocket = null;
	private MessageSendTask mSendTaskChane = null;

	public HttpFront(HttpServer server, KyoroSocket socket) throws IOException {
		if(Log.ON){Log.v(TAG, "HttpFront#new()");}
		sId = "[httpfront:"+socket.getHost()+ ":"+ socket.getPort()+"]";
		socket.setDebug(sId);

		mServer = new WeakReference<HttpServer>(server);
		mSocket = socket;
		mKFKSocket = new KyoroFileForKyoroSocket(socket, 1024);
		mCurrentReader = new MarkableFileReader(mKFKSocket,1024);
	}

	LinkedList<EventTask> mMyTask = new LinkedList<EventTask>();
	public void addMyTask(EventTask task) {
		mMyTask.add(task);
	}


	public boolean isOkToParseHeader() throws IOException {
		//
		//rewrite
		//
		KyoroSocketEventRunner runner = KyoroSocketEventRunner.getYourWorker(); 
		KyoroSelector seletor = runner.getSelector();
		if(seletor == null) {
			if(Log.ON){Log.v("########","selector == null");}
			mKFKSocket.setSelector(seletor);
		}
		boolean prev = mCurrentReader.setBlockOn(false);
		try {
			if(mHeaderChunk == null) {
				if(Log.ON){Log.v(TAG, "HttpFront#isOkToParseHeader()");}
				mHeaderChunk = new LookaheadHttpHeader(mCurrentReader, Integer.MAX_VALUE);
			}
			boolean ret =LookaheadHttpHeader.readByEndOfHeader(mHeaderChunk, mCurrentReader);
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
		this.mUri = HttpRequest.decode(mCurrentReader);
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
		KyoroFile kfiles = server.createResponse(this, info.mSocket, info.mUri);
		kfiles.seek(0);
		MessageSendTask task = new MessageSendTask(server.getEventRunner(),info.mSocket, kfiles);
		task.nextAction(new HttpFrontCloseTask(this, server.getEventRunner()));
		server.getEventRunner().pushWork(task);
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