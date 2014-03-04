package net.hetimatan.net.ssdp.portmapping;

import java.io.IOException;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.net.http.HttpServer;
import net.hetimatan.net.http.HttpServer.HttpServerEventDispatcher;
import net.hetimatan.net.http.HttpServer.HttpServerEventDispatcher.HttpServerListener;
import net.hetimatan.net.ssdp.SSDPClient;
import net.hetimatan.net.ssdp.SSDPServiceInfo;
import net.hetimatan.net.ssdp.message.SSDPMessage;
import net.hetimatan.util.http.HttpGetRequestUri;

//
// auto portmapping
// 1. search device 
//   PortMappingClient.searchDevice();
// 2. request service list to root device  
//   PortMappingClient.getRootDevice(message);
// 3. request externalIpAddress to router
//   PortMappingClient.getExternalIpAddress(postUrl);
public class SimplePortMapping {

	// --------------------------------------------------------------------------
	//
	// --------------------------------------------------------------------------
	// todo
	// weak reference issue
	// need following property
	public static SimplePortMapping sAutoPortMapping = null;
	public static HttpServer sServer = null;
	public static int sServerPort = 8081;
	public static HttpServerListener sObserver = new HttpServerListener() {
		@Override
		public void onBoot(HttpServer server) {
			try {
				PortMappingInfo info = new PortMappingInfo();
				info.newExternalPort = sServerPort;
				info.newProtocol = "TCP";
				info.newInternalPort = sServerPort;
				info.newInternalClient = "192.168.0.3";
				info.newEnabled = 1;
				info.newPortMappingDescription = "test";
				info.newLeaseDuration = 60*60; //1hour
//				info.newInternalClient.
				
				sAutoPortMapping = new SimplePortMapping("192.168.0.3", info);
				sAutoPortMapping.start();
			} catch (IOException e){e.printStackTrace();}
		}
	};
	// for test
	public static void main(String[] args) {
	//	try {
			sServer = new HttpServer();
			sServer.setPort(sServerPort);
			sServer.getDispatcher().addHttpServerListener(sObserver);
			sServer.startServer(null);

	//	} catch (IOException e) {
	//		e.printStackTrace();
	//	}
	}

	// --------------------------------------------------------------------------
	//
	// --------------------------------------------------------------------------


	private PortMappingClient mClient = null;
	private String mNicHostName = "";
	private _Observer mObserver = new _Observer();
	private String mCurrentExternalIPAddress = "";
	private PortMappingInfo mInfo = null;

	public SimplePortMapping(String nicHostName, PortMappingInfo info) {
		mNicHostName = nicHostName;
		mInfo = info;
	}

	public String getCurrentExternalIPAddress() {
		return mCurrentExternalIPAddress;
	}

	//
	// interface
	//
	public void start() throws IOException {
		mClient = PortMappingClient.startPortMapping(mNicHostName, mObserver);
		mClient.searchDevice();
	}

	public void stop() {
		
	}

	public void close() {
		
	}

	private ResultObserver mResultObserver = null;
	public void setResut(ResultObserver ro) {
		mResultObserver = ro;
	}

	interface ResultObserver {
		public void fin(boolean result);
	}

	//
	// interface 
	//
	class _Observer implements PortMappingClientListener {

		@Override
		public void onReceiveSSDPMessage(SSDPClient client, SSDPMessage message) {
			mClient.getRootDevice(message);
		}

		@Override
		public void onFindNIC() {
		}

		@Override
		public void onFindSSDPService(SSDPServiceInfo serviceInfo) {
			System.out.println("######onFindSSDPService="+serviceInfo.toString());
			String serviceType = serviceInfo.get(SSDPServiceInfo.SERVICE_TYPE);
			String controlUrl = serviceInfo.get(SSDPServiceInfo.CONTROL_URL);
			if(serviceType == null || !serviceType.contains("WANIPConnection:1")) {
				return;}
			if(controlUrl == null) {
				return;}
			
			String postUrl = "";
			if(controlUrl.startsWith("http://")) {
				postUrl = controlUrl;
			} else {
				MarkableFileReader reader = null;
				try {
					HttpGetRequestUri geturi = HttpGetRequestUri.decode(serviceInfo.getLocation());
					if(controlUrl.startsWith("/")){
						controlUrl = controlUrl.substring(1);
					}
					postUrl = "http://"+geturi.getHost()+":"+geturi.getPort()+"/"+controlUrl;
				} catch(IOException e){
					return;
				} 
			}
			System.out.println("######deviceControlUrl="+postUrl);
			mClient.getExternalIpAddress(postUrl);
			mClient.addPortMapping(postUrl, mInfo);
		}

		@Override
		public void onGetExternalIPAddress(String address) {
			System.out.println("######externalIPAddress="+address);
			if(address != null && address.length() != 0 ) {
				mCurrentExternalIPAddress = address;
			}
			//
			//
		}

		@Override
		public void onAddPortMapping(PortMappingInfo info) {
			System.out.println("#---------#");
			System.out.println("#---add------#");
			System.out.println("#"+info.toString());
			System.out.println("#---------#");
		}

		@Override
		public void onDeletePortMapping(PortMappingInfo info) {
			System.out.println("#---------#");
			System.out.println("#---del------#");
			System.out.println("#"+info.toString());
			System.out.println("#---------#");
		}
		
	}
}
