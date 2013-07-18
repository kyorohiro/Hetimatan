package net.hetimatan.io.file;


import java.io.IOException;

import net.hetimatan.util.io.ByteArrayBuilder;

public class MarkableReaderHelper {

	
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
