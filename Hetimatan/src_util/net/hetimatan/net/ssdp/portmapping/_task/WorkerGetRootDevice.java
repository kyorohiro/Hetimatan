package net.hetimatan.net.ssdp.portmapping._task;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.LinkedList;

import net.hetimatan.net.http.HttpGet;
import net.hetimatan.net.ssdp.SSDPServiceInfo;
import net.hetimatan.net.ssdp.portmapping.PortMappingClient;

public class WorkerGetRootDevice extends HttpGet {
	private WeakReference<PortMappingClient> mClient = null;
	public WorkerGetRootDevice(String url, PortMappingClient client) throws IOException {
		super();
		update(url);
		mClient = new WeakReference<PortMappingClient>(client);
	}

	@Override
	public void recvBody() throws IOException, InterruptedException {
		super.recvBody();

		byte[] buffer = getBody();
		System.out.println("##"+new String(buffer)+"##");

		RootDeviceXml2ServiceInfo converter = new RootDeviceXml2ServiceInfo();
		LinkedList<SSDPServiceInfo> infos = converter.createServiceList((new String(buffer)).getBytes());//buffer);//converter._data.getBytes());//buffer);
		for(SSDPServiceInfo serviceInfo:infos) {
			{
				PortMappingClient client = mClient.get();
				if(client != null) {
					client.getDispatcher().dispatchFindSSDPServiceInfo(serviceInfo);
				}
			}
			System.out.println(""+serviceInfo);
		}
	}
}