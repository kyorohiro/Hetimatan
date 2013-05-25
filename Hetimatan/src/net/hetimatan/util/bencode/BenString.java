package net.hetimatan.util.bencode;


import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import net.hetimatan.ky.io.MarkableReader;
import net.hetimatan.util.io.ByteArrayBuilder;
import net.hetimatan.util.url.PercentEncoder;

public class BenString extends BenObject {

	private byte[] mValue = null;
	private String mCharset = "utf8";

	public BenString(byte[] buffer, int begin, int end, String charset) {
		super(TYPE_STRI);
		mValue = new byte[end-begin];
		System.arraycopy(buffer, begin, mValue, 0, end-begin);
//		if(end-begin>=6){
//			System.out.println("XxxX#:"+mValue[4]+"-"+mValue[5]+"-----------------------");
//		}
	}

	public BenString(String value) {
		super(TYPE_STRI);
		try {
			mValue = value.getBytes("utf8");
		} catch (UnsupportedEncodingException e) {
			mValue = value.getBytes();
		}
	}

	public String toString() {
		try {
			return new String(mValue, mCharset);
		} catch (UnsupportedEncodingException e) {
			return new String(mValue);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof BenString) {
			BenString benobj = (BenString)obj;
			return Arrays.equals(toByte(), benobj.toByte());
		} else {
			return super.equals(obj);
		}
	}

	public int byteLength() {
		return mValue.length;
	}

	@Override
	public byte[] toByte() {
		return mValue;
	}

	// for test
	public String toPercentString() {
		PercentEncoder encoder = new PercentEncoder();
		return encoder.encode(mValue);
	}

	@Override
	public void encode(OutputStream output) throws IOException {
		output.write((""+mValue.length).getBytes());
		output.write(":".getBytes());
		output.write(mValue);
//		if(mValue.length>=6){
//			System.out.println("XxxN#:"+mValue.length+","+mValue[4]+"-"+mValue[5]+"-----------------------");
//		}
	}

	//
	// benstring    : [0-9]* ":" <bytes array/string> 
	// # bytes array/string length is prev [0-9]*.
	//
	public static BenString decodeString(MarkableReader input) throws IOException {
		log("decodeString:"+input.getFilePointer()+",");
		try {
			input.pushMark();

			if(!checkHeadAtClosedInterval(input, (byte)'0', (byte)'9')) {
				throw new IOException("");
			}

			ByteArrayBuilder builder = new ByteArrayBuilder();
			// length
			do {
				byte v = (byte)(0xFF&input.read());
				if(v == -1) {
					input.backToMark();
					throw new IOException();
				} else if(v == ':') {
					break;
				} else {
					builder.append(v);
				}
			} while(true);
			String lenAsString = new String(builder.getBuffer(), 0, builder.length());
			int len = Integer.parseInt(lenAsString);

			// text
			builder.clear();
			for(int i=0;i<len;i++) {
				if(input.peek()<0) {
					throw new IOException();
				}
				builder.append((byte)(0xFF&input.read()));
			}
			return new BenString(builder.getBuffer(), 0, (int)len, "utf8");
		} catch(NumberFormatException e) {
			input.backToMark();
			throw new IOException();
		} finally {
			input.popMark();
		}
	}

}
