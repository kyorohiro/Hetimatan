package net.hetimatan.util.bitfield;

import junit.framework.TestCase;

public class TestForBitField extends TestCase {

	BitField newBitField(int bitsize) {
		return new BitField(bitsize);
	}
	
	public void test_bitsizeIsZero() {
		BitField bitfield = newBitField(0);
		assertEquals(0, bitfield.getBinary().length);
		assertEquals(0, bitfield.lengthPerBit());
		assertEquals(0, bitfield.lengthPerByte());
		assertEquals(true, bitfield.isAllOff());
		assertEquals(true, bitfield.isAllOn());
	}

	public void test_bitsizeIsOne() {
		BitField bitfield = newBitField(1);
		assertEquals(1, bitfield.getBinary().length);
		assertEquals(1, bitfield.lengthPerBit());
		assertEquals(1, bitfield.lengthPerByte());
		assertEquals(false, bitfield.isAllOff());
		assertEquals(true, bitfield.isAllOn());
		assertEquals(0x80, 0xFF&bitfield.getBinary()[0]);
	}
	
}
