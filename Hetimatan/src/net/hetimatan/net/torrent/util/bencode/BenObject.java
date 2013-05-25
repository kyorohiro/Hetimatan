package net.hetimatan.net.torrent.util.bencode;


import java.io.IOException;
import java.io.OutputStream;

import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.io.filen.RACashFile;


public abstract class BenObject {

	private int mType = 0;
	public static final int TYPE_STRI  = 0;
	public static final int TYPE_INTE  = 1;
	public static final int TYPE_LIST  = 2;
	public static final int TYPE_DICT  = 3;

	public static void log(CharSequence log) {
//		System.out.println("bencode_log:"+log);
	}

	private static RACashFile vFile = null;
	public static synchronized byte[] createEncode(BenObject target) throws IOException {
		try {
			vFile = new RACashFile(512, 2);
			target.encode(vFile.getLastOutput());
			byte[] buffer = new byte[(int)vFile.length()];
			//int len = 
			vFile.read(buffer);
			return buffer;
		} finally {
			vFile.close();
		}
	}


	public BenObject(int type) {
		mType = type;
	}

	public int getType() {
		return mType;
	}

	public int toInteger() {
		return 0;
	}

	public String toString() {
		return "";
	}

	public byte[] toByte() {
		return new byte[0];
	}

	public BenObject getBenValue(int location) {
		return null;
	}

	public BenObject getBenValue(String value) {
		return null;
	}

	public int size() {
		return 0;
	}

	public abstract void encode(OutputStream output) throws IOException;


	protected static boolean checkHead(MarkableReader input, byte compare)  throws IOException {
		byte h = 0;
		h = (byte)(0xFF&(int)input.peek());
		log(""+h+"("+((char)h)+")");
		if(h != compare) {
			return false;
		} else {
			return true;
		}
	}

	protected static boolean checkHeadAtClosedInterval(MarkableReader input, byte begin, byte end)  throws IOException {
		byte h = 0;
		h = (byte)(0xFF&(int)input.peek());
		if(begin <=h && h<=end) {
			return true;
		} else {
			return false;
		}
	}

	//
	// benobject    : beninteger | benstring | beniction | benlist
	//
	public static BenObject decodeValue(MarkableReader input) throws IOException {
		log("decodeValue:"+input.getFilePointer()+",");
		try {
			return BenInteger.decodeInteger(input);
		} catch(IOException e) {}
		try {
			return BenString.decodeString(input);
		} catch(IOException e) {}
		try {
			return BenList.decodeList(input);
		} catch(IOException e) {}
		try {
			return BenDiction.decodeDiction(input);
		} catch(IOException e) {throw e;}
	}

	public static int parseInt(BenObject object, int def) {
		if(object == null) {
			return def;
		}
		return object.toInteger();
	}

	public static String parseString(BenObject object, String def) {
		if(object == null) {
			return def;
		}
		return object.toString();
	}

}
