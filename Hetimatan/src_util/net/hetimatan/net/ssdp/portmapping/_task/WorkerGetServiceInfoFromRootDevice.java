package net.hetimatan.net.ssdp.portmapping._task;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.LinkedList;

import net.hetimatan.net.http.HttpGet;
import net.hetimatan.net.ssdp.SSDPServiceInfo;
import net.hetimatan.net.ssdp.portmapping.PortMappingClient;

public class WorkerGetServiceInfoFromRootDevice extends HttpGet {
	private WeakReference<PortMappingClient> mClient = null;
	private String mLocation = null;
	public WorkerGetServiceInfoFromRootDevice(String url, PortMappingClient client) throws IOException {
		super();
		mLocation = url;
		update(url);
		mClient = new WeakReference<PortMappingClient>(client);
	}

	@Override
	public void recvBody() throws IOException, InterruptedException {
		super.recvBody();

		byte[] buffer = getBody();
		System.out.println("##"+new String(buffer)+"##");

		RootDeviceXml2ServiceInfo converter = new RootDeviceXml2ServiceInfo();
		LinkedList<SSDPServiceInfo> infos = converter.createServiceList(mLocation, (new String(buffer)).getBytes());//buffer);//converter._data.getBytes());//buffer);
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