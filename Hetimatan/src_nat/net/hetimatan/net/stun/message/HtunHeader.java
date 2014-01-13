package net.hetimatan.net.stun.message;

import java.io.IOException;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.file.MarkableReaderHelper;
import net.hetimatan.util.io.ByteArrayBuilder;

public class HtunHeader {
	public static final int BINDING_REQUEST = 0x01;
	public static final int BINDING_RESPONSE = 0x101;
	public static final int BINDING_ERROR_RESPONSE = 0x111;

	public static HtunHeader decode(MarkableFileReader reader) throws IOException {
		// 2byte zero sign.  
		int sign = MarkableReaderHelper.readShort(reader, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN);
		if(sign != 0) {throw new IOException("bad sign "+sign);}

		// 2byte message type
		int messageType = MarkableReaderHelper.readShort(reader, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN);
		
		// 2byte message length
		int messageLength = MarkableReaderHelper.readShort(reader, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN);
		if(messageLength<0) {
			throw new IOException("bad messageLength len="+messageLength );			
		}
		// data
		byte[] content = new byte[messageLength];
		int readed = reader.read(content);
		if(messageLength != readed) {
			throw new IOException("messageLength exp="+messageLength+",ret="+readed );
		}
		return null;
	}

}
