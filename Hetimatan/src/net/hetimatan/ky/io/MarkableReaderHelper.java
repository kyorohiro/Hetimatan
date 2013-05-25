package net.hetimatan.ky.io;


import java.io.IOException;

import net.hetimatan.util.io.ByteArrayBuilder;

public class MarkableReaderHelper {

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
