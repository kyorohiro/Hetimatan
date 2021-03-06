package net.hetimatan.net.ssdp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.LinkedList;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.filen.ByteKyoroFile;
import net.hetimatan.net.http.HttpGet;
import net.hetimatan.net.ssdp.message.SSDPMessage;
import net.hetimatan.util.event.net.KyoroSocketEventRunner;

public class SSDPClient {
	public static final String SSDP_ADDRESS = "239.255.255.250";
	public static final int SSDP_PORT = 1900;	
	public static final String URN_WAN_IP_CONNECTION = "urn:schemas-upnp-org:service:WANIPConnection:1";
			
	private MulticastSocket mReceiveSocket = null;
	private NetworkInterface mReciveNInterface = null;
	private InetSocketAddress mReceiveGroup = null;
	private LinkedList<SSDPClientListener> mObserver = new LinkedList<>();

	public void init(String hostName) throws IOException {
		mReceiveGroup = new InetSocketAddress(SSDP_ADDRESS, SSDP_PORT);
		InetAddress nicAddress = InetAddress.getByName(hostName);
		mReciveNInterface = NetworkInterface.getByInetAddress(nicAddress);
		mReceiveSocket = new MulticastSocket(new InetSocketAddress(nicAddress, SSDP_PORT));
		mReceiveSocket.joinGroup(mReceiveGroup, mReciveNInterface);
	}

	public void send(String message) throws IOException {
		byte[] data = message.getBytes("UTF8");
		send(data, data.length);
	}
	
	public void sendMessage(SSDPMessage message) throws IOException {
		ByteKyoroFile bf = new ByteKyoroFile();
		message.encode(bf.getLastOutput());
		byte[] buffer = bf.getBuffer();
		send(buffer, buffer.length);
	}

	public void send(byte[] data, int length) throws IOException {
		DatagramPacket dp = new DatagramPacket(data, length, mReceiveGroup);
		mReceiveSocket.send(dp);
		System.out.println("<MESSAGE>"+new String(data, 0, length)+"</MESSAGE>");
	}

	public DatagramPacket receive() throws IOException {
		System.out.println("_locatHost:="+mReceiveSocket.getLocalPort());
		byte[] buf = new byte[1024];
		DatagramPacket dp = new DatagramPacket(buf, buf.length);
		mReceiveSocket.receive(dp);
		return dp;
	}

	public void close() {
		if (mReceiveSocket != null){
			try {
				mReceiveSocket.leaveGroup(mReceiveGroup, mReciveNInterface);
			} catch (IOException e) {
				e.printStackTrace();
			}
			mReceiveSocket.close();
		}
	}


	//
	// observer
	//
	public void addSSDPClientListener(SSDPClientListener observer) {
		mObserver.add(observer);
	}

	public void dispatch(SSDPMessage request) {
		for(SSDPClientListener l : mObserver) {
			l.onReceiveSSDPMessage(this, request);
		}
	}


	//
	// message receiver
	//
	private Thread mReceiverThread = null;
	public void startMessageReceiver() {
		if(mReceiverThread == null || !mReceiverThread.isAlive()) {
			mReceiverThread = new Thread(new MessageReceiver(this));
			mReceiverThread.start();
		}
	}

	public static class MessageReceiver implements Runnable {
		SSDPClient mClient = null;
		
		public MessageReceiver(SSDPClient client) {
			mClient = client;
		}

		@Override
		public void run() {
			while(true) {
				action();
				if(Thread.interrupted()) {
					break;
				}
			}
		}
		
		public void action() {
			try {
				DatagramPacket packet = mClient.receive();
				byte[] buffer = packet.getData();
				System.out.print(new String(buffer));
				MarkableFileReader reader = new MarkableFileReader(buffer);
				mClient.dispatch(SSDPMessage.decode(reader));
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}	
	}
	
}