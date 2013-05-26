package info.kyorohiro.raider.util.http;

import java.io.IOException;
import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.filen.RACashFile;
import net.hetimatan.util.http.HttpHeader;
import net.hetimatan.util.http.HttpObject;
import net.hetimatan.util.http.HttpRequestLine;
import net.hetimatan.util.http.HttpRequestURI;
import net.hetimatan.util.http.HttpResponse;
import net.hetimatan.util.io.ByteArrayBuilder;

import junit.framework.TestCase;

public class TestForHttpHeader extends TestCase {

	public void testHello() {

	}

	public void testEncode001() throws IOException {
		HttpHeader header = new HttpHeader("key","value");
		RACashFile output = new RACashFile(512);
		header.encode(output.getLastOutput());

		output.seek(0);
		byte[] buffer = new byte[(int)output.length()];
		int len = output.read(buffer);
		String tag = new String(buffer, 0, len);
		assertEquals("key:value\r\n", tag);
	}

	public void testDecode001() throws IOException {
		RACashFile base = new RACashFile(512);
		base.addChunk("key:value\r\n".getBytes());
		MarkableFileReader reader = new MarkableFileReader(base, 512);

		HttpHeader value = HttpHeader.decode(reader);
		assertEquals("key", value.getKey());
		assertEquals("value", value.getValue());
	}

}
