package net.hetimatan.net.ssdp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Enumeration;

public class HelloSSDP {

	public static void main(String[] args) {
		System.out.println("start ssdp test");
		SSDPClient client = new SSDPClient();
		try {
			ServerSocket socket 
			= new ServerSocket(8888);
			show();
//			client.init(InetAddress.getLocalHost().getHostAddress());//"192.168.0.3");
			client.init("192.168.0.3");
			client.sendMessage(new SSDPSearchMessage(
					SSDPSearchMessage.UPNP_ROOT_DEVICE,
//					SSDPClient.ST_CONTENT_DICTIONARY,
					3));
			while(true) {
				DatagramPacket ret = client.receive();
				System.out.println(new String(ret.getData()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("end ssdp test");		
	}

	public static void connect(ServerSocket socket) throws IOException {
		socket.bind(new InetSocketAddress(7000));
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

