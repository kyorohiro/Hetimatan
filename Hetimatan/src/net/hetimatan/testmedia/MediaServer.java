package net.hetimatan.testmedia;

import java.io.File;
import java.io.IOException;

import net.hetimatan.io.file.KyoroFile;
import net.hetimatan.io.filen.RACashFile;
import net.hetimatan.io.net.KyoroSocket;
import net.hetimatan.net.http.HttpServer;
import net.hetimatan.util.http.HttpObjectHelper;
import net.hetimatan.util.http.HttpRequestURI;
import net.hetimatan.util.io.ByteArrayBuilder;

public class MediaServer extends HttpServer {

	private RACashFile mFile = null;
	public MediaServer() { 
		try {
			mFile = new RACashFile(new File("../../d.mp4"), 16*1024, 4);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ByteArrayBuilder createHeader(KyoroSocket socket, HttpRequestURI uri, KyoroFile responce) throws IOException {
		
		String rangeHeader = uri.getHeaderValue("Range");
		long[] range = new long[0];
		if(rangeHeader != null && rangeHeader.length() != 0) {
			range = HttpObjectHelper.getRange(rangeHeader);
		}
		ByteArrayBuilder builder = new ByteArrayBuilder();
		builder.append(("HTTP/1.1 200 OK\r\n").getBytes());
		builder.append(("Content-Length: "+responce.length()+"\r\n").getBytes());
		builder.append(("Accept-Ranges: bytes\r\n").getBytes());
		builder.append(("Connection: close\r\n").getBytes());
		builder.append(("Content-Type: video/mp4\r\n").getBytes());
		builder.append(("\r\n").getBytes());
		return builder;
	}

	@Override
	public KyoroFile createResponse(KyoroSocket socket, HttpRequestURI uri) throws IOException {
		return mFile;
	}
	
	public static MediaServer sServer = null;
	public static void main(String[] args) {
		MediaServer server = new MediaServer();
		server.setPort(8888);
		server.startServer(null);
		sServer = server;
	}
}
