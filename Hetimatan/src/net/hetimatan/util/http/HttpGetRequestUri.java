package net.hetimatan.util.http;


import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Set;

import net.hetimatan.ky.io.MarkableFileReader;
import net.hetimatan.ky.io.MarkableReader;
import net.hetimatan.ky.io.next.RACashFile;

// GET request
// http://www.w3schools.com/tags/ref_httpmethods.asp
public class HttpGetRequestUri extends HttpObject {
	private String mPath = "";
	private LinkedHashMap<String, String> mValues = new LinkedHashMap<String, String>();

	// todo throw IOEXception?
	public static HttpGetRequestUri crateHttpGetRequestUri(String requestPath) {
		try {
			RACashFile base = new RACashFile(requestPath.getBytes());
			MarkableReader reader  = new MarkableFileReader(base, 100);
			return HttpGetRequestUri.decode(reader);
		} catch(Exception e) {
			return new HttpGetRequestUri(requestPath);
		}
	}

	public HttpGetRequestUri(String path) {
		mPath = path;
	}

	@Override
	public String toString() {
		try {
			String ret = HttpObject.createEncode(this);
			return ret;
		} catch (IOException e) {
			return "#Failed toString#";
		}
	}

	public void putVale(String key, String value) {
		mValues.put(key, value);
	}

	public Set<String> keySet() {
		return mValues.keySet();
	}

	public String getPath() {
		return mPath;
	}

	public String getValue(String key) {
		return mValues.get(key);
	}

	//
	// /test/demo_form.asp?name1=value1&name2=value2
	//
	@Override
	public void encode(OutputStream output) throws IOException {
		output.write(mPath.getBytes());
		Set<String> keys = mValues.keySet();
		boolean isFirst = true;
		for (String key : keys) {
			if (true == isFirst) {
				isFirst = false;
				output.write("?".getBytes());
			} else {
				output.write("&".getBytes());
			}
			output.write(key.getBytes());
			output.write("=".getBytes());
			output.write(mValues.get(key).getBytes());
		}
	}

/*	public String createEncode() throws IOException {
		VirtualFile vFile = new VirtualFile(512);
		encode(vFile);
		byte[] buffer = new byte[(int)vFile.length()];
		int len = vFile.read(buffer);
		return new String(buffer, 0, len);
	}
*/
	//
	// /test/demo_form.asp?name1=value1&name2=value2
	public static HttpGetRequestUri decode(MarkableReader reader) throws IOException {
		String path = _path(reader);
		HttpGetRequestUri ret = new HttpGetRequestUri(path);
		if (reader.peek() == -1) {
			return ret;
		}
		if(reader.peek() == '?') {
			_keyValues(reader, ret);
		}
		return ret;
	}

	private static String _path(MarkableReader reader) throws IOException {
		return _value(reader, "?".getBytes(), true);
	}

	private static void _keyValues(MarkableReader reader, HttpGetRequestUri uri) throws IOException {
		int datam = 0;
		boolean isFirst = true;
		while(true) {
			datam = reader.peek();
			if(-1 == datam) {
				break;
			}
			if (true == isFirst) {
				isFirst = false;
				_question(reader);
			} else {
				_ampersand(reader);
			}
			_keyValue(reader, uri);
		}
	}

	private static void _keyValue(MarkableReader reader, HttpGetRequestUri uri) throws IOException {
		String key = _value(reader, "=".getBytes(), false);
		_value(reader, (byte)'=');
		String value = _value(reader, "&".getBytes(), true);
		uri.putVale(key, value);
	}
	
	private static void _ampersand(MarkableReader reader) throws IOException {
		_value(reader, (byte) '&');
	}

	private static void _question(MarkableReader reader) throws IOException {
		_value(reader, (byte) '?');
	}
}