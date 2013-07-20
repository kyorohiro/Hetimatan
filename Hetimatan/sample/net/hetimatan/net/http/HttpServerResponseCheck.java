package net.hetimatan.net.http;

import java.io.IOException;


import net.hetimatan.io.file.KyoroFile;
import net.hetimatan.io.filen.ByteKyoroFile;
import net.hetimatan.io.net.KyoroSocket;
import net.hetimatan.util.http.HttpRequestHeader;
import net.hetimatan.util.http.HttpRequest;
import net.hetimatan.util.http.HttpResponse;
import net.hetimatan.util.io.ByteArrayBuilder;
import net.hetimatan.util.log.Log;

public class HttpServerResponseCheck extends HttpServer {

	private static HttpServerResponseCheck server = null;
	private int mResponseNumber = 0;
	public static void main(String[] args) {
		server = new HttpServerResponseCheck();
		server.setPort(18081);
		server.startServer(null);
	}

	@Override
	public KyoroFile createResponse(HttpFront front, KyoroSocket socket, HttpRequest uri) throws IOException {
		mResponseNumber++;
		return super.createResponse(front, socket, uri);
	}

	public int getResponseNumber() {
		return mResponseNumber;
	}
	
}
