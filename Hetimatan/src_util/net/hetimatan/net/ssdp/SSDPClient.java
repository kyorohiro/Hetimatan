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
import net.hetimatan.util.http.HttpRequest;

class SSDPClient {
	public static final String SSDP_ADDRESS = "239.255.255.250";
	public static final int SSDP_PORT = 1900;
	
	private MulticastSocket mReceiveSocket = null;
	private NetworkInterface mReciveNInterface = null;
	private InetSocketAddress mReceiveGroup = null;

	public NetworkInterface getNetworkInterface(ServerSocket socket) throws SocketException {
		return NetworkInterface.getByName(socket.getInetAddress().getHostName());
	}

	public void init(String hostName) throws IOException {
		mReceiveGroup = new InetSocketAddress(SSDP_ADDRESS, SSDP_PORT);
		InetAddress ll = InetAddress.getByName(hostName);
		mReciveNInterface = NetworkInterface.getByInetAddress(ll);
		mReceiveSocket = new MulticastSocket(new InetSocketAddress(
				ll, SSDP_PORT));
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
		System.out.println("<MESSAGE>"+new String(data,length)+"</MESSAGE>");
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

	private LinkedList<SSDPClientListener> mObserver = new LinkedList<>();
	public void addSSDPClientListener(SSDPClientListener observer) {
		mObserver.add(observer);
	}

	public void dispatch(SSDPMessage request) {
		for(SSDPClientListener l : mObserver) {
			l.onReceiveå(request);
		}
	}

	Thread mTh = null;
	public void startMessageReceiver() {
		if(mTh == null || !mTh.isAlive()) {
			mTh = new Thread(new MessageReceiver(this));
			mTh.start();
		}
	}
	//
	//
	//
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