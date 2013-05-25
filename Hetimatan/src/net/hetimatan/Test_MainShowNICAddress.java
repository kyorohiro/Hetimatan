package net.hetimatan;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class Test_MainShowNICAddress {

	public static void main(String[] args) {
		 try {
			 String address = java.net.InetAddress.getLocalHost().toString();
//			 System.out.println(""+address);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		 
		try {
			Enumeration<NetworkInterface> inters = NetworkInterface.getNetworkInterfaces();
			while(inters.hasMoreElements()) {
				NetworkInterface ni = inters.nextElement();
				StringBuilder out = new StringBuilder();
				out.append(""+ni.getDisplayName()+"\r\n");
				out.append(""+ni.getName()+"\r\n");
				Enumeration<InetAddress> adds = ni.getInetAddresses();
				while(adds.hasMoreElements()) {
					InetAddress add = adds.nextElement();
					out.append(add.getHostAddress()+"\r\n");
					out.append(add.getHostName()+"\r\n");
				}
//				System.out.println("##\n"+out.toString());
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
	}
}
