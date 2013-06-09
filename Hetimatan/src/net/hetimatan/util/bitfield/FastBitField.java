package net.hetimatan.util.bitfield;

import java.util.Random;

public class FastBitField extends BitField {

	private BitField mIndex = null;
	private Random mR = null;
	public FastBitField(int bitsize) {
		super(bitsize);
		mR = new Random(System.currentTimeMillis());
		int indexBitsize = bitsize/(8*8);
		if(bitsize!=0) {
			indexBitsize+=1;
		}
		mIndex = new BitField(indexBitsize);
	}


	@Deprecated
	public int getPieceAtRandom() {
		int bitLength  = mIndex.lengthPerBit();
		int inedx = mR.nextInt(bitLength);
		
		return 0;
	}

	@Override
	public boolean isAllOff() {
		return super.isAllOff();
	}

	@Override
	public boolean isAllOn() {
		return mIndex.isAllOn();
	}

	@Override
	public void oneClear() {
		super.oneClear();
		if(mIndex != null) {
			mIndex.oneClear();
		}
	}

	@Override
	public void zeroClear() {
		super.zeroClear();
		mIndex.zeroClear();
	}

	@Override
	public void setBitfield(byte[] bitfield) {
		super.setBitfield(bitfield);
		for(int number=0;number<bitfield.length;number+=8) {
			int superIndexPerByte = number/(8);
			int index = number/(8*8);
			boolean o = false;
			for(int i=0;i<8&&(superIndexPerByte+i)<bitfield.length;i++) {
				if(bitfield[superIndexPerByte+i] != 0) {
					mIndex.isOn(index, true);
					o=true;
					break;
				}
			}
			if(o) {
				mIndex.isOn(index, true);
			} else {
				mIndex.isOn(index, false);				
			}
		}
	}

	@Override
	public void isOn(int number, boolean on) {
		super.isOn(number, on);
		int superIndexPerByte = number/(8);
		int index = number/(8*8);
		if(on) {
			mIndex.isOn(index, on);
		} else {
			byte[] buffer = super.getBinary();
			boolean o = false;
			for(int i=0;i<8&&(superIndexPerByte+i)<buffer.length;i++) {
				if(buffer[superIndexPerByte+i] != 0) {
					mIndex.isOn(index, true);
					o=true;
					break;
				}
			}
			if(!o) {
				mIndex.isOn(index, false);
			}
		}
	}

	public BitField getIndex() {
		return mIndex;
	}
}
