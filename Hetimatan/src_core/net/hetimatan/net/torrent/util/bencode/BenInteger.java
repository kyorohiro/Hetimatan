package net.hetimatan.net.torrent.util.bencode;


import java.io.IOException;
import java.io.OutputStream;

import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.util.io.ByteArrayBuilder;

public class BenInteger extends BenObject {

	private int mValue = 0;

	public BenInteger(int value) {
		super(TYPE_INTE);
		mValue = value;
	}

	public int toInteger() {
		return mValue;
	}

	public String toString() {
		return ""+mValue;
	}

	public byte[] toByte() {
		return toString().getBytes();
	}

	@Override
	public void encode(OutputStream output) throws IOException {
		output.write('i');
		output.write(this.toString().getBytes("utf8"));
		output.write('e');		
	}

	//
	// beninteger   : "i" [0-9]* "e"
	//
	public static BenInteger decodeInteger(MarkableReader input) throws IOException {
		log("decodeInteger:"+input.getFilePointer()+",");
		try {
			input.pushMark();

			if (!checkHead(input, (byte)'i')) {
				throw new IOException("");
			} else {
				input.read();
			}

			ByteArrayBuilder builder = new ByteArrayBuilder();
			do {
				if (input.peek() == 'e') {
					input.read();
					break;					
				} else if (-1 == input.peek()) {
					input.backToMark();
					throw new IOException();
				} 
				byte b = (byte)(0xFF&input.read());
				builder.append(b);
			} while(true);
			String integerAsString = new String(builder.getBuffer(), 0, builder.length());
			return new BenInteger(Integer.parseInt(integerAsString));
		} catch(NumberFormatException e) {
			input.backToMark();
			throw new IOException();			
		} finally {
			input.popMark();
		}
	}
}
