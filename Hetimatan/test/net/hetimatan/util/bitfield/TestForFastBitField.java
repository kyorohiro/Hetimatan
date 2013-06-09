package net.hetimatan.util.bitfield;

public class TestForFastBitField extends TestForBitField {

	BitField newBitField(int bitsize) {
		return new FastBitField(bitsize);
	}
}
