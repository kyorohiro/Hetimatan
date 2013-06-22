package net.hetimatan.net.http;

import java.io.IOException;


import net.hetimatan.io.file.KyoroFile;
import net.hetimatan.io.net.KyoroSocket;
import net.hetimatan.util.http.HttpHeader;
import net.hetimatan.util.http.HttpRequestURI;

public class HttpServer3xx extends HttpServer {

	@Override
	public KyoroFile createResponse(HttpFront front, KyoroSocket socket,
			HttpRequestURI uri) throws IOException {
		String path = uri.getLine().getRequestURI().getPath();
		return super.createResponse(front, socket, uri);
	}
}
