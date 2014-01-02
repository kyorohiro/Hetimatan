package net.hetimatan.net.ssdp.portmapping;

import java.io.IOException;

import net.hetimatan.net.ssdp.SSDPClient;
import net.hetimatan.net.ssdp.SSDPServiceInfo;
import net.hetimatan.net.ssdp.message.SSDPMessage;
import net.hetimatan.net.ssdp.message.SSDPStartLine;
import net.hetimatan.util.http.HttpRequestHeader;

//
// auto portmapping
// 1. search device 
//   PortMappingClient.searchDevice();
// 2. request root device to service list
//    
//
public class SimplePortMapping {

	// for test
	public static void main(String[] args) {
		try {
			SimplePortMapping autoPortMapping = new SimplePortMapping("192.168.0.3");
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
		}

		@Override
		public void onGetExternalIPAddress(String address) {
		}

		@Override
		public void onAddPortMapping(PortMappingInfo info) {
		}

		@Override
		public void onDeletePortMapping(PortMappingInfo info) {
		}
		
	}
}
