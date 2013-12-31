package net.hetimatan.net.ssdp.sample;

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
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;

import com.sun.xml.internal.messaging.saaj.packaging.mime.Header;
import com.sun.xml.internal.txw2.Document;

import net.hetimatan.net.http.HttpGet;
import net.hetimatan.net.http.request.HttpGetResponse;
import net.hetimatan.net.ssdp.SSDPClient;
import net.hetimatan.net.ssdp.SSDPClientListener;
import net.hetimatan.net.ssdp.message.SSDPMessage;
import net.hetimatan.net.ssdp.message.SSDPSearchMessage;
import net.hetimatan.net.ssdp.sample.RootDeviceXml2ServiceInfo.SSDPServiceInfo;
import net.hetimatan.util.http.HttpRequest;
import net.hetimatan.util.http.HttpRequestHeader;

public class PortMappingSample {

	public static void main(String[] args) {
		System.out.println("start ssdp test");
		SSDPClient client = new SSDPClient();
		try {
			ServerSocket socket 
			= new ServerSocket(8888);
			show();
//			client.init(InetAddress.getLocalHost().getHostAddress());//"192.168.0.3");
			client.init("192.168.0.3");
			client.addSSDPClientListener(new RObserver());
			client.sendMessage(new SSDPSearchMessage(
					SSDPSearchMessage.UPNP_INTERNET_GATEWAY,
					//SSDPSearchMessage.UPNP_ROOT_DEVICE,
//					SSDPClient.ST_CONTENT_DICTIONARY,
					3));
			client.startMessageReceiver();
//			while(true) {
//				DatagramPacket ret = client.receive();
//				System.out.println(new String(ret.getData()));
//			}
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

	public static class RObserver implements SSDPClientListener {
		@Override
		public void onReceiveSSDPMessage(SSDPClient client, SSDPMessage request) {
			System.out.println("##\r\n"+request.toString()+"\r\n##");
			if(!"200".equals(request.getLine().getCode())) {
				return;
			}

			HttpRequestHeader header = request.getHeader("location");
			try {
				SSDPGetRootDevice cl = new SSDPGetRootDevice(header.getValue());
				cl.startTask(null, null);
//				client.startHttpGet(header.getValue());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static class SSDPGetRootDevice extends HttpGet {
		public SSDPGetRootDevice(String url) throws IOException {
			super();
			update(url);
		}

		@Override
		public void recvBody() throws IOException, InterruptedException {
			super.recvBody();

			try {
				byte[] buffer = getBody();
				System.out.println("##"+buffer.length+"##");
				System.out.println("##"+buffer[buffer.length-1]+"##");
				System.out.println("##"+buffer[buffer.length-2]+"##");
				System.out.println("##"+buffer[buffer.length-3]+"##");
				System.out.println("##"+buffer[buffer.length-4]+"##");
				System.out.println("##"+buffer[buffer.length-5]+"##");
				System.out.println("##"+buffer[buffer.length-6]+"##");
				System.out.println("##"+buffer[buffer.length-7]+"##");
				System.out.println("##"+buffer[buffer.length-8]+"##");
				System.out.println("##"+buffer[buffer.length-9]+"##");
				System.out.println("##"+buffer[buffer.length-10]+"##");
				System.out.println("##"+buffer[buffer.length-11]+"##");
				System.out.println("##"+buffer[buffer.length-12]+"##");
				System.out.println("##"+buffer[buffer.length-13]+"##");
				System.out.println("##"+buffer[buffer.length-14]+"##");

				System.out.println("##"+new String(buffer)+"##");
				RootDeviceXml2ServiceInfo converter = new RootDeviceXml2ServiceInfo();
				LinkedList<SSDPServiceInfo> infos = converter.createServiceList((new String(buffer)).getBytes());//buffer);//converter._data.getBytes());//buffer);
				for(SSDPServiceInfo info:infos) {
					System.out.println(""+info);
				}
			} finally {
				//close();
			}
		}
		
	}
}

