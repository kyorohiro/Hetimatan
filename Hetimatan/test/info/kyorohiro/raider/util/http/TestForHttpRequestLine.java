package info.kyorohiro.raider.util.http;

import info.kyorohiro.helloworld.io.MarkableFileReader;
import info.kyorohiro.helloworld.io.next.RACashFile;

import java.io.IOException;

import junit.framework.TestCase;

public class TestForHttpRequestLine extends TestCase {

	public void testHello() {

	}

	public void testEncode001() throws IOException {
		//(String method, String requestUri, String httpVersion)
		HttpRequestLine line = new HttpRequestLine(HttpRequestLine.GET, "/announce?a=b&b=c", HttpRequestLine.HTTP11);
		RACashFile output = new RACashFile(512);
		line.encode(output.getLastOutput());

		output.seek(0);
		byte[] buffer = new byte[(int)output.length()];
		int len = output.read(buffer);
		String tag = new String(buffer, 0, len);
		assertEquals("GET /announce?a=b&b=c HTTP/1.1\r\n", tag);
	}

	public void testDecode001() throws IOException {
		RACashFile base = new RACashFile(512);
		base.addChunk("GET /announce?a=b&b=c HTTP/1.1\r\n".getBytes());
		MarkableFileReader reader = new MarkableFileReader(base, 512);

		HttpRequestLine value = HttpRequestLine.decode(reader);
		assertEquals("GET", value.getMethod());
		assertEquals("/announce?a=b&b=c", HttpObject.createEncode(value.getRequestURI()));
		assertEquals("HTTP/1.1", value.getHttpVersion());
	}

}
