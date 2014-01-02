package net.hetimatan.net.ssdp.portmapping;

import java.io.IOException;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.net.ssdp.SSDPClient;
import net.hetimatan.net.ssdp.SSDPServiceInfo;
import net.hetimatan.net.ssdp.message.SSDPMessage;
import net.hetimatan.net.ssdp.message.SSDPStartLine;
import net.hetimatan.util.http.HttpGetRequestUri;
import net.hetimatan.util.http.HttpRequestHeader;

//
// auto portmapping
// 1. search device 
//   PortMappingClient.searchDevice();
// 2. request root device to service list
//   PortMappingClient.getRootDevice(message);
// 3. 
//
public class SimplePortMapping {

	// todo
	// weak reference issue
	// need following property
	public static SimplePortMapping autoPortMapping = null;

	// for test
	public static void main(String[] args) {
		try {
			autoPortMapping = new SimplePortMapping("192.168.0.3");
			autoPortMapping.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private PortMappingClient mClient = null;
	private String mNicHostName = "";
	private _Observer mObserver = new _Observer();

	public SimplePortMapping(String nicHostName) {
		mNicHostName = nicHostName;
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
			
		}

		@Override
		public void onGetExternalIPAddress(String address) {
			System.out.println("######externalIPAddress="+address);
		}

		@Override
		public void onAddPortMapping(PortMappingInfo info) {
		}

		@Override
		public void onDeletePortMapping(PortMappingInfo info) {
		}
		
	}
}
