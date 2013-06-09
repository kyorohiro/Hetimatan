package net.hetimatan.util.bitfield;

public class FastBitField extends BitField {

	private BitField mIndex = null;
	public FastBitField(int bitsize) {
		super(bitsize);
		mIndex = new BitField(bitsize/(8*8));
	}

	@Override
	public boolean isAllOff() {
		return mIndex.isAllOff();
	}

	@Override
	public boolean isAllOn() {
		return mIndex.isAllOn();
	}

	@Override
	public void oneClear() {
		super.oneClear();
		mIndex.oneClear();
	}

	@Override
	public void zeroClear() {
		super.zeroClear();
		mIndex.zeroClear();
	}

	@Override
	public void setBitfield(byte[] bitfield) {
		super.setBitfield(bitfield);
		
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
			if(buffer[superIndexPerByte] != 0) {
				mIndex.isOn(index, true);
			} else {
				mIndex.isOn(index, false);
			}
		}
	}

}
