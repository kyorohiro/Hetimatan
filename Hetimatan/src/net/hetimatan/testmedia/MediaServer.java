package net.hetimatan.testmedia;

import java.io.File;
import java.io.IOException;

import net.hetimatan.io.file.KyoroFile;
import net.hetimatan.io.filen.RACashFile;
import net.hetimatan.io.net.KyoroSocket;
import net.hetimatan.net.http.HttpServer;
import net.hetimatan.util.http.HttpRequestURI;

public class MediaServer extends HttpServer {

	private RACashFile mFile = null;
	public MediaServer() { 
		try {
			mFile = new RACashFile(new File("../../c.mp4"), 16*1024, 4);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public KyoroFile createResponse(KyoroSocket socket, HttpRequestURI uri) throws IOException {
		return mFile;
		//super.createResponse(socket, uri);
	}
	
	public static MediaServer sServer = null;
	public static void main(String[] args) {
		MediaServer server = new MediaServer();
		server.setPort(8888);
		server.doStart(null);
		sServer = server;
	}
}
