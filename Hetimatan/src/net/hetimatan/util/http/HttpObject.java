package net.hetimatan.util.http;


import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.io.filen.RACashFile;
import net.hetimatan.util.io.ByteArrayBuilder;

public abstract class HttpObject {

	private int mType = 0;
	public static final String SP = " ";
	public static final String CRLF = "\r\n";

	public static final int TYPE_STRI  = 0;
	public static final int TYPE_INTE  = 1;
	public static final int TYPE_LIST  = 2;
	public static final int TYPE_DICT  = 3;

	private static RACashFile vFile = null;
	public static synchronized String createEncode(HttpObject target) throws IOException {
		try {
			vFile = new RACashFile(512, 2);
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

	public int getType() {
		return mType;
	}


	public byte[] toByte() {
		return new byte[0];
	}

	
	public int size() {
		return 0;
	}

	protected static void _value(MarkableReader reader, byte value) throws IOException {
		try {
			reader.pushMark();
			int datam = reader.read();
			if(datam<0||datam!=value) {
				reader.backToMark();
				throw new IOException();
			}
		} finally {
			reader.popMark();
		}
	}
	protected static String _value(MarkableReader reader, byte[] fin, boolean EOFisFin) throws IOException {
		return _value(reader, fin, null, EOFisFin);
	}

	protected static String _value(MarkableReader reader, byte[] fin1, byte fin2[], boolean EOFisFin) throws IOException {
		try {
			reader.pushMark();
			int datam = 0;
			ByteArrayBuilder builder = new ByteArrayBuilder();
			do {
				datam = reader.peek();
				if(datam < 0) {
					if(EOFisFin) {
						break;
					} else {
						reader.backToMark();
						throw new IOException("##"+datam+"("+((char)datam)+")##"+reader.getFilePointer()+"#");
					}
				} else if(datam == fin1[0]) {
					boolean pass = true;
					try {
						reader.pushMark();
						if(fin1.length > 1) {
							for(int i=0;i<fin1.length;i++) {
								if(fin1[i] != (byte)(0xFF&reader.read())){
									pass = false;
									break;
								}
							}
						}
					} finally {
						reader.backToMark();
						reader.popMark();
					}
					if(pass) {
						break;
					}
				} else if(fin2 != null&&datam == fin2[0]) {
						break;
				}
				
				builder.append((byte)(0xFF&datam));
				reader.read();
			} while(true);
			return new String(builder.getBuffer(), 0, builder.length());
		} finally {
			reader.popMark();
		}
	}

	public static void _crlf(MarkableReader reader) throws IOException {
		int cr = reader.read();
		if(cr == '\n') {
			return;
		}
		int lf = reader.read();
		if(cr == '\r' && lf == '\n') {			
		} else {
			throw new IOException("#"+cr+","+lf+"#");
		}
	}

	public static void _sp(MarkableReader reader) throws IOException {
		//int sp = 
		reader.read();
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

	//HTTP-Version   = "HTTP" "/" 1*DIGIT "." 1*DIGIT
	//public static void _httpversion(MarkableReader reader) throws IOException {
	//	
	//}
	//

	//
	// benobject    : beninteger | benstring | beniction | benlist
	//
	//public static BenObject decodeValue(MarkableReader input) throws IOException {

	public abstract void encode(OutputStream output) throws IOException;

}
