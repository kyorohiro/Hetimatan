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

	private int[] mShuffleList= new int[]{0,1,2,3,4,5,6,7};
	private void shuffle() {
		int tmp1 = 0;
		int tmp2 = 0;
		for(int i=0;i<8;i++) {
			tmp1 = mR.nextInt(8);
			tmp2 = mShuffleList[i];
			mShuffleList[i] = mShuffleList[tmp1];
			mShuffleList[tmp1] = tmp2;
		}
	}

	public int getPieceAtRandom() {
		int index = mIndex.getPieceAtRandom()*8;
		if(index <0) {return -1;}
		shuffle();
		byte[] buffer = getBinary();
		int v = buffer.length-index;
		if(v>8) {v=8;}
		for(int i=0;i<8;i++) {
			if(mShuffleList[i]<v&&(0xFF&buffer[index+mShuffleList[i]]) != 0xFF) {
				return super.getPieceAtRandomPerByte(index+mShuffleList[i]);
			}
		}
		return -1;
	}

	@Override
	public void isOn(int number, boolean on) {
		super.isOn(number, on);
		int superIndexPerByte = number/(8);
		int index = number/(8*8);
		if(!on) {
			mIndex.isOn(index, false);
		} else {
			byte[] buffer = super.getBinary();
			boolean o = false;
			for(int i=0;i<8&&(superIndexPerByte+i)<buffer.length;i++) {
				if((0xFF&buffer[superIndexPerByte+i]) != 0xFF) {
					mIndex.isOn(index, false);
					o=true;
					break;
				}
			}
			if(!o) {
				mIndex.isOn(index, true);
			}
		}
	}

	public BitField getIndex() {
		return mIndex;
	}
}
