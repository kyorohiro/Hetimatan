package net.hetimatan.net.http.request;


import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.LinkedHashMap;

import net.hetimatan.io.net.KyoroSelector;
import net.hetimatan.io.net.KyoroSocket;
import net.hetimatan.io.net.KyoroSocketImpl;
import net.hetimatan.util.http.HttpObject;
import net.hetimatan.util.http.HttpRequestLine;
import net.hetimatan.util.http.HttpRequest;


public class KyoroSocketGetRequester implements GetRequesterInter {
	
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

	public KyoroSocketGetRequester setHttpVersion(String httpVersion) {
		mHttpVersion = httpVersion;
		return this;
	}

	public KyoroSocketGetRequester setPort(int port) {
		mPort = port;
		return this;
	}

	public KyoroSocketGetRequester setHost(String host) {
		mHost = host;
		return this;
	}

	public KyoroSocketGetRequester setPath(String path) {
		mPath = path;
		return this;
	}


	@Override
	public GetResponseInter doRequest(KyoroSelector selector)
			throws IOException, InterruptedException {
		KyoroSocket socket = null;
		mSelector = selector;
		try {
			socket = _connectionRequest();
			_writeRequest(socket);
			GetResponseInter res = _getResponse(socket);
			res.read();
			return res;
		} finally {
			if (socket != null) {
				socket.close();
			}
		}	}
	public GetResponseInter doRequest() throws IOException, InterruptedException {
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

	public GetResponseInter _getResponse(KyoroSocket socket) throws IOException, InterruptedException {
//		mHeader.get(.)
//		return null;
		GetResponseInter response = new KyoroSocketGetResponse(socket, mSelector);
		return response;
	}	

	public KyoroSocketGetRequester putValue(String key, String value) {
		mValues.put(key, value);
		return this;		
	}

	public KyoroSocketGetRequester putHeader(String key, String value) {
		mHeader.put(key, value);
		return this;
	}

	public synchronized byte[] createRequest() throws IOException {
		HttpRequest uri = HttpRequest
		.newInstance(REQUEST_METHOD_GET, mPath, HttpRequestLine.HTTP10)
		;//.addHeader(HEADER_HOST, InetAddress.getLocalHost().getHostName());
		for (String key : mHeader.keySet()) {
			uri.addHeader(key, mHeader.get(key));
		}
		for (String key : mValues.keySet()) {
			uri.putValue(key, mValues.get(key));
		}
		return HttpObject.createEncode(uri).getBytes();
	}

	@Override
	public void setSelector(KyoroSelector selector) throws IOException {
		mSelector = selector;
	}

}