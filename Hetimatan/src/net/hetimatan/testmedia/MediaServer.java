package net.hetimatan.testmedia;

import java.io.IOException;

import net.hetimatan.io.file.KyoroFile;
import net.hetimatan.io.net.KyoroSocket;
import net.hetimatan.net.http.HttpServer;
import net.hetimatan.util.http.HttpRequestURI;

public class MediaServer extends HttpServer {

	@Override
	public KyoroFile createResponse(KyoroSocket socket, HttpRequestURI uri) throws IOException {
		return super.createResponse(socket, uri);
	}

}
