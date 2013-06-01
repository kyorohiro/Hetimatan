package net.hetimatan.net.http;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Set;

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

//@todo
public class SimpleHttpServer extends HttpServer {

	private RACashFile mFile = null;
	private int mMaxOfRenge = 5;
	private LinkedHashMap<String, String> mMimetype = new LinkedHashMap<String,String>();
	{
		mMimetype.put("mp4", "video/mp4");
		mMimetype.put("txt", "text/plain");
		mMimetype.put("html", "text/html");
	}

	public SimpleHttpServer() { 
		try {
			mFile = new RACashFile(new File("../../h264.mp4"), 16*1024, 4);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public KyoroFile createResponse(HttpFront front, KyoroSocket socket, HttpRequestURI uri) throws IOException {
		String rangeHeader = uri.getHeaderValue("Range");
		boolean isRange = false;
		PieceInfoList list = null;
		if(rangeHeader != null && rangeHeader.length() != 0) {
			list = HttpObjectHelper.getRangeList(rangeHeader,mFile.length());
			if(list.size()>0) {
				isRange = true;
			}
		}
		if(!isRange) {
			return createDefaultResponse(front, socket, uri);
		} else {
			return createSingleRangeResponse(list.getPieceInfo(0), front, socket, uri);
//			return createMultiRangeResponse(list.getPieceInfo(0), front, socket, uri);
		}
	}

	public String getMimeType(String path) {
		Set<String> keyset = mMimetype.keySet();
		for(String key:keyset) {
			if(path.endsWith(key)){
				return mMimetype.get(key);
			}
		}
		return "text/plain";
	}

	public KyoroFile createDefaultResponse(HttpFront front, KyoroSocket socket, HttpRequestURI uri) throws IOException {
		KyoroFile content = KFNextHelper.subSequence(mFile, 0, mFile.length());
		KyoroFile header = new ByteKyoroFile();
		String path =uri.getLine().getRequestURI().getPath();
		header.addChunk(("HTTP/1.1 200 OK\r\n").getBytes());
		header.addChunk(("Content-Length: "+content.length()+"\r\n").getBytes());
		header.addChunk(("Connection: close\r\n").getBytes());
		header.addChunk(("Content-Type: "+getMimeType(path)+"\r\n").getBytes());
		header.addChunk(("\r\n").getBytes());

		KyoroFile[] files = new KyoroFile[2];
		files[0] = header;files[0].seek(0);
		files[1] = content;files[1].seek(0);
		KyoroFileForFiles kfiles = new KyoroFileForFiles(files);
		kfiles.seek(0);
		return kfiles;
	}

	public KyoroFile createSingleRangeResponse(PieceInfo piece, HttpFront front, KyoroSocket socket, HttpRequestURI uri) throws IOException {
		KyoroFile content = KFNextHelper.subSequence(mFile, piece.getStart(), piece.getEnd()+1);
		KyoroFile header = new ByteKyoroFile();
		String path =uri.getLine().getRequestURI().getPath();
		header.addChunk(("HTTP/1.1 206 Partial Content\r\n").getBytes());
		header.addChunk(("Content-Length: "+content.length()+"\r\n").getBytes());
		header.addChunk(("Connection: close\r\n").getBytes());
		header.addChunk(("Content-Type: "+getMimeType(path)+"\r\n").getBytes());
		header.addChunk(("Content-Range: bytes "+piece.getStart()+"-"+piece.getEnd()+"/"+mFile.length()+"\r\n").getBytes());
		header.addChunk(("\r\n").getBytes());

		KyoroFile[] files = new KyoroFile[2];
		files[0] = header;files[0].seek(0);
		files[1] = content;files[1].seek(0);
		KyoroFileForFiles kfiles = new KyoroFileForFiles(files);
		kfiles.seek(0);
		return kfiles;
	}

	
	public static SimpleHttpServer sServer = null;
	public static void main(String[] args) {
		SimpleHttpServer server = new SimpleHttpServer();
		server.setPort(8888);
		server.startServer(null);
		sServer = server;
	}
}

/*
public KyoroFile createMultiRangeResponse(PieceInfoList list, HttpFront front, KyoroSocket socket, HttpRequestURI uri) throws IOException {
int length = mMaxOfRenge;
String path =uri.getLine().getRequestURI().getPath();
if(length > list.size()) {length = list.size();}

KyoroFile[] responses = new KyoroFile[length*3+2];
KyoroFile content = mFile;

ByteKyoroFile header = new ByteKyoroFile();
int r = 0;
long resLen = content.length();
long contentLength = 0;
responses[r] = header;r++;


///
header = new ByteKyoroFile();
//header.addChunk(("--THIS_STRING_SEPARATES\r\n\r\n").getBytes());
header.addChunk(("--THIS_STRING_SEPARATES\r\n").getBytes());
responses[r++] = header;
contentLength += responses[r-1].length();
System.out.println("==lend["+(r-1)+"]="+contentLength+","+responses[r-1].length());
for(int i=0;i<length;i++) {
	///
	header = new ByteKyoroFile();
	long start = list.getPieceInfo(i).getStart();
	long end = list.getPieceInfo(i).getEnd();
	header.addChunk(("Content-Range: bytes "+ start+ "-"+end+"/"+resLen+"\r\n").getBytes());
	header.addChunk(("Content-Type: "+getMimeType(path)+"\r\n\r\n").getBytes());
	responses[r++] = header;
	contentLength += responses[r-1].length();
	System.out.println("==lena["+(r-1)+"]="+contentLength+","+responses[r-1].length());

	///
	System.out.println("==lenb["+(r-1)+"]="+list.getPieceInfo(i).getStart()+","+ list.getPieceInfo(i).getEnd());
	responses[r++] = KFNextHelper.subSequence(mFile, list.getPieceInfo(i).getStart(), list.getPieceInfo(i).getEnd()+1);
	contentLength += responses[r-1].length();
	System.out.println("==lenc["+(r-1)+"]="+contentLength+","+responses[r-1].length());
	
	///
	header = new ByteKyoroFile();
//	header.addChunk(("--THIS_STRING_SEPARATES\r\n\r\n").getBytes());
	header.addChunk(("--THIS_STRING_SEPARATES\r\n").getBytes());
	responses[r++] = header;
	contentLength += responses[r-1].length();
	System.out.println("==lend["+(r-1)+"]="+contentLength+","+responses[r-1].length());
}


responses[0].addChunk(("HTTP/1.1 206 Partial Content\r\n").getBytes());			
responses[0].addChunk(("Content-Length: "+contentLength+"\r\n").getBytes());
responses[0].addChunk(("Accept-Ranges: bytes\r\n").getBytes());
responses[0].addChunk(("Connection: close\r\n").getBytes());
responses[0].addChunk(("Content-Type: multipart/byteranges; boundary=THIS_STRING_SEPARATES\r\n").getBytes());
responses[0].addChunk(("\r\n").getBytes());


System.out.print("#--");
for(int i=0;i<responses.length;i++) {
	responses[i].seek(0);
	if(responses[i] instanceof ByteKyoroFile) {
		byte[] b = KFNextHelper.newBinary(responses[i]);
		System.out.print("#"+new String(b));
	}
}
return new KyoroFileForFiles(responses);
}
*/