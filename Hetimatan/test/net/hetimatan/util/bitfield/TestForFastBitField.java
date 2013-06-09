package net.hetimatan.util.bitfield;

public class TestForFastBitField extends TestForBitField {

	BitField newBitField(int bitsize) {
		return new FastBitField(bitsize);
	}

	public void testfast_bitsizeIs72() {
		FastBitField bitfield = (FastBitField)newBitField(71);
		assertEquals(9, bitfield.getBinary().length);
		assertEquals(71, bitfield.lengthPerBit());
		assertEquals(9, bitfield.lengthPerByte());
		assertEquals(false, bitfield.isAllOff());
		assertEquals(true, bitfield.isAllOn());
		assertEquals(0xFF, 0xFF&bitfield.getBinary()[0]);
		assertEquals(0xFE, 0xFF&bitfield.getBinary()[8]);

		BitField index = bitfield.getIndex();
		assertEquals(2, index.lengthPerBit());
		assertEquals(1, index.lengthPerByte());
		assertEquals(true, index.isOn(0));
		assertEquals(true, index.isOn(1));

		bitfield.isOn(0, true);
		bitfield.isOn(70, false);
		bitfield.isOn(69, false);
		bitfield.isOn(68, false);
		bitfield.isOn(67, false);
		bitfield.isOn(66, false);
		bitfield.isOn(65, false);
		assertEquals(true, index.isOn(1));
		bitfield.isOn(64, false);

		assertEquals(true, index.isOn(0));
		assertEquals(false, index.isOn(1));
		
		bitfield.isOn(67, true);
		assertEquals(true, index.isOn(0));
		assertEquals(true, index.isOn(1));

	}

}
