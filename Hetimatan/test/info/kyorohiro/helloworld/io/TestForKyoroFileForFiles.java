package info.kyorohiro.helloworld.io;

import info.kyorohiro.helloworld.io.next.RACashFile;

import java.io.IOException;

import junit.framework.TestCase;

public class TestForKyoroFileForFiles extends TestCase {

	public void testHello() {
		
	}

	public void testOne001() throws IOException {
		RACashFile[] files = new RACashFile[1];
		files[0] = new RACashFile("あいう".getBytes());
		KyoroFileForFiles kff = new KyoroFileForFiles(files);
		byte[] buffer = new byte[256];
		int len = kff.read(buffer);
		assertEquals("あいう".getBytes().length, len);
		assertEquals("あいう", new String(buffer, 0, len));
	}

	public void testOne002() throws IOException {
		byte[] testdata = new byte[256*1024-1];
		for(int i=0;i<testdata.length;i++){
			testdata[i] = (byte)Math.random();
		}
		RACashFile[] files = new RACashFile[1];
		files[0] = new RACashFile(testdata);
		KyoroFileForFiles kff = new KyoroFileForFiles(files);
		byte[] buffer = new byte[256];
		int len = kff.read(buffer);
		assertEquals(256, len);
		for(int i=0;i<256;i++) {
			assertEquals(testdata[i], buffer[i]);
		}

		
		buffer = new byte[testdata.length];
		kff.seek(0);
		len = kff.read(buffer);
		assertEquals(buffer.length, len);
		for(int i=0;i<buffer.length;i++) {
			assertEquals(testdata[i], buffer[i]);
		}
	}

	public void testTwo001() throws IOException {
		RACashFile[] files = new RACashFile[2];
		files[0] = new RACashFile("あいう".getBytes());
		files[1] = new RACashFile("かきく".getBytes());
		KyoroFileForFiles kff = new KyoroFileForFiles(files);
		byte[] buffer = new byte[256];
		int len = kff.read(buffer);
		assertEquals("あいうかきく".getBytes().length, len);
		assertEquals("あいうかきく", new String(buffer, 0, len));
	}

	public void testTwo002() throws IOException {
		RACashFile[] files = new RACashFile[3];
		files[0] = new RACashFile("あいう".getBytes());
		files[1] = new RACashFile("かきく".getBytes());
		files[2] = new RACashFile("さ".getBytes());
		KyoroFileForFiles kff = new KyoroFileForFiles(files);
		try {
			byte[] buffer = new byte[256];
			int len = kff.read(buffer);
			assertEquals("あいうかきくさ".getBytes().length, len);
			assertEquals("あいうかきくさ", new String(buffer, 0, len));
		} finally {
			kff.close();
		}
	}
}
