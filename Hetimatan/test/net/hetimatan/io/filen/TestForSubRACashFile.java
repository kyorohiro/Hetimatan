package net.hetimatan.io.filen;

import java.io.IOException;
import java.util.Random;

import net.hetimatan.io.filen.AccessorFile;
import net.hetimatan.io.filen.RACashFile;

import junit.framework.TestCase;

public class TestForSubRACashFile extends TestCase {

	public void testZeroByte() throws IOException {
		RACashFile vf = null;
		try {
			vf = new RACashFile(10, 3);
			byte[] buffer = new byte[101];
			for(int i=0;i<buffer.length;i++) {buffer[0] = 0;}
			int ret = vf.read(buffer);
			
			//=====================
			AccessorFile scash = new AccessorFile(vf, 10, 100, 10, 3);
			scash.read(buffer);
			assertEquals(-1, ret);
			for(int i=0;i<buffer.length;i++) {assertEquals(0, buffer[0]);}
		} finally {
			vf.close();
		}
	}

	public void testXxxByte() throws IOException {
		RACashFile vf = null;
		try {
			vf = new RACashFile(10, 3);
			byte[] expected = new byte[101];
			Random r = new Random(123);
			for(int i=0;i<expected.length;i++) {
				expected[0] = (byte)r.nextInt();}
			int ret = vf.read(expected);
			
			//=====================
			byte[] buffer = new byte[100];
			AccessorFile scash = new AccessorFile(vf, 10, 100, 10, 3);
			ret = scash.read(buffer);
			assertEquals(90, ret);
			for(int i=10;i<100;i++) {assertEquals(expected[i], buffer[i-10]);}
		} finally {
			vf.close();
		}
	}
	
	public void testXxyByte() throws IOException {
		RACashFile vf = null;
		try {
			vf = new RACashFile(10, 3);
			byte[] expected = new byte[101];
			Random r = new Random(123);
			for(int i=0;i<expected.length;i++) {
				expected[0] = (byte)r.nextInt();}
			int ret = vf.read(expected);
			
			//=====================
			byte[] buffer = new byte[12];
			AccessorFile scash = new AccessorFile(vf, 10, 100, 10, 3);
			scash.seek(1);
			ret = scash.read(buffer);
			assertEquals(12, ret);
			for(int i=11;i<23;i++) {assertEquals(expected[i], buffer[i-11]);}
		} finally {
			vf.close();
		}
	}

	public void testXxzByte() throws IOException {
		RACashFile vf = null;
		try {
			vf = new RACashFile(10, 3);
			byte[] expected = new byte[90];
			Random r = new Random(123);
			for(int i=0;i<expected.length;i++) {
				expected[0] = (byte)r.nextInt();}
			int ret = vf.read(expected);
			
			//=====================
			byte[] buffer = new byte[1200];
			AccessorFile scash = new AccessorFile(vf, 10, 100, 10, 3);
			scash.seek(2);
			ret = scash.read(buffer);
			assertEquals(88, ret);
			for(int i=2;i<90;i++) {assertEquals(expected[i], buffer[i-2]);}
		} finally {
			vf.close();
		}
	}

}
