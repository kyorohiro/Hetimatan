package net.hetimatan.net.ssdp.portmapping._task;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.hetimatan.io.filen.CashKyoroFile;
import net.hetimatan.net.http.HttpGet;
import net.hetimatan.net.ssdp.portmapping.PortMappingClient;
import net.hetimatan.net.ssdp.portmapping.PortMappingRequest;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class WorkerGetExternalIpAddress extends HttpGet {
	private WeakReference<PortMappingClient> mClient = null;
	public WorkerGetExternalIpAddress(String location, PortMappingClient client) throws IOException {
		super();
		update(location);
		PortMappingRequest request = new PortMappingRequest();
		{
			CashKyoroFile body = new CashKyoroFile(request.createBody_GetExternalIpAddress().getBytes());
			setBody(body);
		}
		{
			addHeader(PortMappingRequest.SOAPACTION_TYPE, PortMappingRequest.SOAPACTION_VALUE_GET_EXTERNAL_IP_ADDRESS);
		}
		mClient = new WeakReference<PortMappingClient>(client);
	}

	@Override
	public void recvBody() throws IOException, InterruptedException {
		super.recvBody();

		byte[] buffer = getBody();
		System.out.println("##"+new String(buffer)+"##");
		String extractIpAddress = extractIpAddress(buffer);
		PortMappingClient client = mClient.get();
		if(client != null) {
			client.getDispatcher().dispatchGetExternalIPAddress(extractIpAddress);
		}
	}

	public static String extractIpAddress(byte[] buffer) throws IOException {
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setNamespaceAware(false);
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(new InputSource(new ByteArrayInputStream(buffer)));

				Stack<Node> nod = new Stack<>();
				nod.push(doc);
				Node node = null;
				NodeList list = null;
				while(nod.size()>0) {
					node = nod.pop();
					list = node.getChildNodes();
					for(int i=0;i<list.getLength();i++) {
						Node n1 = list.item(i);
						if(n1.getNodeName().equals("NewExternalIPAddress")) {
							return n1.getFirstChild().getNodeValue();
						} else {
							nod.push(n1);
						}
					}
				}
				return "";
			} catch(Throwable t) {
				t.printStackTrace();
				throw new IOException("");
			}
	}

}