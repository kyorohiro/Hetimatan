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
		
		assertEquals(true, bitfield.isOn(0));
		bitfield.isOn(0, false);
		assertEquals(false, bitfield.isOn(0));
		bitfield.oneClear();
		assertEquals(true, bitfield.isOn(0));
		assertEquals(0x80, 0xFF&bitfield.getBinary()[0]);

		bitfield.zeroClear();
		assertEquals(false, bitfield.isOn(0));
		assertEquals(0x00, 0xFF&bitfield.getBinary()[0]);
	}

	public void test_bitsizeIsNine() {
		BitField bitfield = newBitField(9);
		assertEquals(2, bitfield.getBinary().length);
		assertEquals(9, bitfield.lengthPerBit());
		assertEquals(2, bitfield.lengthPerByte());
		assertEquals(false, bitfield.isAllOff());
		assertEquals(true, bitfield.isAllOn());
		assertEquals(0xFF, 0xFF&bitfield.getBinary()[0]);
		assertEquals(0x80, 0xFF&bitfield.getBinary()[1]);

		for(int i=0;i<bitfield.lengthPerBit();i++)
		{ assertEquals(true, bitfield.isOn(i));}

		bitfield.isOn(1, false);
		assertEquals(false, bitfield.isOn(1));

		bitfield.isOn(8, false);
		assertEquals(false, bitfield.isOn(8));

		assertEquals(false, bitfield.isAllOff());
		assertEquals(false, bitfield.isAllOn());

		bitfield.oneClear();
		assertEquals(false, bitfield.isAllOff());
		assertEquals(true, bitfield.isAllOn());
		
		bitfield.zeroClear();
		assertEquals(true, bitfield.isAllOff());
		assertEquals(false, bitfield.isAllOn());

	}


	public void test_bitsizeIs72() {
		BitField bitfield = newBitField(70);
		assertEquals(9, bitfield.getBinary().length);
		assertEquals(70, bitfield.lengthPerBit());
		assertEquals(9, bitfield.lengthPerByte());
		assertEquals(false, bitfield.isAllOff());
		assertEquals(true, bitfield.isAllOn());
		assertEquals(0xFF, 0xFF&bitfield.getBinary()[0]);
		assertEquals(0xFC, 0xFF&bitfield.getBinary()[8]);
	}

}
