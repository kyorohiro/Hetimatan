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

	public static class HtunChangeRequest extends HtunAttribute {
		
		public static HtunChangeRequest decode(MarkableFileReader reader) throws IOException {
			int type = MarkableReaderHelper.readShort(reader, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN);
			int status = reader.read();
			boolean changePort = false;
			boolean changeIp = false;
			if((status&0x01) == 0x01) {
				changePort = true;
			}
			else if((status&0x04) == 0x04) {
				changeIp = true;
			}
			return null;
		}
	}
}

