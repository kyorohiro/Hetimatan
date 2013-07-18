package net.hetimatan.io.file;


import java.io.IOException;

import net.hetimatan.util.io.ByteArrayBuilder;

public class MarkableReaderHelper {


	public static byte[] jumpAndGet(MarkableReader reader, byte[] availabe, int limit) throws IOException {
		long start = reader.getFilePointer();
		jumpPattern(reader, availabe, limit);
		long end = reader.getFilePointer();
		if(start == end) {
			return new byte[0];
		} else {
			byte[] ret = new byte[(int)(end-start)];
			reader.seek(start);
			reader.read(ret, 0, ret.length);
			return ret;
		}
	}

	public static void jumpPattern(MarkableReader reader, byte[] availabe, int limit) throws IOException {
		int v = 0;
		reader.pushMark();
		int tmp = 0;
		try {
			do {
				v = reader.read();
				if (v<0) {
					return;
				}
				boolean update = false;
				for(byte b: availabe) {
					if(v == (b&0xff)) {
						update = true;
						break;
					}
				}
				if(!update) {
					return;
				}
				tmp++;
			} while(tmp<limit);
		} finally {
			reader.popMark();
		}
	}
	public static void match(MarkableReader reader, byte[] source) throws IOException {
		int v = 0;
		reader.pushMark();
		int tmp = 0;
		try {
			do {
				v = reader.read();
				if (v<0) {
					reader.backToMark();
					throw new IOException();
				}
				if(v != (source[tmp]&0xff)) {
					reader.backToMark();
					throw new IOException();          
				}
				tmp++;
			} while(tmp<source.length);
		} finally {
			reader.popMark();
		}
	}

	//ByteArrayBuilder.BYTEORDER_BIG_ENDIAN
	public static int readInt(MarkableReader reader, int order) throws IOException {
		byte[] buffer = readBuffer(reader, 4);
		return ByteArrayBuilder.parseInt(buffer, order);
	}

	public static byte[] readBuffer(MarkableReader reader, int length) throws IOException {
		try {
			byte[] hashId = new byte[length];
			reader.pushMark();
			int len = reader.read(hashId, 0, hashId.length);
			if(len!=length) {
				throw new IOException();
			}
			return hashId;
		} catch(IOException e) {
			reader.backToMark();
			throw e;
		} finally {
			reader.popMark();
		}
	}
}
