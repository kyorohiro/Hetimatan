package net.hetimatan.util.http;


import java.io.IOException;
import java.io.OutputStream;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.io.filen.CashKyoroFile;

// KEY ":" VALUE CRLF
public class HttpHeader extends HttpObject {

	private String mKey = "";
	private String mValue = "";
	public static final String SEPARATOR = ":";
	public static final String HEADER_CONTENT_LENGTH = "Content-Length";
	public static final String HEADER_CONTENT_TyPE = "Content-Type";
	public static final String HEADER_LOCATION = "Location";
	public static final String HEADER_HOST = "Host";


	public HttpHeader(String key, String value) {
		mKey = key;
		mValue = value;
	}

	@Override
	public void encode(OutputStream output) throws IOException {
		output.write(mKey.getBytes());
		output.write(SEPARATOR.getBytes());
		output.write(mValue.getBytes());
		output.write(CRLF.getBytes());
	}

	public String getKey() {
		return mKey;
	}

	public String getValue() {
		return mValue;
	}

	public static HttpHeader decode(String path) throws IOException {
		CashKyoroFile vFile = new CashKyoroFile(512, 2);
		vFile.addChunk(path.getBytes());
		MarkableReader reader = new MarkableFileReader(vFile, 256);
		return HttpHeader.decode(reader);
	}

	public static HttpHeader decode(MarkableReader reader) throws IOException {
		String key = _key(reader);
		_sep(reader);
		String value = _val(reader);
		_crlf(reader);
		return new HttpHeader(key, value);
	}

	private static String _key(MarkableReader reader) throws IOException {
		return _value(reader, SEPARATOR.getBytes(), false);
	}

	private static String _val(MarkableReader reader) throws IOException {
		return _value(reader, CRLF.getBytes(),"\n".getBytes() , false);
	}

	private static void _sep(MarkableReader reader) throws IOException {
		if(reader.read() != ':' ) {
			throw new IOException();
		}
	}
	
}
