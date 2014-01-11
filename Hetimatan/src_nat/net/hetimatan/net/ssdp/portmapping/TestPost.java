package net.hetimatan.net.ssdp.portmapping;

import java.io.IOException;

import net.hetimatan.io.filen.ByteKyoroFile;
import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.net.http.HttpGet;
import net.hetimatan.util.event.CloseRunnerTask;
import net.hetimatan.util.event.net.KyoroSocketEventRunner;

public class TestPost {

	public static void main(String[] args) {
		String address = "http://192.168.0.1:2869/upnp/control/WANIPConn1";
		PortMappingRequestTemplate request = new PortMappingRequestTemplate();
		try {
			HttpGet getter = new HttpGet();
			{
				CashKyoroFile body = new CashKyoroFile(request.createBody_GetExternalIpAddress().getBytes());
				getter.setBody(body);
			}
			{
				getter.addHeader(PortMappingRequestTemplate.SOAPACTION_TYPE, PortMappingRequestTemplate.SOAPACTION_VALUE_GET_EXTERNAL_IP_ADDRESS);
			}
			getter.update(address);
			CloseRunnerTask close = new CloseRunnerTask(null);
			KyoroSocketEventRunner runner = getter.startTask(null, close);
			runner.waitByClose(30000);
			runner.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(InterruptedException e) {
			e.printStackTrace();			
		} finally {
			System.out.println("end");
		}
	}
}
