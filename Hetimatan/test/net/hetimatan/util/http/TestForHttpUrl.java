package net.hetimatan.util.http;

import java.io.IOException;


import net.hetimatan.io.filen.KFNextHelper;
import net.hetimatan.io.filen.CashKyoroFile;
import junit.framework.TestCase;

public class TestForHttpUrl extends TestCase {

	public void testEncode() throws IOException {
		CashKyoroFile output = new CashKyoroFile(512);
		try {
			HttpUrl url = new HttpUrl("127.0.0.1", "xx", 8080);
			url.encode(output.getLastOutput());
			String e = new String (KFNextHelper.newBinary(output));
			assertEquals("http://127.0.0.1:8080/xx", e);
		} finally {
			output.close();
		}
	}

	public void testDecode() {
		{
			String location = "http://127.0.0.1:8080/xxx?aaa=a:a";
			HttpUrl url = HttpUrl.decode(location);
			assertEquals("127.0.0.1", url.getHost());
			assertEquals(8080, url.getPort());
			assertEquals("/xxx", url.getMethod());
		}
		{
			String location = "127.0.0.1/yyy";
			HttpUrl url = HttpUrl.decode(location);
			assertEquals("127.0.0.1", url.getHost());
			assertEquals(80, url.getPort());
			assertEquals("/yyy", url.getMethod());
		}
	}
}

