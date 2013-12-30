package net.hetimatan.net.ssdp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.util.http.HttpRequestHeader;
import net.hetimatan.util.io.ByteArrayBuilder;

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

	public static SSDPMessage decode(MarkableFileReader reader) throws IOException {
		SSDPResponseLine line = SSDPResponseLine.decode(reader);
		SSDPMessage message = new SSDPMessage(line);
		while(reader.peek()!=-1 && !SSDPResponseLine.crlf(reader, false)) {
			HttpRequestHeader header = HttpRequestHeader.decode(reader);
			message.add(header);
		}
		return message;
	}

	public static class SSDPResponseLine {
		private String mCode = null;
		private String mMessage = null;
		
		public SSDPResponseLine(String code, String message) {
			mCode = code;
			mMessage = message;
		}
	
		public static SSDPResponseLine decode(MarkableFileReader reader) throws IOException {
			reader.pushMark();
			try {
				version(reader);
				sp(reader);
				String code = code(reader);
				System.out.println("#"+code+"#");
				sp(reader);
				String message = message(reader);
				System.out.println("#"+message+"#");
				crlf(reader, true);
				return new SSDPResponseLine(code, message);
			} catch(IOException e) {
				reader.backToMark();
				throw e;
			} finally {
				reader.popMark();
			}
		}

		public static boolean crlf(MarkableFileReader reader, boolean throwable) throws IOException {
			int v = reader.read();
			if(v == '\n') {
				return true;
			}
			if(v == '\r' && '\n' == reader.read()) {
				return true;
			}
			if(throwable) {
				throw new IOException("");
			} else {
				return false;
			}
		}
	
		public static void sp(MarkableFileReader reader) throws IOException {
			reader.pushMark();
			try {
				if(reader.read() != 0x20) {
					reader.backToMark();
					throw new IOException("");
				}
			} finally {
				reader.popMark();
			}
		}

		public static String message(MarkableFileReader reader) throws IOException {
			ByteArrayBuilder builder = new ByteArrayBuilder();
			int v  = 0;
			do {
				v = reader.peek();
				if(v == -1) {break;}
				if(v == '\n'){break;}
				if(v == '\r'){break;}
				builder.append((byte)(0xFF&reader.read()));
			} while(true);
			return new String(builder.getBuffer(), 0, builder.length());
		}

		public static String code(MarkableFileReader reader) throws IOException {
			ByteArrayBuilder builder = new ByteArrayBuilder();
			
			reader.pushMark();
			try {
				int v = reader.peek();
				if(!(0x30<=v && v<=0x39)) {
					reader.backToMark();
					throw new IOException("");
				}
				do {
					v = reader.peek();
					if(!(0x30<=v && v<=0x39)) {
						break;
					}
					builder.append((byte)(0xFF&v));
					reader.read();
				} while(true);
			} finally {
				reader.popMark();
			}
			return new String(builder.getBuffer(), 0, builder.length());
		}

 		public static String version(MarkableFileReader reader) throws IOException {
			byte[] expected1 = "HTTP/1.1".getBytes();
			byte[] expected2 = "http/1.1".getBytes();

			reader.pushMark();
			try {
				for(int i=0;i<expected1.length;i++) {
					int v = reader.read();
					if(!(expected1[i] == v || expected2[i] == v)) {
						reader.backToMark();
						throw new IOException("");
					}
				}
			} finally {
				reader.popMark();
			}
			return "HTTP/1.1";
		}
	}
}

