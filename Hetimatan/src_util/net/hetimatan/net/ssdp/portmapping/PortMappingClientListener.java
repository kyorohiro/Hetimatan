package net.hetimatan.net.ssdp.portmapping;

import net.hetimatan.net.ssdp.portmapping.RootDeviceXml2ServiceInfo.SSDPServiceInfo;

public interface PortMappingClientListener {
	public void onFindNIC();
	public void onFindSSDPService(SSDPServiceInfo serviceInfo);
	public void onGetExternalIPAddress(String address);
}
