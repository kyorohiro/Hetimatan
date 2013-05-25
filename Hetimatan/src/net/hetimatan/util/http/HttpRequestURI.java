package net.hetimatan.util.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;

import net.hetimatan.io.file.MarkableReader;

//http://www.w3.org/Protocols/rfc2616/rfc2616.html
//Request-URI    = "*" | absoluteURI | abs_path | authority
//Request-Line   = Method SP Request-URI SP HTTP-Version CRLF
public class HttpRequestURI extends HttpObject {

	private HttpRequestLine mLine = null;
	private LinkedList<HttpHeader> mHeaders = new LinkedList<HttpHeader>();

	public static HttpRequestURI newInstance(String method, String requestUri,
			String httpVersion) {
		HttpRequestLine line = new HttpRequestLine(method, requestUri,
				httpVersion);
		return new HttpRequestURI(line);
	}

	public HttpRequestURI(HttpRequestLine line) {
		mLine = line;
	}

	public HttpRequestLine getLine() {
		return mLine;
	}

	public LinkedList<HttpHeader> getHeader() {
		return mHeaders;
	}

	public HttpRequestURI putValue(String key, String value) {
		mLine.putValue(key, value);
		return this;
	}

	public String getValue(String key) {
		return mLine.getRequestURI().getValue(key);
	}

	public String getHeaderValue(String key) {
		for(HttpHeader h:mHeaders) {
			if (key.equals(h.getKey())) {
				return h.getValue();
			}
		}
		return "";
	}

	public HttpRequestURI addHeader(String key, String value) {
		return addHeader(new HttpHeader(key, value));
	}

	public HttpRequestURI addHeader(HttpHeader header) {
		mHeaders.add(header);
		return this;
	}

	@Override
	public void encode(OutputStream output) throws IOException {
		mLine.encode(output);
		for (HttpHeader header : mHeaders) {
			header.encode(output);
		}
		output.write(CRLF.getBytes());
	}

	public static HttpRequestURI decode(MarkableReader reader)
			throws IOException {
		HttpRequestLine line = HttpRequestLine.decode(reader);
		HttpRequestURI ret = new HttpRequestURI(line);
		try {
			while (true) {
				if (isCrlf(reader)) {
					break;
				}
				ret.addHeader(HttpHeader.decode(reader));
			}
		} catch (IOException e) {
		}
		_crlf(reader);
		return ret;
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
