package net.hetimatan.net.http.request;


import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;

import net.hetimatan.io.net.KyoroSelector;
import net.hetimatan.io.net.KyoroSocket;
import net.hetimatan.io.net.KyoroSocketImpl;
import net.hetimatan.util.http.HttpObject;
import net.hetimatan.util.http.HttpRequestLine;
import net.hetimatan.util.http.HttpRequest;


public class HttpGetRequester  {
	public static final String REQUEST_METHOD_GET = "GET";
	public static final String SCHEME_HTTP = "http";
	public static final String HTTP10 = "HTTP/1.0";
	public static final String HTTP11 = "HTTP/1.1";
	public static final String HEADER_HOST = "Host";
	public static final String USER_AGENT = "User-Agent";
	public static final String CONTENT_LENGTH = "Content-Length";

	private LinkedHashMap<String, String> mHeader = new LinkedHashMap<String, String>();
	private LinkedHashMap<String, String> mValues = new LinkedHashMap<String, String>();
	private String mPath = "";
	private int mPort = 80;
	private String mHost = "127.0.0.1";
	private String mHttpVersion = HttpRequestLine.HTTP10;
	private KyoroSelector mSelector = null;

	public static void log(String message) {
		System.out.println("KyoroSocketGetRequester#"  + message);
	}

	public HttpGetRequester setHttpVersion(String httpVersion) {
		mHttpVersion = httpVersion;
		return this;
	}

	public HttpGetRequester setPort(int port) {
		mPort = port;
		return this;
	}

	public HttpGetRequester setHost(String host) {
		mHost = host;
		return this;
	}

	public HttpGetRequester setPath(String path) {
		mPath = path;
		return this;
	}


	
	public HttpGetResponse doRequest(KyoroSelector selector) throws IOException, InterruptedException {
		KyoroSocket socket = null;
		mSelector = selector;
		try {
			socket = _connectionRequest();
			_writeRequest(socket);
			HttpGetResponse res = _getResponse(socket);
			res.read();
			return res;
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
	}

	public HttpGetResponse doRequest() throws IOException, InterruptedException {
		return doRequest(new KyoroSelector());
	}

	public KyoroSocket _connectionRequest() throws IOException, InterruptedException {
		System.out.println("#####"+SCHEME_HTTP + "://" + mHost + ":" + mPort+mPath);
		URL url = new URL(SCHEME_HTTP + "://" + mHost + ":" + mPort+mPath);
		KyoroSocket socket = null;
		socket = new KyoroSocketImpl();
		socket.setDebug("KyoroSocketGetConnection:"+socket.getHost()+":"+socket.getPort());
		socket.connect(url.getHost(), url.getPort());
		while(socket.getConnectionState() == KyoroSocket.CN_CONNECTING){
			Thread.yield();
			Thread.sleep(0);}
		return socket;
	}

	public void _writeRequest(KyoroSocket socket) throws IOException {
		log("_writeRequest()");
		byte[] buffer = createRequest();
		log(new String(buffer));
		socket.write(buffer, 0, buffer.length);
	}

	public HttpGetResponse _getResponse(KyoroSocket socket) throws IOException, InterruptedException {
		HttpGetResponse response = new HttpGetResponse(socket, mSelector);
		return response;
	}	

	public HttpGetRequester putValue(String key, String value) {
		mValues.put(key, value);
		return this;		
	}

	public HttpGetRequester putHeader(String key, String value) {
		mHeader.put(key, value);
		return this;
	}

	public synchronized HttpRequest createHttpRequest() throws IOException {
		HttpRequest uri = HttpRequest
		.newInstance(REQUEST_METHOD_GET, mPath, HttpRequestLine.HTTP10);
		for (String key : mHeader.keySet()) {
			uri.addHeader(key, mHeader.get(key));
		}
		for (String key : mValues.keySet()) {
			uri.putValue(key, mValues.get(key));
		}
		return uri;
	}

	public synchronized byte[] createRequest() throws IOException {
		HttpRequest uri = createHttpRequest();
		return HttpObject.createEncode(uri).getBytes();
	}

	
	public void setSelector(KyoroSelector selector) throws IOException {
		mSelector = selector;
	}

}