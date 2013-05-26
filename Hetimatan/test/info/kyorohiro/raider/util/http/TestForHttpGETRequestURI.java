package info.kyorohiro.raider.util.http;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.filen.RACashFile;
import net.hetimatan.util.http.HttpGetRequestUri;
import net.hetimatan.util.http.HttpHeader;
import net.hetimatan.util.http.HttpObject;
import net.hetimatan.util.http.HttpRequestLine;
import net.hetimatan.util.http.HttpRequestURI;
import net.hetimatan.util.http.HttpResponse;
import net.hetimatan.util.io.ByteArrayBuilder;

import java.io.IOException;
import java.util.Iterator;

import junit.framework.TestCase;

public class TestForHttpGETRequestURI  extends TestCase {

	public void testHello() {

	}

	public void testEncode001() throws IOException {
		HttpGetRequestUri uri = new HttpGetRequestUri("*");
		RACashFile output = new RACashFile(512);
		uri.encode(output.getLastOutput());
		output.seek(0);
		byte[] buffer = new byte[(int)output.length()];
		int len = output.read(buffer);
		String tag = new String(buffer, 0, len);
		assertEquals("*", tag);
	}

	public void testEncode002() throws IOException {
		HttpGetRequestUri uri = new HttpGetRequestUri("/announce");
		uri.putVale("01", "value01");

		RACashFile output = new RACashFile(512);
		uri.encode(output.getLastOutput());
		output.seek(0);
		byte[] buffer = new byte[(int)output.length()];
		int len = output.read(buffer);
		String tag = new String(buffer, 0, len);
		assertEquals("/announce?01=value01", tag);
	}

	public void testEncode003() throws IOException {
		HttpGetRequestUri uri = new HttpGetRequestUri("/announce");
		uri.putVale("01", "value01");
		uri.putVale("02", "value02");
		uri.putVale("03", "value03");
		RACashFile output = new RACashFile(512);
		uri.encode(output.getLastOutput());
		output.seek(0);
		byte[] buffer = new byte[(int)output.length()];
		int len = output.read(buffer);
		String tag = new String(buffer, 0, len);
		assertEquals("/announce?01=value01&02=value02&03=value03", tag);
	}

	public void testDecode001() throws IOException {
		RACashFile base = new RACashFile(512);
		base.addChunk("*".getBytes());
		MarkableFileReader reader = new MarkableFileReader(base, 512);

		HttpGetRequestUri value = HttpGetRequestUri.decode(reader);
		assertEquals("*", value.getPath());
	}

	public void testDecode002() throws IOException {
		RACashFile base = new RACashFile(512);
		base.addChunk("/announce?01=value01".getBytes());
		MarkableFileReader reader = new MarkableFileReader(base, 512);

		HttpGetRequestUri value = HttpGetRequestUri.decode(reader);
		assertEquals("/announce", value.getPath());
		Iterator<String> keys = value.keySet().iterator();
		assertEquals("/announce", value.getPath());
		String key = keys.next();
		assertEquals("01", key);
		assertEquals("value01", value.getValue(key));
		assertEquals(1, value.keySet().size());
	}

	public void testDecode003() throws IOException {
		RACashFile base = new RACashFile(512);
		base.addChunk("/announce?01=value01&02=value02&03=value03".getBytes());
		MarkableFileReader reader = new MarkableFileReader(base, 512);

		HttpGetRequestUri value = HttpGetRequestUri.decode(reader);
		assertEquals("/announce", value.getPath());

		Iterator<String> keys = value.keySet().iterator();
		{
			String key = keys.next();
			assertEquals("01", key);
			assertEquals("value01", value.getValue(key));
		}
		{
			String key = keys.next();
			assertEquals("02", key);
			assertEquals("value02", value.getValue(key));
		}
		{
			String key = keys.next();
			assertEquals("03", key);
			assertEquals("value03", value.getValue(key));
		}
		{
			assertEquals(3, value.keySet().size());
		}
	}
}
