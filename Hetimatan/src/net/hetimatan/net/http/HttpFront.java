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
	public long sTimeout = 10*1024;

	private WeakReference<HttpServer> mServer = null;
	private KyoroSocket mSocket = null;
	private HttpRequest mUri = null;
	private MarkableReader mCurrentReader = null;
	private LookaheadHttpHeader mHeaderChunk = null;
	private KyoroFileForKyoroSocket mKFKSocket = null;
//	private MessageSendTask mSendTaskChane = null;
//	private MessageSendTask mtask = null;
	private LinkedList<EventTask> mMyTask = new LinkedList<EventTask>();


	public HttpFront(HttpServer server, KyoroSocket socket) throws IOException {
		sId = "[httpfront:"+socket.getHost()+ ":"+ socket.getPort()+"]";
		socket.setDebug(sId);

		mServer = new WeakReference<HttpServer>(server);
		mSocket = socket;
		mKFKSocket = new KyoroFileForKyoroSocket(socket, 1024);
		mCurrentReader = new MarkableFileReader(mKFKSocket,1024);
	}

	public void addMyTask(EventTask task) {
		mMyTask.add(task);
	}

	public boolean isOkToParseHeader() throws IOException {
		boolean prev = mCurrentReader.setBlockOn(false);
		try {
			if(mHeaderChunk == null) {
				mHeaderChunk = new LookaheadHttpHeader(mCurrentReader, Integer.MAX_VALUE);
			}
			boolean parseable =LookaheadHttpHeader.readByEndOfHeader(mHeaderChunk, mCurrentReader);
			if(parseable == true) {
				mCurrentReader.seek(mHeaderChunk.getStart());
				mHeaderChunk = null;
			}
			else if(sTimeout < mHeaderChunk.getElapsedTime()) {
				HttpHistory.get().pushMessage(this.sId+":timeout parseheader:"+"\n");
				mHeaderChunk = null;
				throw new IOException("-timeout parse header-");
			}
			return parseable;
		} finally {
			mCurrentReader.setBlockOn(prev);
		}
	}

	public void parseHeader() throws IOException {
		this.mUri = HttpRequest.decode(mCurrentReader);
		HttpHistory.get().pushMessage(this.sId+":parse request:"+mUri.toString()+"\n");
	}


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
		HttpHistory.get().pushMessage(sId+"#close:"+"\n");
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