package net.hetimatan.net.ssdp.portmapping._task;

import java.io.IOException;
import java.lang.ref.WeakReference;

import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.net.http.HttpGet;
import net.hetimatan.net.ssdp.portmapping.PortMappingClient;
import net.hetimatan.net.ssdp.portmapping.PortMappingInfo;
import net.hetimatan.net.ssdp.portmapping.PortMappingRequest;

public class WorkerDelPortMapping extends HttpGet {
	private WeakReference<PortMappingClient> mClient = null;
	private PortMappingInfo mInfo = null;
	public WorkerDelPortMapping(PortMappingClient client, String location, PortMappingInfo info) throws IOException {
		super();
		update(location);
		mInfo = info;
		PortMappingRequest request = new PortMappingRequest();
		{
			CashKyoroFile body = new CashKyoroFile(
					request.createBody_Del(info.newExternalPort, info.newProtocol).getBytes());
			setBody(body);
		}
		{
			addHeader(PortMappingRequest.SOAPACTION_TYPE, PortMappingRequest.SOAPACTION_VALUE_DELETE_PORT_MAPPING);
		}
		mClient = new WeakReference<PortMappingClient>(client);
	}
	
	 @Override
	public void recvBody() throws IOException, InterruptedException {
		super.recvBody();
		PortMappingClient client = mClient.get();
		if(client != null) {
			client.getDispatcher().dispatchDeletePortMapping(mInfo);
		}
	}

}
