package net.hetimatan.net.media;

import java.io.File;
import java.io.IOException;

import net.hetimatan.io.file.KyoroFile;
import net.hetimatan.io.file.KyoroFileForFiles;
import net.hetimatan.io.filen.ByteKyoroFile;
import net.hetimatan.io.filen.KFNextHelper;
import net.hetimatan.io.filen.RACashFile;
import net.hetimatan.io.net.KyoroSocket;
import net.hetimatan.net.http.HttpFront;
import net.hetimatan.net.http.HttpServer;
import net.hetimatan.net.torrent.util.piece.PieceInfo;
import net.hetimatan.net.torrent.util.piece.PieceInfoList;
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

	@Override
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
	public KyoroFile createContent(KyoroSocket socket, HttpRequestURI uri) throws IOException {
		return KFNextHelper.subSequence(mFile, 0, mFile.length());
	}



	@Override
	public KyoroFile createResponse(HttpFront front, KyoroSocket socket, HttpRequestURI uri) throws IOException {
		String rangeHeader = uri.getHeaderValue("Range");
		boolean isRange = false;
		PieceInfoList list = null;
		if(rangeHeader != null && rangeHeader.length() != 0) {
			list = HttpObjectHelper.getRangeList(rangeHeader);
			if(list.size()>0) {
				isRange = true;
			}
		}
		if(!isRange) {
			return super.createResponse(front, socket, uri);
		} else {
			return createRangeResponse(list, front, socket, uri);
		}
	}

	public KyoroFile createRangeResponse(PieceInfoList list, HttpFront front, KyoroSocket socket, HttpRequestURI uri) throws IOException {
		int length = 5;
		if(length > list.size()) {length = list.size();}

		KyoroFile[] responses = new KyoroFile[length*3+1];
		int r = 0;
		KyoroFile response = createResponse(front, socket, uri);

		ByteKyoroFile header = new ByteKyoroFile();
		header.addChunk(("HTTP/1.1 206 Partial Content\r\n").getBytes());			
		long resLen = response.length();
		header.addChunk(("Content-Length: "+resLen+"\r\n").getBytes());
		header.addChunk(("Accept-Ranges: bytes\r\n").getBytes());
		header.addChunk(("Connection: close\r\n").getBytes());
		header.addChunk(("Content-Type: multipart/byteranges; boundary=THIS_STRING_SEPARATES\r\n").getBytes());
		header.addChunk(("\r\n").getBytes());
		responses[r] = header;r++;

		for(int i=0;i<length;i++) {
			header = new ByteKyoroFile();
			header.addChunk(("Content-Range: bytes "
					+ list.getPieceInfo(i).getStart()
					+ "-"+list.getPieceInfo(i).getEnd()+"/"+resLen+"\r\n").getBytes());
			header.addChunk(("Content-Type: video/mp4\r\n").getBytes());
			header.addChunk(("--THIS_STRING_SEPARATES\r\n").getBytes());
			responses[r++] = header;
			responses[r++] = KFNextHelper.subSequence(mFile, list.getPieceInfo(i).getStart(), list.getPieceInfo(i).getEnd());
			header = new ByteKyoroFile();		
			header.addChunk(("\r\n--THIS_STRING_SEPARATES\r\n").getBytes());
			responses[r++] = header;
		}
		return new KyoroFileForFiles(responses);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static MediaServer sServer = null;
	public static void main(String[] args) {
		MediaServer server = new MediaServer();
		server.setPort(8888);
		server.startServer(null);
		sServer = server;
	}
}
