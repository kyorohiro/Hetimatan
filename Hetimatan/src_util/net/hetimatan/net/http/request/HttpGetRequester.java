package net.hetimatan.net.http.request;


import java.io.IOException;

import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.io.net.KyoroSelector;
import net.hetimatan.io.net.KyoroSocket;
import net.hetimatan.io.net.KyoroSocketImpl;
import net.hetimatan.util.event.EventTask;
import net.hetimatan.util.event.net.MessageSendTask;
import net.hetimatan.util.http.HttpObject;
import net.hetimatan.util.http.HttpRequest;


public class HttpGetRequester  {
	public static final String REQUEST_METHOD_GET = "GET";
	public static final String SCHEME_HTTP = "http";
	public static final String HTTP10 = "HTTP/1.0";
	public static final String HTTP11 = "HTTP/1.1";
	public static final String HEADER_HOST = "Host";
	public static final String USER_AGENT = "User-Agent";
	public static final String CONTENT_LENGTH = "Content-Length";

	private KyoroSelector mSelector = null;
	private HttpGetRequestUriBuilder mBuilder = new HttpGetRequestUriBuilder();
	private MessageSendTask mTask = null;

	public static void log(String message) {
		System.out.println("KyoroSocketGetRequester#"  + message);
	}

	public HttpGetRequestUriBuilder getUrlBuilder() {
		return mBuilder;
	}

	public KyoroSocket connect(KyoroSocket socket) throws IOException, InterruptedException {
		if(socket == null) {
			socket = new KyoroSocketImpl();
		}
		socket.setDebug("KyoroSocketGetConnection:"+mBuilder.getHost() +","+ mBuilder.getPort());
		socket.connect(mBuilder.getHost(), mBuilder.getPort());
		return socket;
	}

	public MessageSendTask request(KyoroSocket socket) throws IOException {
		log("_writeRequest()");
		byte[] buffer = createRequest();
		log(new String(buffer));
		//
		// todo
		if(mTask == null) {
			mTask = new MessageSendTask(socket, new CashKyoroFile(buffer));
		}
		//socket.write(buffer, 0, buffer.length);
		return mTask;
	}

	public HttpGetResponse getResponse(KyoroSocket socket) throws IOException, InterruptedException {
		HttpGetResponse response = new HttpGetResponse(socket, mSelector);
		return response;
	}	

	public synchronized HttpRequest createHttpRequest() throws IOException {
		return mBuilder.createHttpRequest();
	}

	public synchronized byte[] createRequest() throws IOException {
		HttpRequest uri = createHttpRequest();
		return HttpObject.createEncode(uri).getBytes();
	}

	public void setSelector(KyoroSelector selector) throws IOException {
		mSelector = selector;
	}

	/**
	 * テスト用
	 * @throws Throwable 
	 */
	public static HttpGetResponse syncRequest(HttpGetRequester requester, KyoroSelector selector) throws Throwable {
		KyoroSocket socket = null;
		requester.setSelector(selector);
		try {
			socket = requester.connect(null);
			while(socket.getConnectionState() == KyoroSocket.CN_CONNECTING){
				Thread.yield();
				Thread.sleep(0);}
			MessageSendTask sendTask= requester.request(socket);
			do {
				sendTask.action(null);
			} while(sendTask.isKeep());
			
			//
			HttpGetResponse res = requester.getResponse(socket);
			res.readHeader();
			res.readBody();
			return res;
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
	}

	/**
	 * テスト用
	 * @throws Throwable 
	 */
	public static HttpGetResponse doRequest(HttpGetRequester requester) throws Throwable {
		return syncRequest(requester, new KyoroSelector());
	}
}