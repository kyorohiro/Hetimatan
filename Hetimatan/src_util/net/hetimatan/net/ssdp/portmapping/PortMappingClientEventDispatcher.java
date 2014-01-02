package net.hetimatan.net.ssdp.portmapping;

import java.util.LinkedList;

import net.hetimatan.net.ssdp.SSDPClient;
import net.hetimatan.net.ssdp.message.SSDPMessage;
import net.hetimatan.net.ssdp.portmapping._task.RootDeviceXml2ServiceInfo.SSDPServiceInfo;

public class PortMappingClientEventDispatcher {
	private LinkedList<PortMappingClientListener> mObserverList = new LinkedList<>();
	
	public void addObserver(PortMappingClientListener listener) {
		mObserverList.add(listener);
	}

	public void dispatchFindNIC() {
		for(PortMappingClientListener l : mObserverList) {
			l.onFindNIC();
		}
	}

	public void dispatchFindSSDPServiceInfo(SSDPServiceInfo serviceInfo) {
		for(PortMappingClientListener l : mObserverList) {
			l.onFindSSDPService(serviceInfo);
		}
	}

	public void dispatchGetExternalIPAddress(String address) {
		for(PortMappingClientListener l : mObserverList) {
			l.onGetExternalIPAddress(address);
		}
	}

	public void dispatchReceiveSSDPMessage(SSDPClient client, SSDPMessage message) {
		for(PortMappingClientListener l : mObserverList) {
			l.onReceiveSSDPMessage(client, message);
		}
	}

	public void dispatchAddPortMapping(PortMappingInfo info) {
		for(PortMappingClientListener l : mObserverList) {
			l.onAddPortMapping(info);
		}		
	}
	
	public void dispatchDeletePortMapping(PortMappingInfo info) {
		for(PortMappingClientListener l : mObserverList) {
			l.onDeletePortMapping(info);
		}		
	}
}
