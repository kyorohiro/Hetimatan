package info.kyorohiro.helloworld.io.next;

import java.io.IOException;

import net.hetimatan.io.filen.ByteKyoroFile;

import junit.framework.TestCase;

public class TestForByteKyoroFile extends TestCase {

	public void testRead_zero() throws IOException {
		ByteKyoroFile file = new ByteKyoroFile();
		try {
			byte[] buffer = new byte[100];
			int len = file.read(buffer);
			assertEquals(0, len);
			assertEquals(0, file.getFilePointer());
			assertEquals(0, file.length());
		} finally {
			file.close();
		}
	}

	public void testWriteRead() throws IOException {
		ByteKyoroFile file = new ByteKyoroFile();
		try {
			file.write(1);
			file.write(2);
			file.seek(0);
			byte[] buffer = new byte[100];
			int len = file.read(buffer);
			assertEquals(2, len);
			assertEquals(2, file.getFilePointer());
			assertEquals(2, file.length());
			assertEquals(1, buffer[0]);
			assertEquals(2, buffer[1]);
		} finally {
			file.close();
		}
	}

	public void testSeek_append1() throws IOException {
		ByteKyoroFile file = new ByteKyoroFile();
		try {
			file.write(1);
			file.write(2);
			file.seek(10);
			file.seek(0);
			byte[] buffer = new byte[100];
			int len = file.read(buffer);
			assertEquals(10, len);
			assertEquals(10, file.getFilePointer());
			assertEquals(10, file.length());
			assertEquals(1, buffer[0]);
			assertEquals(2, buffer[1]);
			assertEquals(0, buffer[9]);
		} finally {
			file.close();
		}
	}

	public void testSeekWrite_append2() throws IOException {
		ByteKyoroFile file = new ByteKyoroFile();
		try {
			file.write(1);
			file.write(2);
			file.seek(10);
			file.write(9);
			file.seek(0);
			byte[] buffer = new byte[100];
			int len = file.read(buffer);
			assertEquals(11, len);
			assertEquals(11, file.getFilePointer());
			assertEquals(11, file.length());
			assertEquals(1, buffer[0]);
			assertEquals(2, buffer[1]);
			assertEquals(9, buffer[10]);
		} finally {
			file.close();
		}
	}

	public void testSeekWrite_back() throws IOException {
		ByteKyoroFile file = new ByteKyoroFile();
		try {
			file.write(1);
			file.write(2);
			file.seek(10);
			file.write(3);
			file.seek(0);
			file.write(4);
			file.seek(0);

			byte[] buffer = new byte[100];
			int len = file.read(buffer);
			assertEquals(11, len);
			assertEquals(11, file.getFilePointer());
			assertEquals(11, file.length());
			assertEquals(4, buffer[0]);
			assertEquals(2, buffer[1]);
			assertEquals(3, buffer[10]);
		} finally {
			file.close();
		}
	}

	//
	// SKIP -------------------------------------
	//
	public void testSkip_Read_zero() throws IOException {
		ByteKyoroFile file = new ByteKyoroFile();
		try {
			byte[] buffer = new byte[100];
			file.skip(100);
			file.seek(100);
			int len = file.read(buffer);
			assertEquals(0, len);
			assertEquals(100, file.getFilePointer());
			assertEquals(100, file.length());
		} finally {
			file.close();
		}
	}


	public void testSkip_WriteRead() throws IOException {
		ByteKyoroFile file = new ByteKyoroFile();
		try {
			file.skip(100);
			file.seek(100);
			file.write(1);
			file.write(2);
			file.seek(100);
			byte[] buffer = new byte[100];
			int len = file.read(buffer);
			assertEquals(2, len);
			assertEquals(102, file.getFilePointer());
			assertEquals(102, file.length());
			assertEquals(1, buffer[0]);
			assertEquals(2, buffer[1]);
		} finally {
			file.close();
		}
	}


	public void testSkip_Seek_append1() throws IOException {
		ByteKyoroFile file = new ByteKyoroFile();
		try {
			file.skip(100);
			file.seek(100);
			file.write(1);
			file.write(2);
			file.seek(110);
			file.seek(100);
			byte[] buffer = new byte[100];
			int len = file.read(buffer);
			assertEquals(10, len);
			assertEquals(110, file.getFilePointer());
			assertEquals(110, file.length());
			assertEquals(1, buffer[0]);
			assertEquals(2, buffer[1]);
			assertEquals(0, buffer[9]);
		} finally {
			file.close();
		}
	}

	public void testWriteRead2() throws IOException {
		ByteKyoroFile file = new ByteKyoroFile();
		try {
			file.write(1);
			file.write(2);
			file.write(3);
			file.write(4);
			file.seek(0);
			byte[] buffer = new byte[100];
			int len = file.read(buffer);
			assertEquals(4, len);
			assertEquals(4, file.getFilePointer());
			assertEquals(4, file.length());
			assertEquals(1, buffer[0]);
			assertEquals(2, buffer[1]);
			assertEquals(3, buffer[2]);
			assertEquals(4, buffer[3]);
			
			file.skip(2);
			file.seek(2);
			len = file.read(buffer);
			assertEquals(2, len);
			assertEquals(4, file.getFilePointer());
			assertEquals(4, file.length());
			assertEquals(3, buffer[0]);
			assertEquals(4, buffer[1]);
			
		} finally {
			file.close();
		}
	}
}
