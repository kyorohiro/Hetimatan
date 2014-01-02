package net.hetimatan.net.ssdp.portmapping;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.net.http.HttpGet;
import net.hetimatan.net.ssdp.SSDPClient;
import net.hetimatan.net.ssdp.SSDPClientListener;
import net.hetimatan.net.ssdp.message.SSDPMessage;
import net.hetimatan.net.ssdp.message.SSDPSearchMessage;
import net.hetimatan.net.ssdp.portmapping._task.RootDeviceXml2ServiceInfo;
import net.hetimatan.net.ssdp.portmapping._task.WorkerDelPortMapping;
import net.hetimatan.net.ssdp.portmapping._task.RootDeviceXml2ServiceInfo.SSDPServiceInfo;
import net.hetimatan.net.ssdp.portmapping._task.WorkerAddPortMapping;
import net.hetimatan.net.ssdp.portmapping._task.WorkerGetRootDevice;
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
		mClient.startMessageReceiver();
	}

	public void searchDevice() throws IOException {
		mClient.sendMessage(new SSDPSearchMessage(
				SSDPSearchMessage.UPNP_INTERNET_GATEWAY,
				3));
	}

	public void getRootDevice(SSDPMessage message) {
		if(!"200".equals(message.getLine().getCode())) {
			return;
		}
		HttpRequestHeader header = message.getHeader("location");
		if(header == null) { return; }
		getRootDevice(header.getValue());
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

	public void getRootDevice(String location) {
		try {
			WorkerGetRootDevice cl = new WorkerGetRootDevice(location, this);
			cl.startTask(null, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void getExternalIpAddress(String location) {
		try {
			WorkerGetRootDevice cl = new WorkerGetRootDevice(location, this);
			cl.startTask(null, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stop() {
		
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
/*
 * 
	public static void main(String[] args) {
		String address = "http://192.168.0.1:2869/upnp/control/WANIPConn1";
		PortMappingRequest request = new PortMappingRequest();
		try {
			HttpGet getter = new HttpGet();
			{
				CashKyoroFile body = new CashKyoroFile(request.createBody_GetExternalIpAddress().getBytes());
				getter.setBody(body);
			}
			{
				getter.addHeader(PortMappingRequest.SOAPACTION_TYPE, PortMappingRequest.SOAPACTION_VALUE_GET_EXTERNAL_IP_ADDRESS);
			}
			getter.update(address);
			CloseRunnerTask close = new CloseRunnerTask(null);
			KyoroSocketEventRunner runner = getter.startTask(null, close);
			runner.waitByClose(30000);
			runner.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(InterruptedException e) {
			e.printStackTrace();			
		} finally {
			System.out.println("end");
		}
	}
*/
