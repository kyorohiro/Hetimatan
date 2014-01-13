package net.hetimatan.net.stun.message;

import java.io.IOException;
import java.io.OutputStream;

import com.sun.org.apache.bcel.internal.generic.LLOAD;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.file.MarkableReaderHelper;
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
		
	}

	public static HtunAttribute decode(MarkableFileReader reader) throws IOException {
		int type = MarkableReaderHelper.readShort(reader, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN);
		int length = MarkableReaderHelper.readShort(reader, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN);
		
		return null;
	}

	public static class HtunChangeRequest extends HtunAttribute {
		
		public static final int STATUS_CHANGE_PORT = 0x02;
		public static final int STATUS_CHANGE_IP = 0x04;
		public static final int STATUS_CHANGE_IP_PORT = 0x06;
	
		private int mStatus = 0;

		public HtunChangeRequest(int status) {
			mStatus = status;
		}

		public boolean changeIp() {
			if((mStatus&STATUS_CHANGE_IP) == STATUS_CHANGE_IP) {
				return true;
			} else {
				return false;
			}
		}

		public boolean chagePort() {
			if((mStatus&STATUS_CHANGE_PORT) == STATUS_CHANGE_PORT) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		public void encode(OutputStream output) throws IOException {
			// 2
			output.write(ByteArrayBuilder.parseShort(
					HtunAttribute.CHANGE_RESUQEST, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN));
			// 4
			output.write(ByteArrayBuilder.parseShort(
					4, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN));
			// 4
			output.write(ByteArrayBuilder.parseInt(
					mStatus, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN));
			
		}

		public static HtunChangeRequest decode(MarkableFileReader reader) throws IOException {
			int type = MarkableReaderHelper.readShort(reader, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN);
			if(type != HtunAttribute.CHANGE_RESUQEST) {
				throw new IOException("bad type =" + type);
			}
			int length = MarkableReaderHelper.readShort(reader, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN);
			if(length != 4) {
				throw new IOException("bad length =" + length);
			}
			int status = MarkableReaderHelper.readInt(reader, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN);
			return new HtunChangeRequest(status);
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

