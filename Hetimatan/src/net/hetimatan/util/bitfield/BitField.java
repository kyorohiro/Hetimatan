package net.hetimatan.util.bitfield;

import net.hetimatan.util.url.PercentEncoder;


public class BitField {
	private byte[] mBitfield = new byte[0];
	private int mBitsize = 0;
	public static final int[] BIT = {0xFF, 0x80, 0xC0, 0xE0, 0xF0, 0xF8, 0xFC, 0xFE};
	
	public static BitField relative(BitField ina, BitField inb, BitField out) {
		if(out == null) {
			int len = ina.lengthPerBit();
			out = new BitField(len);
		}
		int len = out.lengthPerByte();
		if(len>inb.lengthPerByte()) {
			len = inb.lengthPerByte();
		}
		for(int i=0;i<out.lengthPerByte();i++) {
			out.mBitfield[i] = (byte)(0xFF&out.mBitfield[i]);
		}
		for(int i=0;i<len;i++) {
			out.mBitfield[i] = (byte)(0xFF&ina.mBitfield[i]&(~inb.mBitfield[i]));
		}
		return out;
	}

	public BitField(int bitsize) {
		mBitsize = bitsize;
		int byteSize = bitsize/8;
		if((bitsize%8)!=0) {
			byteSize+=1;
		}
		mBitfield = new byte[byteSize];
		oneClear();
	}

	public void oneClear() {
		int bitsize = mBitsize;
		int byteSize = bitsize/8;
		if((bitsize%8)!=0) {byteSize+=1;}
		for(int i=0;i<mBitfield.length;i++) {
			mBitfield[i] = (byte)0xFF;
		}
		if(mBitfield.length!=0) {
			mBitfield[byteSize-1]= (byte)(BIT[bitsize%8]&0xFF);
		}
	}

	public boolean isAllOff() {
		int len =lengthPerBit();
		for(int i=0;i<len;i++) {
			if(isOn(i)) {
				return false;
			}
		}
		return true;
	}

	public boolean isAllOn() {
		int len =lengthPerBit();
		for(int i=0;i<len;i++) {
			if(!isOn(i)) {
				return false;
			}
		}
		return true;
	}

	public void zeroClear() {
		for(int i=0;i<mBitfield.length;i++) {
			mBitfield[i] = 0;
		}
	}

	public int lengthPerBit() {
		return mBitsize;
	}

	public int lengthPerByte() {
		return mBitfield.length;
	}

	public byte[] getBinary() {
		return mBitfield;
	}

	public void setBitfield(byte[] bitfield) {
		int length = bitfield.length;
		if(length > mBitfield.length) {
			length = mBitfield.length;
		}
		System.arraycopy(bitfield, 0, mBitfield, 0, length);
	}

	public void isOn(int number, boolean on) {
		int chunk = number/8;
		int pos = number%8;
		// 8 0, 7 1, 3 3 7 7 
		if(mBitfield == null || chunk>=mBitfield.length) {
			return;
		}

		int value = 0x01<<(7-pos);
		int v = mBitfield[chunk];
		if(on) {
			mBitfield[chunk] = (byte)(v|value);
		} else {
			value = value^0xFFFFFFFF;
			mBitfield[chunk] = (byte)(v&value);
		}
	}

	public boolean isOn(int number) {
		int chunk = number/8;
		int pos = number%8;
		// 8 0, 7 1, 3 3 7 7 
		if(mBitfield == null || chunk>=mBitfield.length) {
			return false;
		}
		if(((mBitfield[chunk]>>(7-pos))&0x01) == 0x01 ) {
			return true;
		} else {
			return false;
		}
	}

	public String toURLString() {
		PercentEncoder en = new PercentEncoder();
		return ""+mBitfield.length+":"+en.encode(mBitfield)+"e";
	}
}
