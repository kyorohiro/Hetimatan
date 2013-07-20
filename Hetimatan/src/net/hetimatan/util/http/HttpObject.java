package net.hetimatan.util.http;


import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.io.file.MarkableReaderHelper;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.util.io.ByteArrayBuilder;

public abstract class HttpObject {

	private int mType = 0;
	public static final String SP = " ";
	public static final String CRLF = "\r\n";

	public static final int TYPE_STRI  = 0;
	public static final int TYPE_INTE  = 1;
	public static final int TYPE_LIST  = 2;
	public static final int TYPE_DICT  = 3;

	private static CashKyoroFile vFile = null;
	public static synchronized String createEncode(HttpObject target) throws IOException {
		try {
			vFile = new CashKyoroFile(512, 2);
			target.encode(vFile.getLastOutput());
			byte[] buffer = new byte[(int)vFile.length()];
			//vFile.seek(0);//todo
			int len = vFile.read(buffer);
			return new String(buffer, 0, len);
		} finally {
			vFile.close();
		}
	}
	public static void log(CharSequence log) {
	//	System.out.println("bencode_log:"+log);
	}

	public static void _crlf(MarkableReader reader) throws IOException {
		try {
			MarkableReaderHelper.match(reader, "\r\n".getBytes());
			return;
		} catch(IOException e) {
		}
		MarkableReaderHelper.match(reader, "\n".getBytes());
	}

	public static void _sp(MarkableReader reader) throws IOException {
		MarkableReaderHelper.match(reader, " ".getBytes());
		MarkableReaderHelper.jumpPattern(reader, " ".getBytes(), 256);
	}

	public static String parseString(String value, String basic) {
		if (value != null) {
			return value;
		} else {
			return basic;
		}
	}

	public static int parseInt(String value, int basic) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return basic;
		}
	}

	public static byte[] aton(String host) throws UnknownHostException {
//		System.out.println("Xxx:"+host);
		InetAddress address = InetAddress.getByName(host);
		byte[] byteAddress = address.getAddress();
		return byteAddress;
	}
	
	public static String ntoa(int raw) {
	    byte[] b = new byte[] {(byte)(raw >> 24), (byte)(raw >> 16), (byte)(raw >> 8), (byte)raw};
	    try {
	        return InetAddress.getByAddress(b).getHostAddress();
	    } catch (UnknownHostException e) {
	        //No way here
	        return null;
	    }
	}

	public static String ntoa(byte[] b) {
	    try {
	        return InetAddress.getByAddress(b).getHostAddress();
	    } catch (UnknownHostException e) {
	        //No way here
	        return null;
	    }
	}

	public static byte[] portToB(int port) {
		return ByteArrayBuilder
				.parseShort(port, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN);
	}

	public static int bToPort(byte[] port) {
		return ByteArrayBuilder
				.parseShort(port, 0, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN);
	}

	public int getType() {
		return mType;
	}

	public byte[] toByte() {
		return new byte[0];
	}

	public int size() {
		return 0;
	}

	public abstract void encode(OutputStream output) throws IOException;

}
