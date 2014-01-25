package net.hetimatan.util.io.net;

import java.io.IOException;
import java.net.UnknownHostException;

import net.hetimatan.io.net.KyoroDatagramMock;
import net.hetimatan.io.net.KyoroSelector;
import net.hetimatan.util.event.net.KyoroSocketEventRunner;
import net.hetimatan.util.http.HttpObject;
import junit.framework.TestCase;
import net.hetimatan.util.test.*;
public class TestForMockUDP extends TestCase {

	public void testSendReceive() throws UnknownHostException, IOException {
		KyoroDatagramMock d1 = new KyoroDatagramMock();
		KyoroDatagramMock d2 = new KyoroDatagramMock();
		try {
			d1.bind(HttpObject.address("127.0.0.1", 800));
			d2.bind(HttpObject.address("127.0.0.2", 801));

			d1.send("abc".getBytes(), HttpObject.address("127.0.0.2", 801));
			byte[] receiveAddress = d2.receive();
			byte[] receiveData = d2.getByte();
			{
				TestUtil.assertArrayEquals(this, "..", "abc".getBytes(), receiveData);
				TestUtil.assertArrayEquals(this, "..", HttpObject.address("127.0.0.1", 800), receiveAddress);
			}
		} finally {
			d1.close();
			d2.close();
		}
	}

	public void testSelector() throws UnknownHostException, IOException {
		KyoroSelector selector = new KyoroSelector();
		//		KyoroSocketEventRunner runner = new KyoroSocketEventRunner();
		KyoroDatagramMock d1 = new KyoroDatagramMock();
		KyoroDatagramMock d2 = new KyoroDatagramMock();
		try {
			d1.bind(HttpObject.address("127.0.0.1", 800));
			d2.bind(HttpObject.address("127.0.0.2", 801));

			d1.regist(selector, KyoroSelector.READ);
			selector.select(0);
			assertEquals(false, selector.next());
		} finally {
			d1.close();
			d2.close();
		}
	}
	
}
