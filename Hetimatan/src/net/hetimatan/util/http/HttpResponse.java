package net.hetimatan.util.http;


import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;

import net.hetimatan.io.file.KyoroFile;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.io.filen.RACashFile;

// http://www.studyinghttp.net/cgi-bin/rfc.cgi?2616
//http://www.w3.org/Protocols/rfc2616/rfc2616.html
//Status-Line = HTTP-Version SP Status-Code SP Reason-Phrase CRLF
public class HttpResponse extends HttpObject {

	public static final String STATUS_CODE_301_MOVE_PERMANENTLY = "301 Moved Permanently";
	public static final String STATUS_CODE_302_Found = "302 Found";
	public static final String STATUS_CODE_303_SEE_OTHER= "303 See Other";
	public static final String STATUS_CODE_304_NOT_MODIFIED= "304 Not Modified";
	public static final String STATUS_CODE_305_USE_PROXY= "305 Use Proxy";
	public static final String STATUS_CODE_307_TEMPORARY_REDIRECT= "307 Temporary Redirect";

	
	private LinkedList<HttpHeader> mHeaders = new LinkedList<HttpHeader>();
	private String mHttpVersion = null;
	private String mStatusCode = null;
	private String mReasonPharse = null;
	private KyoroFile mContent = null;
	private HttpHeader mContentLength = null;

	public HttpResponse(String httpVersion, String statusCode,
			String reasonPhrase) {
		mHttpVersion = httpVersion;
		mStatusCode = statusCode;
		mReasonPharse = reasonPhrase;
	}

	public String getHttpVersion() {
		return mHttpVersion;
	}

	public String getStatusCode() {
		return mStatusCode;
	}

	public String getReasonPharse() {
		return mReasonPharse;
	}

	public KyoroFile getContent() {
		return mContent;
	}


	public void setContent(byte[] content) throws IOException {
		setContent(new RACashFile(content));
	}

	public void setContent(KyoroFile content) {
		mContent = content;
	}

	public LinkedList<HttpHeader> getHeader() {
		return mHeaders;
	}

	public HttpResponse addHeader(String key, String value) {
		return addHeader(new HttpHeader(key, value));
	}

	public HttpResponse addHeader(HttpHeader header) {
//		System.out.println("key="+header.getKey()+","+header.getValue());
		if (header.getKey().toLowerCase()
				.equals(HttpHeader.HEADER_CONTENT_LENGTH.toLowerCase())) {
			mContentLength = header;
		}
		mHeaders.add(header);
		return this;
	}

	public long getContentSizeFromHeader() {
		if(mContentLength == null) {
			return Integer.MAX_VALUE;
		} 
		String value = mContentLength.getValue();
		try {
			return Integer.parseInt(value.replaceAll(" ", ""));
		} catch(Exception e) {
			return 0;
		}
	}

	@Override
	public void encode(OutputStream output) throws IOException {
		output.write((mHttpVersion + SP + mStatusCode + SP + mReasonPharse + CRLF)
				.getBytes());
		long contentLength = 0;
		if(mContent != null) {
			contentLength = mContent.length();
		}
		output.write(("" + HttpHeader.HEADER_CONTENT_LENGTH + ": "
				+ contentLength + CRLF).getBytes());
		for (HttpHeader header : mHeaders) {
			header.encode(output);
		}
		output.write(CRLF.getBytes());

		byte[] buffer = new byte[100];
		while (contentLength!=0) {
			int len = mContent.read(buffer);
			if (len < 0) {
				break;
			}
			output.write(buffer, 0, len);
		}
		output.flush();
	}

	// Status-Line = HTTP-Version SP Status-Code SP Reason-Phrase CRLF
	public static HttpResponse decode(MarkableReader reader, boolean isReadBody) throws IOException {
		String httpVersion = "";
		String statusCode = "";
		String reasonPhrase = "";

//		System.out.println("------------1-------------");
		httpVersion = _httpVersion(reader);
		System.out.println("-httpVersion="+httpVersion+"#");
		_sp(reader);
//		System.out.println("------------2-------------");
		statusCode = _statusCode(reader);
		System.out.println("-statusCode="+statusCode+"#");
		_sp(reader);
//		System.out.println("------------3-------------");
		reasonPhrase = _reasonPhrase(reader);
		System.out.println("-reasonPhrase="+reasonPhrase+"#");
		_crlf(reader);
//		System.out.println("------------4-------------");
		HttpResponse ret = new HttpResponse(httpVersion, statusCode, reasonPhrase);

//		System.out.println("------------5-------------");
		try {
			while (true) {
				if (isCrlf(reader)||isLf(reader)) {
					break;
				}
				ret.addHeader(HttpHeader.decode(reader));
			}
		} catch (IOException e) {
		}

//		System.out.println("------------6-------------");
		_crlf(reader);
//		System.out.println("------------7-------------");

		long size = ret.getContentSizeFromHeader();
		System.out.println("------------8-------------"+size);
		if(size <0) {
			size = Integer.MAX_VALUE;
		}
		// 
		// todo MarkableReader内の VFを使用してもよいかも
		RACashFile vf = new RACashFile(512, 2);
		if(isReadBody) {
			int added = 0;
			byte[] buffer = new byte[1];
			while(true) {
				int value = reader.read();
				if(value<0||added>=size){
					break;
				}
				buffer[0] = (byte)(0xFF&value);
				vf.addChunk(buffer);
				added++;
			}
		}
		ret.setContent(vf);
		return ret;
	}

	public static String _httpVersion(MarkableReader reader) throws IOException {
		return _value(reader, SP.getBytes(), false);
	}

	public static String _statusCode(MarkableReader reader) throws IOException {
		return _value(reader, SP.getBytes(), false);
	}

	public static String _reasonPhrase(MarkableReader reader) throws IOException {
		try {
			String ret = _value(reader,"\n".getBytes(), false);
			//todo
			if(ret.endsWith("\r")) {
				ret = ret.substring(0, ret.length()-1);
			}
			return ret;
		} catch(IOException e) {
		}
		return _value(reader, CRLF.getBytes(), "\n".getBytes(), false);
	}

	public static boolean isLf(MarkableReader reader) {
		try {
			reader.pushMark();
			int cr = reader.read();
			if(cr =='\n'){
				return true;
			}
		} catch (IOException e) {
		} finally {
			reader.backToMark();
			reader.popMark();
		}
		return false;
	}
	public static boolean isCrlf(MarkableReader reader) {
		try {
			reader.pushMark();
			int cr = reader.read();
			int lf = reader.read();
			if (cr == '\r' && lf == '\n') {
				return true;
			}
		} catch (IOException e) {
		} finally {
			reader.backToMark();
			reader.popMark();
		}
		return false;
	}

}
