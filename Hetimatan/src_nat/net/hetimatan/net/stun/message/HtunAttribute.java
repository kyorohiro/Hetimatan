package net.hetimatan.net.stun.message;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.org.apache.bcel.internal.generic.LLOAD;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.file.MarkableReaderHelper;
import net.hetimatan.net.stun.HtunServer;
import net.hetimatan.net.stun.message.attribute.HtunChangeRequest;
import net.hetimatan.net.stun.message.attribute.HtunXxxAddress;
import net.hetimatan.util.io.ByteArrayBuilder;

public class HtunAttribute {

	public static final int MAPPED_ADDRESS = 0x01;
	public static final int RESPONSE_ADDRESS = 0x02;
	public static final int CHANGE_RESUQEST = 0x03;
	public static final int SOURCE_ADDRESS = 0x04;
	public static final int CHANGE_ADDRESS = 0x05;
	public static final int USERNAME = 0x06;
	public static final int MESSAGE_INTEGRITY = 0x07;
	public static final int ERROR_CODE = 0x08;
	public static final int UNKNOWN_ATTRIBUTE = 0x0a;
	public static final int REFLECTED_FROM = 0x0b;

	public void encode(OutputStream output) throws IOException {
		throw new IOException();
	}

	public static HtunAttribute decode(MarkableFileReader reader) throws IOException {
		try {
			reader.pushMark();
			int type = MarkableReaderHelper.readShort(reader, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN);
			reader.backToMark();
			switch(type) {
			case HtunAttribute.CHANGE_RESUQEST:
				return HtunChangeRequest.decode(reader);
			case HtunAttribute.SOURCE_ADDRESS:
			case HtunAttribute.CHANGE_ADDRESS:
			case HtunAttribute.MAPPED_ADDRESS:
				return HtunXxxAddress.decode(reader);
			default:
				throw new IOException();
			}
		} catch(IOException e) {
			reader.backToMark();
			throw e;
		} finally {
			reader.popMark();
		}
	}


	public static class HtunMappedAddress extends HtunAttribute {
		public static HtunMappedAddress decode(MarkableFileReader reader) throws IOException {
			// 2byte type
			int type = MarkableReaderHelper.readInt(reader, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN);
			// 2byte length 
			int length = MarkableReaderHelper.readInt(reader, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN);
			// 1byte family
			int family = reader.read();
			// 2byte port
			int port = MarkableReaderHelper.readShort(reader, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN);
			// 4 byte ip
			byte[] ip = new byte[4];
			ip[0] = (byte)(0xFF&reader.read());
			ip[1] = (byte)(0xFF&reader.read());
			ip[2] = (byte)(0xFF&reader.read());
			ip[3] = (byte)(0xFF&reader.read());
			return null;
		}
	}

}

