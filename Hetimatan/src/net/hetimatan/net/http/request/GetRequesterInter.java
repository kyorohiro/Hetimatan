package net.hetimatan.net.http.request;


import java.io.IOException;
import java.net.URL;

import net.hetimatan.io.net.KyoroSelector;
import net.hetimatan.io.net.KyoroSocket;
import net.hetimatan.io.net.KyoroSocketImpl;

public interface GetRequesterInter {
	public static final String REQUEST_METHOD_GET = "GET";
	public static final String SCHEME_HTTP = "http";
	public static final String HTTP10 = "HTTP/1.0";
	public static final String HTTP11 = "HTTP/1.1";
	public static final String HEADER_HOST = "Host";
	public static final String USER_AGENT = "User-Agent";
	public static final String CONTENT_LENGTH = "Content-Length";

	public GetRequesterInter setHttpVersion(String httpVersion);
	public GetRequesterInter setPort(int port);
	public GetRequesterInter setHost(String host);
	public GetRequesterInter setPath(String path);
	public GetResponseInter doRequest() throws IOException, InterruptedException; 
	public void setSelector(KyoroSelector selector) throws IOException;
	public GetResponseInter doRequest(KyoroSelector selector) throws IOException, InterruptedException; 
	public GetRequesterInter putValue(String key, String value);
	public GetRequesterInter putHeader(String key, String value);
	public byte[] createRequest() throws IOException;

	public KyoroSocket _connectionRequest() throws IOException, InterruptedException;
	public void _writeRequest(KyoroSocket socket) throws IOException;
	public GetResponseInter _getResponse(KyoroSocket socket) throws IOException, InterruptedException;

}