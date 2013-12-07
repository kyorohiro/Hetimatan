package net.hetimatan.net.http.request;

import java.io.IOException;
import java.util.LinkedHashMap;

import javax.swing.plaf.basic.BasicScrollPaneUI.HSBChangeListener;

import net.hetimatan.util.http.HttpRequest;
import net.hetimatan.util.http.HttpRequestLine;
import net.hetimatan.util.http.HttpRequestUri;

public class HttpGetRequestUriBuilder {
	public static final String REQUEST_METHOD_GET = "GET";
	public static final String SCHEME_HTTP = "http";
	public static final String HTTP10 = "HTTP/1.0";
	public static final String HTTP11 = "HTTP/1.1";
	public static final String HEADER_HOST = "Host";
	public static final String USER_AGENT = "User-Agent";
	public static final String CONTENT_LENGTH = "Content-Length";

	private LinkedHashMap<String, String> mHeader = new LinkedHashMap<String, String>();
	private LinkedHashMap<String, String> mValues = new LinkedHashMap<String, String>();

	private String mPath = "/";
	private int mPort = 80;
	private String mHost = "127.0.0.1";
	private String mHttpVersion = HTTP10;

	
	public String getHost() {
		return mHost;
	}

	public int getPort() {
		return mPort;
	}

	public HttpGetRequestUriBuilder setPort(int port) {
		mPort = port;
		return this;
	}

	public HttpGetRequestUriBuilder setHttpVersion(String httpVersion) {
		mHttpVersion = httpVersion;
		return this;
	}
	public HttpGetRequestUriBuilder setHost(String host) {
		mHost = host;
		return this;
	}

	public HttpGetRequestUriBuilder setPath(String path) {
		mPath = path;
		return this;
	}

	public HttpGetRequestUriBuilder putValue(String key, String value) {
		mValues.put(key, value);
		return this;		
	}

	public HttpGetRequestUriBuilder putHeader(String key, String value) {
		mHeader.put(key, value);
		return this;
	}

	public synchronized HttpRequest createHttpRequest() throws IOException {
		HttpRequest request = HttpRequest
		.newInstance(REQUEST_METHOD_GET, mPath, HttpRequestLine.HTTP10);
		boolean haveHost = false;
		for (String key : mHeader.keySet()) {
			if(key.matches("[Hh][Oo][Ss][Tt]")){haveHost=true;}
			request.addHeader(key, mHeader.get(key));
		}
		for (String key : mValues.keySet()) {
			request.putValue(key, mValues.get(key));
		}
		request.getLine().setHttpVersion(mHttpVersion);
		HttpRequestUri uri = request.getLine().getRequestURI();
		uri.setHost(mHost);
		uri.setPort(mPort);
		if(!haveHost) {
			request.addHeader(HEADER_HOST, mHost);
		}
		return request;
	}

	public synchronized HttpRequestUri createHttpRequestUri() throws IOException {
		HttpRequestUri uri = HttpRequestUri.crateHttpGetRequestUri(mPath);
		for (String key : mValues.keySet()) {
			uri.putVale(key, mValues.get(key));
		}
		uri.setHost(mHost);
		uri.setPort(mPort);
		return uri;
	}
}
