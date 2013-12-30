package net.hetimatan.net.ssdp;

import net.hetimatan.util.http.HttpRequest;

public interface SSDPClientListener {
	public void onReceiveå(SSDPMessage header);
}
