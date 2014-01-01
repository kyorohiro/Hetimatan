package net.hetimatan.net.ssdp.portmapping;

import java.io.IOException;
import java.net.ServerSocket;

import net.hetimatan.net.ssdp.SSDPClient;
import net.hetimatan.net.ssdp.message.SSDPSearchMessage;
import net.hetimatan.net.ssdp.portmapping.PortMappingClient.RObserver;

public class PortMappingMain {
	public static void main(String[] args) {
		System.out.println("start ssdp test");
		SSDPClient client = new SSDPClient();
		try {
			ServerSocket socket 
			= new ServerSocket(8888);
			show();
			client.init("192.168.0.3");
			client.addSSDPClientListener(new RObserver());
			client.sendMessage(new SSDPSearchMessage(
					SSDPSearchMessage.UPNP_INTERNET_GATEWAY,
					3));
			client.startMessageReceiver();

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("end ssdp test");		
	}
}
