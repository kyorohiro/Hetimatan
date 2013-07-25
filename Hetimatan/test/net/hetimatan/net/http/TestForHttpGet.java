package net.hetimatan.net.http;

import java.io.IOException;

import net.hetimatan.io.filen.KFNextHelper;
import net.hetimatan.net.http.request.HttpGetResponse;
import net.hetimatan.util.event.CloseTask;
import net.hetimatan.util.event.EventTaskRunner;

import junit.framework.TestCase;

//todo chk
public class TestForHttpGet extends TestCase {
	public void testRedirect() throws InterruptedException, IOException {
		HttpServer3xx _3xx = new HttpServer3xx();
		_3xx.setPort(8080);
		_3xx.startServer(null);
		while(!_3xx.isBinded()){Thread.yield();}

		HttpServerResponseCheck rc = new HttpServerResponseCheck();
		rc.setPort(8081);
		rc.startServer(null);
		while(!rc.isBinded()){Thread.yield();}
	
		HttpGet httpget = new HttpGet();
		httpget.update("127.0.0.1", "/301?mv=http://127.0.0.1:8081", 8080);
		EventTaskRunner runner = httpget.startTask(null, new CloseTask(null));
		
		while(true){if(rc.getResponseNumber() >= 1){break;} Thread.sleep(0);}

		runner.waitByClose(10000);
		HttpGetResponse response = httpget.getGetResponse();
		
		System.out.println(new String(KFNextHelper.newBinary(response.getVF())));
		_3xx.close();
		rc.close();
	}
}
