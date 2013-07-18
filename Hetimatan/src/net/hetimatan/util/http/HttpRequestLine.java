package net.hetimatan.util.http;


import java.io.IOException;
import java.io.OutputStream;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.io.filen.CashKyoroFile;

//Request-Line   = Method SP Request-URI SP HTTP-Version CRLF
public class HttpRequestLine extends HttpObject {
	private String mMethod = "";
	private HttpRequestUri mRequestURI = null;
	private String mHTTPVersion = "";

	public static final String HTTP11 = "HTTP/1.1";
	public static final String HTTP10 = "HTTP/1.0";
	public static final String GET = "GET";

	public HttpRequestLine(String method, String requestUri, String httpVersion) {
		mMethod = method;
		mRequestURI = HttpRequestUri.crateHttpGetRequestUri(requestUri);
		mHTTPVersion = httpVersion;
	}

	@Override
	public String toString() {
		String ret = ""+mMethod +" "+mRequestURI.toString()+ " "+mHTTPVersion;
		return ret;
	}

	public HttpRequestLine putValue(String key, String value) {
		mRequestURI.putVale(key, value);
		return this;
	}

	public String getMethod() {
		return mMethod;
	}

	public HttpRequestUri getRequestURI() {
		return mRequestURI;
	}
	
	public String getHttpVersion() {
		return mHTTPVersion;
	}

	@Override
	public void encode(OutputStream output) throws IOException {
		output.write(mMethod.getBytes());
		output.write(SP.getBytes());
		mRequestURI.encode(output);
		output.write(SP.getBytes());
		output.write(mHTTPVersion.getBytes());
		output.write(CRLF.getBytes());
	}

	public static HttpRequestLine decode(String path) throws IOException {
		CashKyoroFile vFile = new CashKyoroFile(512, 2);
		vFile.addChunk(path.getBytes());
		MarkableReader reader = new MarkableFileReader(vFile, 256);
		return HttpRequestLine.decode(reader);
	}

	public static HttpRequestLine decode(MarkableReader reader)
			throws IOException {
		try {
			reader.pushMark();
			String method = _metod(reader);
			_sp(reader);
			String requestUri = _requestURI(reader);
			_sp(reader);
			String httpVersion = _httpVersion(reader);
			_crlf(reader);
			return new HttpRequestLine(method, requestUri, httpVersion);
		} finally {
			reader.popMark();
		}
	}

	private static String _metod(MarkableReader reader) throws IOException {
		return _value(reader, SP.getBytes(), false);
	}

	private static String _requestURI(MarkableReader reader)
			throws IOException {
		return _value(reader, SP.getBytes(), false);
	}

	private static String _httpVersion(MarkableReader reader)
			throws IOException {
		return _value(reader, CRLF.getBytes(), false);
	}
}
