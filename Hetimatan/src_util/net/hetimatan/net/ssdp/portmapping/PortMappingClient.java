package net.hetimatan.net.ssdp.portmapping;

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
import net.hetimatan.net.ssdp.portmapping.RootDeviceXml2ServiceInfo.SSDPServiceInfo;
import net.hetimatan.util.http.HttpRequest;
import net.hetimatan.util.http.HttpRequestHeader;

public class PortMappingClient {



	public static void connect(ServerSocket socket) throws IOException {
		socket.bind(new InetSocketAddress(7000));
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

			byte[] buffer = getBody();

			System.out.println("##"+new String(buffer)+"##");
			RootDeviceXml2ServiceInfo converter = new RootDeviceXml2ServiceInfo();
			LinkedList<SSDPServiceInfo> infos = converter.createServiceList((new String(buffer)).getBytes());//buffer);//converter._data.getBytes());//buffer);
			for(SSDPServiceInfo info:infos) {
				System.out.println(""+info);
			}
		}		
	}
}

