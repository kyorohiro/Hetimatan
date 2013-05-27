package net.hetimatan.io.filen;

import java.io.File;
import java.io.IOException;

import net.hetimatan.io.file.KyoroFile;
import net.hetimatan.util.event.GlobalAccessProperty;


public class KFNextHelper {

	public final static String GATAG_DEFAULTCASH = "GATAG_DEFAULTCASH";
	private static int sID = 0;

	public static File newCashFile() {
		boolean deleted = true;
		File base = null;
		do {
			sID++;
			GlobalAccessProperty prop = GlobalAccessProperty.getInstance();
			String pathAsString = prop.get(GATAG_DEFAULTCASH, ".");
			File dir = new File(pathAsString);
			base = new File(dir, "virtualFile_"+sID);
			if(base.exists()) {
				deleted = base.delete();
			} else {
				deleted = true;
			}
		}while(!deleted);
		return base;
	}

	public static byte[] newBinary(KyoroFile src) throws IOException {
		int len = (int)src.length();
		byte[] buffer = new byte[len];
		long fp = src.getFilePointer();
		try {
			src.seek(0);
			src.read(buffer);
		} finally {
			src.seek(fp);
		}
		return buffer;
	}

	public static void xcopy(File[] srcs, RACashFile dest) throws IOException {
		for(int i=0;i<srcs.length;i++) {
			RACashFile src = new RACashFile(srcs[i], 256, 10);
			long fp = dest.getFilePointer();
			try {
				KFNextHelper.copy(src, dest);
				dest.syncWrite();
			} finally {
				dest.seek(fp+src.length());
				src.close();
			}
		}
	}

//	public static void copy(RACashFile src, RACashFile  dest) throws IOException {
	public static void copy(KyoroFile src, RACashFile  dest) throws IOException {
		byte[] buffer = new byte[512];
		int len=0;
		long srcFP = src.getFilePointer();
		long destFP = dest.getFilePointer();

		try {
			do {
				len = buffer.length;
				len = src.read(buffer);
				if(len>0) {
					dest.write(buffer, 0, len);
				}
			} while(len>0);
		} finally {
			src.seek(srcFP);
			dest.seek(destFP);
		}
	}

	public static KyoroFile subSequence(RACashFile file, long start, long end) throws IOException {
		return new AccessorFile(file, start, end, 256, 3);
	}

}