package net.hetimatan.util.http;


import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Set;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.io.file.MarkableReaderHelper;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.util.io.ByteArrayBuilder;

// GET request
// http://www.w3schools.com/tags/ref_httpmethods.asp
// Request-URI    = "*" | absoluteURI | abs_path | authority
public class HttpRequestUri extends HttpObject {
	private String mPath = "";
	private LinkedHashMap<String, String> mValues = new LinkedHashMap<String, String>();

	// todo throw IOEXception?
	public static HttpRequestUri crateHttpGetRequestUri(String requestPath) {
		try {
			CashKyoroFile base = new CashKyoroFile(requestPath.getBytes());
			MarkableReader reader  = new MarkableFileReader(base, 100);
			return HttpRequestUri.decode(reader);
		} catch(Exception e) {
			return new HttpRequestUri(requestPath);
		}
	}

	public HttpRequestUri(String path) {
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

	//
	// /test/demo_form.asp?name1=value1&name2=value2
	public static HttpRequestUri decode(MarkableReader reader) throws IOException {
		String path = _path(reader);
		HttpRequestUri ret = new HttpRequestUri(path);
		if (reader.peek() == -1) {
			return ret;
		}
		if(reader.peek() == '?') {
			_keyValues(reader, ret);
		}
		return ret;
	}

	public static HttpRequestUri astarisk(MarkableReader reader) throws IOException  {
		MarkableReaderHelper.match(reader, "*".getBytes());
		HttpRequestUri ret = new HttpRequestUri("*");
		return ret;
	}

	public static HttpRequestUri absPath(MarkableReader reader) throws IOException {
		String path = "/";
		try {
			path = path(reader);
		} catch(IOException e) {
			;
		}
		
		HttpRequestUri ret = new HttpRequestUri(path);

		try {
			MarkableReaderHelper.match(reader, "?".getBytes());
			query(reader, ret);
		} catch(IOException e) {
		}
		
		try {
			fragment(reader);
		} catch(IOException e) {
		}
		
		return ret;
	}

	public static HttpRequestUri absoluteUri(MarkableReader reader) throws IOException {
		int port = 80;
		String path = "/";
		scheme(reader); 
		MarkableReaderHelper.match(reader, "://".getBytes());
		host(reader);
		try {
			MarkableReaderHelper.match(reader, ":".getBytes());
			port = port(reader);
		} catch(IOException e) {
		}
		try {
			path = path(reader);
		} catch(IOException e) {
			;
		}
		
		HttpRequestUri ret = new HttpRequestUri(path);

		try {
			MarkableReaderHelper.match(reader, "?".getBytes());
			query(reader, ret);
		} catch(IOException e) {
		}
		
		try {
			fragment(reader);
		} catch(IOException e) {
		}
		
		return ret;
	}

	public static String fragment(MarkableReader reader) throws IOException {
		final byte[] available = {
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
				'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
				'u', 'v', 'w', 'x', 'y', 'z',
				'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
				'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
				'U', 'V', 'W', 'X', 'Y', 'Z',
				'.', '-', '/'
		};

		try {
			MarkableReaderHelper.match(reader, "#".getBytes());
			return "#" + new String(MarkableReaderHelper.jumpAndGet(reader, available, 256));
		} catch(IOException e) {
			throw e;
		}
	}

	public static void query(MarkableReader reader, HttpRequestUri uri) throws IOException {
		final byte[] keyAndvalue= {
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
				'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
				'u', 'v', 'w', 'x', 'y', 'z',
				'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
				'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
				'U', 'V', 'W', 'X', 'Y', 'Z',
				'.', '-', '/'
		};

		do {
			try {
				reader.pushMark();
				String key = new String(MarkableReaderHelper.jumpAndGet(reader, keyAndvalue, 256));
				MarkableReaderHelper.match(reader, "=".getBytes());
				String value = new String(MarkableReaderHelper.jumpAndGet(reader, keyAndvalue, 256));
				uri.putVale(key, value);
			} catch(IOException e) {
				reader.backToMark();
				break;
			} finally {
				reader.popMark();				
			}
		} while(true);
	}

	public static String path(MarkableReader reader) throws IOException {
		final byte[] available= {
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
				'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
				'u', 'v', 'w', 'x', 'y', 'z',
				'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
				'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
				'U', 'V', 'W', 'X', 'Y', 'Z',
				'.', '-', '/'
		};

		try {
			MarkableReaderHelper.match(reader, "/".getBytes());
			return "/"+new String(MarkableReaderHelper.jumpAndGet(reader, available, 256));
		} catch(IOException e) {
			throw e;
		}
	}

	public static int port(MarkableReader reader) throws IOException, NumberFormatException {
		final byte[] available= {
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
		};

		try {
			reader.pushMark();
			return Integer.parseInt(new String(MarkableReaderHelper.jumpAndGet(reader, available, 256)));
		} catch(IOException e) {
			reader.backToMark();
			throw e;
		} catch(NumberFormatException e) {
			reader.backToMark();
			throw e;
		} finally  {
			reader.popMark();
		}
	}

	public static String host(MarkableReader reader) throws IOException {
		final byte[] available= {
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
				'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
				'u', 'v', 'w', 'x', 'y', 'z',
				'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
				'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
				'U', 'V', 'W', 'X', 'Y', 'Z',
				'.', '-'
		};

		try {
			return new String(MarkableReaderHelper.jumpAndGet(reader, available, 256));
		} catch(IOException e) {
			throw e;
		}
	}

	public static String scheme(MarkableReader reader) throws IOException {
		try {
			MarkableReaderHelper.match(reader, ("https").getBytes());
			return "https";
		} catch(IOException e) {
		}
		try {
			MarkableReaderHelper.match(reader, ("http").getBytes());
			return "http";
		} catch(IOException e) {
			throw e;
		}
	}

	private static String _path(MarkableReader reader) throws IOException {
		return _value(reader, "?".getBytes(), true);
	}

	private static void _keyValues(MarkableReader reader, HttpRequestUri uri) throws IOException {
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

	private static void _keyValue(MarkableReader reader, HttpRequestUri uri) throws IOException {
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