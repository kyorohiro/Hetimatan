package net.hetimatan.net.ssdp.portmapping;

import net.hetimatan.net.ssdp.SSDPClient;
import net.hetimatan.net.ssdp.SSDPServiceInfo;
import net.hetimatan.net.ssdp.message.SSDPMessage;
public interface PortMappingClientListener {
	public void onReceiveSSDPMessage(SSDPClient client, SSDPMessage request);
	public void onFindNIC();
	public void onFindSSDPService(SSDPServiceInfo serviceInfo);
	public void onGetExternalIPAddress(String address);
	public void onAddPortMapping(PortMappingInfo info);
	public void onDeletePortMapping(PortMappingInfo info);	
}
