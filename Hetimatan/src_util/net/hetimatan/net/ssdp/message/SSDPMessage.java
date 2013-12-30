package net.hetimatan.net.ssdp.message;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.util.http.HttpRequestHeader;

public class SSDPMessage {
	private SSDPResponseLine mLine = null;
	private LinkedList<HttpRequestHeader> mHeader = new LinkedList<>();
	public SSDPMessage(SSDPResponseLine line) {
		mLine = line;
	} 

	public void add(HttpRequestHeader header) {
		mHeader.add(header);
	}

	public void encode(OutputStream output) throws IOException {
		;
	}

	public SSDPResponseLine getLine() {
		return mLine;
	}

	public int numOfHeader() {
		return mHeader.size();
	}

	public HttpRequestHeader getHeader(int i) {
		return mHeader.get(i);
	}
	
	public static SSDPMessage decode(MarkableFileReader reader) throws IOException {
		SSDPResponseLine line = SSDPResponseLine.decode(reader);
		SSDPMessage message = new SSDPMessage(line);
		while(reader.peek()!=-1 && !SSDPResponseLine.crlf(reader, false)) {
			HttpRequestHeader header = HttpRequestHeader.decode(reader);
			message.add(header);
		}
		return message;
	}

}

