package net.hetimatan.net.stun.message;

import junit.framework.TestCase;

public class TestForHtunHeader extends TestCase {
	public void testCreate() {
		byte[] id = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};
		HtunHeader header = new HtunHeader(HtunHeader.BINDING_REQUEST, id);
	}
}
