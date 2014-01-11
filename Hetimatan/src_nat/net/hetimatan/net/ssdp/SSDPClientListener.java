package net.hetimatan.net.ssdp;

import net.hetimatan.net.ssdp.message.SSDPMessage;
import net.hetimatan.util.http.HttpRequest;

public interface SSDPClientListener {
	public void onReceiveSSDPMessage(SSDPClient client, SSDPMessage message);
//	public void onReceiveResponseService();
}
