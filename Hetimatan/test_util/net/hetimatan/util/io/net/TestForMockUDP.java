package net.hetimatan.util.io.net;

import java.io.IOException;
import java.net.UnknownHostException;

import net.hetimatan.io.net.KyoroDatagramMock;
import net.hetimatan.io.net.KyoroSelector;
import net.hetimatan.util.http.HttpObject;
import junit.framework.TestCase;
import net.hetimatan.util.test.*;
public class TestForMockUDP extends TestCase {

	public void testSendReceive() throws UnknownHostException, IOException {
		KyoroDatagramMock d1 = new KyoroDatagramMock(KyoroDatagramMock.NIC_TYPE_FULL_CONE);
		KyoroDatagramMock d2 = new KyoroDatagramMock(KyoroDatagramMock.NIC_TYPE_FULL_CONE);
		try {
			d1.bind(HttpObject.address("127.0.0.1", 800));
			d2.bind(HttpObject.address("127.0.0.2", 801));

			d1.send("abc".getBytes(), d2.getMappedIp());
			byte[] receiveAddress = d2.receive();
			byte[] receiveData = d2.getByte();
			{
				TestUtil.assertArrayEquals(this, "..", "abc".getBytes(), receiveData);
				TestUtil.assertArrayEquals(this, "..", d1.getMappedIp(), receiveAddress);
			}
		} finally {
			d1.close();
			d2.close();
		}
	}

	public void testSelector() throws UnknownHostException, IOException {
		KyoroSelector selector = new KyoroSelector();
		//		KyoroSocketEventRunner runner = new KyoroSocketEventRunner();
		KyoroDatagramMock d1 = new KyoroDatagramMock(KyoroDatagramMock.NIC_TYPE_FULL_CONE);
		KyoroDatagramMock d2 = new KyoroDatagramMock(KyoroDatagramMock.NIC_TYPE_FULL_CONE);
		try {
			d1.bind(HttpObject.address("127.0.0.1", 800));
			d2.bind(HttpObject.address("127.0.0.2", 801));

			d1.regist(selector, KyoroSelector.READ);
			selector.select(0);
			assertEquals(false, selector.next());
			
			d2.send("abc".getBytes(), d1.getMappedIp());
			selector.select(0);
			assertEquals(true, selector.next());

			byte[] receiveAddress = d1.receive();
			byte[] receiveData = d1.getByte();
			{
				TestUtil.assertArrayEquals(this, "..", "abc".getBytes(), receiveData);
				TestUtil.assertArrayEquals(this, "..", d2.getMappedIp(), receiveAddress);
			}

			selector.select(0);
			assertEquals(false, selector.next());
		} finally {
			d1.close();
			d2.close();
		}
	}
	
}
