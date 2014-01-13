package net.hetimatan.net.stun.message;

import java.io.IOException;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.file.MarkableReaderHelper;
import net.hetimatan.util.io.ByteArrayBuilder;

public class HtunAttribute {

	public static HtunAttribute decode(MarkableFileReader reader) throws IOException {
		int type = MarkableReaderHelper.readShort(reader, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN);
		
		return null;
	}
}
