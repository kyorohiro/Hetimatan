package net.hetimatan.net.ssdp.portmapping;

import java.io.IOException;
import java.lang.ref.WeakReference;

import net.hetimatan.net.ssdp.SSDPClient;
import net.hetimatan.net.ssdp.SSDPClientListener;
import net.hetimatan.net.ssdp.message.SSDPMessage;
import net.hetimatan.net.ssdp.message.SSDPSearchMessage;
import net.hetimatan.net.ssdp.message.SSDPStartLine;
import net.hetimatan.net.ssdp.portmapping._task.WorkerDelPortMapping;
import net.hetimatan.net.ssdp.portmapping._task.WorkerAddPortMapping;
import net.hetimatan.net.ssdp.portmapping._task.WorkerGetExternalIpAddress;
import net.hetimatan.net.ssdp.portmapping._task.WorkerGetServiceInfoFromRootDevice;
import net.hetimatan.util.http.HttpRequestHeader;

public class PortMappingClient {

	private PortMappingClientEventDispatcher mDispatcher = new PortMappingClientEventDispatcher();
	private SSDPClient mClient = null;
	private String mNicHostName = null;

	protected PortMappingClient() {
		mClient = new SSDPClient();
	}

	public static PortMappingClient startPortMapping(String nicHostName, PortMappingClientListener listener) throws IOException {
		PortMappingClient client = new PortMappingClient();
		client.setLocation(nicHostName);
		client.setListener(listener);
		client.start();
		return client;
	}

	public void setLocation(String nicHostName) {
		mNicHostName = nicHostName;
	}

	public PortMappingClientEventDispatcher getDispatcher() {
		return mDispatcher;
	}

	public void start() throws IOException {
		mClient.init(mNicHostName);
		mClient.addSSDPClientListener(new RObserver(this));
		mClient.startMessageReceiver();
	}

	public void searchDevice() throws IOException {
		mClient.sendMessage(new SSDPSearchMessage(
				SSDPSearchMessage.UPNP_INTERNET_GATEWAY,
				3));
	}

	public boolean getRootDevice(SSDPMessage message) {
		if(!("200".equals(message.getLine().getCode()))) { 
			return false;}
		if(!(SSDPStartLine.TYPE_RESPONSE == message.getLine().getType())) {
			return false;}

		HttpRequestHeader locationHeader = message.getHeader("location");
		HttpRequestHeader stHeader = message.getHeader("st");

		if(locationHeader == null|| stHeader == null) {
			return false;}
		
		String location = locationHeader.getValue();
		String st = stHeader.getValue();
		System.out.println("<SimplePortMapping><location>"+location+"</location></SimplePortMapping>");
		System.out.println("<SimplePortMapping><st>"+st+"</st></SimplePortMapping>");
		if(!st.contains("InternetGatewayDevice:1")) {
			return false;}

		getServiceInfoFromRootDevice(location);
		return true;
	}

	public void addPortMapping(String location, PortMappingInfo info) {
		try {
			WorkerAddPortMapping cl = new WorkerAddPortMapping(this, location, info);
			cl.startTask(null, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deletePortMapping(String location, PortMappingInfo info) {
		try {
			WorkerDelPortMapping cl = new WorkerDelPortMapping(this, location, info);
			cl.startTask(null, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private WorkerGetServiceInfoFromRootDevice mWorkerGetRootDevice = null; 
	public void getServiceInfoFromRootDevice(String location) {
		try {
			mWorkerGetRootDevice = new WorkerGetServiceInfoFromRootDevice(location, this);
			mWorkerGetRootDevice.startTask(null, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private WorkerGetExternalIpAddress mWorkerGetExternalIpAddress = null; 
	public void getExternalIpAddress(String location) {
		try {
			mWorkerGetExternalIpAddress = new WorkerGetExternalIpAddress(location, this);
			mWorkerGetExternalIpAddress.startTask(null, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		mClient.close();
	}

	public void setListener(PortMappingClientListener observer) {
		mDispatcher.addObserver(observer);
	}

	public static class RObserver implements SSDPClientListener {

		private WeakReference<PortMappingClient> mClient = null;

		public RObserver(PortMappingClient client) {
			mClient = new WeakReference<PortMappingClient>(client);
		}

		@Override
		public void onReceiveSSDPMessage(SSDPClient client, SSDPMessage message) {

			PortMappingClient portMapping = mClient.get();
			if(portMapping != null) {
				portMapping.getDispatcher().dispatchReceiveSSDPMessage(client, message);
			}

			System.out.println("##\r\n"+message.toString()+"\r\n##");

		}
	}

}
