package info.kyorohiro.helloworld.io.next;

import info.kyorohiro.raider.util.TestUtil;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import net.hetimatan.io.filen.KFNextHelper;
import net.hetimatan.io.filen.RACashFile;

import junit.framework.TestCase;

public class TestForKFNextHelper extends TestCase {

	public void testOne() throws IOException {
		RACashFile src = new RACashFile(10, 10);
		RACashFile out = new RACashFile(5, 20);
		byte[] exp = new byte[1024];
		Random r = new Random(777);
		for(int i=0;i<exp.length;i++) {
			exp[i] = (byte)r.nextInt();
		}
		try {
			src.write(exp);
			src.seek(0);
			KFNextHelper.copy(src, out);
			byte[] result = KFNextHelper.newBinary(out);
			TestUtil.assertArrayEquals(this, "", exp, result);
		} finally {
			src.close();
			out.close();
		}
	}

	public void testXCopy() throws IOException {
		File f1 = new File("./f1");
		File f2 = new File("./f2");
		RACashFile src1 = new RACashFile(f1, 10, 10);
		RACashFile src2 = new RACashFile(f2, 10, 10);
		RACashFile out = new RACashFile(5, 20);
		byte[] exp = new byte[1024];
		Random r = new Random(777);
		for(int i=0;i<exp.length;i++) {
			exp[i] = (byte)r.nextInt();
		}

		src1.write(exp, 0, exp.length/2);
		src1.syncWrite();
		src2.write(exp, exp.length/2, exp.length-exp.length/2);
		src2.syncWrite();
		src1.close();
		src2.close();

		File[] srcs = {f1, f2};

		try {
			out.seek(0);
			KFNextHelper.xcopy(srcs, out);
			byte[] result = KFNextHelper.newBinary(out);
			TestUtil.assertArrayEquals(this, "", exp, result);
		} finally {
			src1.close();
			out.close();
		}
	}

	public void testTwo() throws IOException {
		RACashFile src1 = new RACashFile(10, 10);
		RACashFile src2 = new RACashFile(10, 10);
		RACashFile out = new RACashFile(5, 20);
		byte[] exp = new byte[1024];
		Random r = new Random(777);
		for(int i=0;i<exp.length;i++) {
			exp[i] = (byte)r.nextInt();
		}
		
		try {
			src1.write(exp, 0, exp.length/2);
			src2.write(exp, exp.length/2, exp.length-exp.length/2);
			src1.seek(0);
			src2.seek(0);
			KFNextHelper.copy(src1, out);
			assertEquals(exp.length/2, src1.length());
			assertEquals(exp.length/2, src2.length());
			out.seek(src1.length());
			KFNextHelper.copy(src2, out);
			byte[] result = KFNextHelper.newBinary(out);
			TestUtil.assertArrayEquals(this, "", exp, result);
		} finally {
			src1.close();
			out.close();
		}
	}

}
