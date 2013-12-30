package net.hetimatan.net.ssdp.message;

import java.io.IOException;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.util.io.ByteArrayBuilder;

public class SSDPStartLine {
	public static int TYPE_NOTIFY = 1;
	public static int TYPE_MSEARCH = 2;
	public static int TYPE_RESPONSE = 3;
	private int mType = 0;
	private String mCode = null;
	private String mMessage = null;
	
	public SSDPStartLine(String code, String message) {
		mCode = code;
		mMessage = message;
		mType = TYPE_RESPONSE;
	}

	public SSDPStartLine(int type) {
		mCode = "200";
		mMessage = "";
		mType = type;
	}

	public int getType() {
		return mType;
	}

	public String getCode() {
		return mCode;
	}

	public String getMessage() {
		return mMessage;
	}

	public static SSDPStartLine decode(MarkableFileReader reader) throws IOException {
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
			return new SSDPStartLine(code, message);
		} catch(IOException e) {
			reader.backToMark();
			throw e;
		} finally {
			reader.popMark();
		}
	}

	public static SSDPStartLine responseLine(MarkableFileReader reader) throws IOException {
		try {
			return responseLine(reader);
		} catch(IOException e) {
		}
		try {
			return notifyhLine(reader);
		} catch(IOException e) {
		} 
		return mSearchLine(reader);
	}

	public static SSDPStartLine mSearchLine(MarkableFileReader reader) throws IOException {
		reader.pushMark();
		try {
			_m_search(reader);
			sp(reader);
			_astarisk(reader);
			sp(reader);
			version(reader);
			crlf(reader, true);
			return new SSDPStartLine(TYPE_MSEARCH);
		} catch(IOException e) {
			reader.backToMark();
			throw e;
		} finally {
			reader.popMark();
		}
	}

	public static SSDPStartLine notifyhLine(MarkableFileReader reader) throws IOException {
		reader.pushMark();
		try {
			_notify(reader);
			sp(reader);
			_astarisk(reader);
			sp(reader);
			version(reader);
			crlf(reader, true);
			return new SSDPStartLine(TYPE_NOTIFY);
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

	public static String _m_search(MarkableFileReader reader) throws IOException {
		byte[] expected1 = "m-search".getBytes();
		byte[] expected2 = "M-SEARCH".getBytes();

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
		return "M-SEARCH";
	}

	public static String _notify(MarkableFileReader reader) throws IOException {
		byte[] expected1 = "notify".getBytes();
		byte[] expected2 = "NOTIFY".getBytes();

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
		return "NOTIFY";
	}

	public static String _astarisk(MarkableFileReader reader) throws IOException {
		byte[] expected1 = "*".getBytes();

		reader.pushMark();
		try {
			for(int i=0;i<expected1.length;i++) {
				int v = reader.read();
				if(!(expected1[i] == v)) {
					reader.backToMark();
					throw new IOException("");
				}
			}
		} finally {
			reader.popMark();
		}
		return "*";
	}

}