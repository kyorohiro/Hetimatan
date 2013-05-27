package net.hetimatan.net.http;

import java.io.IOException;
import java.util.LinkedList;

import net.hetimatan.io.file.KyoroFile;
import net.hetimatan.io.filen.RACashFile;
import net.hetimatan.io.net.KyoroSelector;
import net.hetimatan.io.net.KyoroServerSocket;
import net.hetimatan.io.net.KyoroServerSocketImpl;
import net.hetimatan.io.net.KyoroSocket;
import net.hetimatan.net.http.task.HttpFrontRequestTask;
import net.hetimatan.net.http.task.HttpServerAcceptTask;
import net.hetimatan.net.http.task.HttpServerBootTask;
import net.hetimatan.util.event.EventTaskRunner;
import net.hetimatan.util.http.HttpRequestURI;
import net.hetimatan.util.io.ByteArrayBuilder;
import net.hetimatan.util.log.Log;
import net.hetimatan.util.net.KyoroSocketEventRunner;


//
// response "this is test"
//
public class HttpServer {
	public static final String TAG = "HttpServer";

	private LinkedList<HttpFront> mClientInfos = new LinkedList<HttpFront>();
	private HttpServerAcceptTask mAcceptTask = null;
	private int mPort = 8080;

	private KyoroSocketEventRunner mRequestRunner = null;
	private boolean mMyReqRunner = false;
	private KyoroServerSocket mServerSocket = null;
	private KyoroSelector mSelector = null;

	public void startServer(KyoroSocketEventRunner requestRunner) {
		if(Log.ON){Log.v(TAG, "HttpServer#startServer()");}

		if (requestRunner != null) {
			mRequestRunner = requestRunner;
		} else {
			mMyReqRunner = true;
			mRequestRunner = new KyoroSocketEventRunner();
		}

		mRequestRunner.waitIsSelect(true);
		HttpServerBootTask boot = new HttpServerBootTask(this, mRequestRunner);
		boot.nextAction(null);
		mSelector = mRequestRunner.getSelector();
		mRequestRunner.start(boot);
	}

	public void close() {
		if(Log.ON){Log.v(TAG, "close()");}
		try {
			if (null != mRequestRunner) {
				mRequestRunner.close();				
			}
			if (null != mServerSocket) {
				mServerSocket.close();
			}
			if(null != mSelector) {
				mSelector.close();
			}
			if(mClientInfos != null) {
				while (0<mClientInfos.size()) {
					HttpFront front = mClientInfos.removeFirst();
					front.close();
				}
			}
			if(mMyReqRunner&&mRequestRunner != null) {
				mRequestRunner.close();
			}
			mAcceptTask = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setPort(int port) {
		mPort = port;
	}

	public void removeList(HttpFront front) {
		if(mClientInfos != null && front != null) {
			mClientInfos.remove(front);
		}
	}

	public HttpFront removeFirst() {
		if(mClientInfos.size() > 0) {
			return mClientInfos.removeFirst();
		} else {
			return null;
		}
	}


	public void boot() throws IOException {
		if(Log.ON){Log.v(TAG, "HttpServer#boot():"+mPort);}
		mServerSocket = new KyoroServerSocketImpl();
		mServerSocket.bind(mPort);
		mServerSocket.regist(mSelector, KyoroSelector.ACCEPT);
		mServerSocket.setEventTaskAtWrakReference(mAcceptTask = new HttpServerAcceptTask(this, mRequestRunner));
	}

	//
	// AcceptTrackerTask call
	public void accept() throws IOException {
		if(Log.ON){Log.v(TAG, "HttpServer#accept():");}
		KyoroSocket socket = mServerSocket.accept();
		socket.regist(mSelector, KyoroSelector.READ);
		HttpFront info = new HttpFront(this, socket);
		socket.setEventTaskAtWrakReference(new HttpFrontRequestTask(info, mRequestRunner));
		addLastHttpRequest(info);
	}

	public void addLastHttpRequest(HttpFront info) {
		mClientInfos.addLast(info);
	}


	//
	// this method is overrided
	public ByteArrayBuilder createHeader(KyoroSocket socket, HttpRequestURI uri, KyoroFile responce) throws IOException {
		if(Log.ON){Log.v(TAG, "HttpServer#createHeader");}
		try {
			ByteArrayBuilder builder = new ByteArrayBuilder();
			builder.append(("HTTP/1.1 200 OK\r\n").getBytes());
			builder.append(("Content-Length: "+responce.length()+"\r\n").getBytes());
			builder.append(("Content-Type: text/plain\r\n").getBytes());
			builder.append(("Connection: close\r\n").getBytes());
			builder.append(("\r\n").getBytes());
			return builder;
		} finally {
			if(Log.ON){Log.v(TAG, "/HttpServer#createHeader");}
		}
	}

	//
	// this method is overrided
	public KyoroFile createResponse(KyoroSocket socket, HttpRequestURI uri) throws IOException {
		if(Log.ON){Log.v(TAG, "HttpServer#createResponse");}
		try {
			return new RACashFile("hello world".getBytes());
		} finally {
			if(Log.ON){Log.v(TAG, "/HttpServer#createResponse");}
		}
	}

	public boolean isBinded() {
		if(mServerSocket == null) {
			return false;
		}
		return mServerSocket.isBinded();
	}
}
