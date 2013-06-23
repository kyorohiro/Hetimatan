package net.hetimatan.net.http;

import junit.framework.TestCase;

public class TestForHttpGet extends TestCase {

	public void testRedirect() throws InterruptedException {
		HttpServer3xx _3xx = new HttpServer3xx();
		_3xx.startServer(null);
		_3xx.setPort(8080);
		while(!_3xx.isBinded()){Thread.yield();}
	
		HttpGet httpget = new HttpGet("127.0.0.1", "/301?mv=http://www.google.com", 8080);
		httpget.startTask(null, null);
		Thread.sleep(10*60*1000);
	}

}
