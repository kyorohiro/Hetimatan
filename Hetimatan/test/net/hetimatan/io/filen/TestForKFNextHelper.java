package net.hetimatan.io.filen;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import net.hetimatan.io.filen.CashKyoroFileHelper;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.util.test.TestUtil;
import junit.framework.TestCase;

public class TestForKFNextHelper extends TestCase {

	public void testOne() throws IOException {
		CashKyoroFile src = new CashKyoroFile(10, 10);
		CashKyoroFile out = new CashKyoroFile(5, 20);
		byte[] exp = new byte[1024];
		Random r = new Random(777);
		for(int i=0;i<exp.length;i++) {
			exp[i] = (byte)r.nextInt();
		}
		try {
			src.write(exp);
			src.seek(0);
			CashKyoroFileHelper.copy(src, out);
			byte[] result = CashKyoroFileHelper.newBinary(out);
			TestUtil.assertArrayEquals(this, "", exp, result);
		} finally {
			src.close();
			out.close();
		}
	}

	public void testXCopy() throws IOException {
		File f1 = new File("./f1");
		File f2 = new File("./f2");
		CashKyoroFile src1 = new CashKyoroFile(f1, 10, 10);
		CashKyoroFile src2 = new CashKyoroFile(f2, 10, 10);
		CashKyoroFile out = new CashKyoroFile(5, 20);
		byte[] exp = new byte[1024];
		Random r = new Random(777);
		for(int i=0;i<exp.length;i++) {
			exp[i] = (byte)i;
//			exp[i] = (byte)r.nextInt();
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
			CashKyoroFileHelper.xcopy(srcs, out);
			byte[] result = CashKyoroFileHelper.newBinary(out);
			TestUtil.assertArrayEquals(this, "", exp, result);
		} finally {
			src1.close();
			out.close();
		}
	}

	public void testTwo() throws IOException {
		CashKyoroFile src1 = new CashKyoroFile(10, 10);
		CashKyoroFile src2 = new CashKyoroFile(10, 10);
		CashKyoroFile out = new CashKyoroFile(5, 20);
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
			CashKyoroFileHelper.copy(src1, out);
			assertEquals(exp.length/2, src1.length());
			assertEquals(exp.length/2, src2.length());
			out.seek(src1.length());
			CashKyoroFileHelper.copy(src2, out);
			byte[] result = CashKyoroFileHelper.newBinary(out);
			TestUtil.assertArrayEquals(this, "", exp, result);
		} finally {
			src1.close();
			out.close();
		}
	}

	public void testTime() throws IOException {
		
		long start = System.currentTimeMillis();
//		File srcF = new File(".\testdata\1mb\1m_a.txt");
		File srcF = new File("../../h264.mp4");
		CashKyoroFile src = new CashKyoroFile(srcF, 16*1024, 3);
//		RACashFile out = new RACashFile(512, 20);
		
		// 1 4000 3000
		// 2 5000 6000
		// 4 11000 11000
		CashKyoroFile out = new CashKyoroFile(16*1024, 10);
		try {
			src.seek(0);
			CashKyoroFileHelper.copy(src, out);
		} finally {
			src.close();
			out.close();
		}
		long end = System.currentTimeMillis();
		System.out.print("time:"+(end-start));
	}

}
