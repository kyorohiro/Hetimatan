package net.hetimatan.net.http;

import java.io.IOException;


import net.hetimatan.io.file.KyoroFile;
import net.hetimatan.io.filen.ByteKyoroFile;
import net.hetimatan.io.net.KyoroSocket;
import net.hetimatan.util.http.HttpHeader;
import net.hetimatan.util.http.HttpRequestURI;
import net.hetimatan.util.http.HttpResponse;
import net.hetimatan.util.io.ByteArrayBuilder;
import net.hetimatan.util.log.Log;

public class HttpServer3xx extends HttpServer {

	private static HttpServer3xx server = null;
	public static void main(String[] args) {
		server = new HttpServer3xx();
		server.setPort(18080);
		server.startServer(null);
	}

	@Override
	public KyoroFile createResponse(HttpFront front, KyoroSocket socket,
			HttpRequestURI uri) throws IOException {
		String path = uri.getLine().getRequestURI().getPath();
		int index = 0;
		if(path.startsWith("http://")) {
			index = path.indexOf("/", "http://".length());
		}
		String address = uri.getValue("mv");
		path = path.substring(index);
		if(path.startsWith("/301")) {
			return createHeader(HttpResponse.STATUS_CODE_301_MOVE_PERMANENTLY, address);
		}
		if(path.startsWith("/302")) {
			return createHeader(HttpResponse.STATUS_CODE_302_Found, address);
		}
		if(path.startsWith("/303")) {
			return createHeader(HttpResponse.STATUS_CODE_303_SEE_OTHER, address);
		}
		if(path.startsWith("/307")) {
			return createHeader(HttpResponse.STATUS_CODE_307_TEMPORARY_REDIRECT, address);
		}

		return super.createResponse(front, socket, uri);
	}

	public KyoroFile createHeader(String responce, String location) throws IOException {
		if(Log.ON){Log.v(TAG, "HttpServer#createHeader");}
		KyoroFile builder = new ByteKyoroFile();
		try {
			builder.addChunk(("HTTP/1.1 "+responce+"\r\n").getBytes());
			builder.addChunk(("Content-Length: "+0+"\r\n").getBytes());
			builder.addChunk(("Content-Type: text/plain\r\n").getBytes());
			builder.addChunk(("Connection: close\r\n").getBytes());
			if(location != null) {
				builder.addChunk((HttpHeader.HEADER_LOCATION +" :"+location+"\r\n").getBytes());				
			}
			builder.addChunk(("\r\n").getBytes());
			return builder;
		} finally {
			if(Log.ON){Log.v(TAG, "/HttpServer#createHeader");}
		}
	}
}