package net.hetimatan.net.ssdp.portmapping;

import java.io.IOException;
import java.net.InetAddress;

import net.hetimatan.net.ssdp.SSDPClient;
import net.hetimatan.net.ssdp.SSDPServiceInfo;
import net.hetimatan.net.ssdp.message.SSDPMessage;

public class PortMappingMain {
	public static void main(String[] args) {
		try {
			PortMappingClient client = PortMappingClient.startPortMapping(
					InetAddress.getLocalHost().getHostAddress(),
					new EventCheck());
			client.searchDevice();
		} catch(IOException e) {
		
		}
	}

	public static class EventCheck implements PortMappingClientListener {

		@Override
		public void onFindNIC() {
		}

		@Override
		public void onFindSSDPService(SSDPServiceInfo serviceInfo) {
		}

		@Override
		public void onGetExternalIPAddress(String address) {
		}

		@Override
		public void onReceiveSSDPMessage(SSDPClient client, SSDPMessage request) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onAddPortMapping(PortMappingInfo info) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onDeletePortMapping(PortMappingInfo info) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
/*
	public static void main(String[] args) {
		System.out.println("start ssdp test");
		SSDPClient client = new SSDPClient();
		
		try {
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

	public static void show() throws SocketException {
		Enumeration<NetworkInterface> interfaceMap = NetworkInterface.getNetworkInterfaces();
		while (interfaceMap.hasMoreElements()) {
			NetworkInterface n = interfaceMap.nextElement();
			System.out.println("Interface " + n.getName() + ": ");
			Enumeration<InetAddress> adds = n.getInetAddresses();
			while (adds.hasMoreElements()) {
				InetAddress a = adds.nextElement();
				System.out.print("\tAddress " + ((a instanceof Inet4Address ? "(IPv4)"
						: (a instanceof Inet6Address ? "(IPv6)" : "(?)"))));
				System.out.println(": " + a.getHostAddress());
			}
		}
	}
}
*/