package net.hetimatan.net.ssdp.portmapping._task;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.hetimatan.net.ssdp.SSDPServiceInfo;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;



public class RootDeviceXml2ServiceInfo {

	public SSDPServiceInfo createSSDPServiceInfo(String location, Node serviceNode) {
		SSDPServiceInfo ret = new SSDPServiceInfo(location);

		if(serviceNode == null) {return ret;}
		NodeList list = serviceNode.getChildNodes();
		if(list == null) {return ret;}

		for(int i=0;i<list.getLength();i++) {
			Node n = list.item(i);
			if(n == null) {continue;}
			String key = n.getNodeName();
			Node v = n.getFirstChild();
			if(v == null) {continue;}
			ret.add(key.toLowerCase(), v.getNodeValue());
		}
		return ret;
	}

	public LinkedList<SSDPServiceInfo> createServiceList(String location, byte[] buffer) throws IOException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new ByteArrayInputStream(buffer)));

			LinkedList<SSDPServiceInfo> ret = new LinkedList<>();
			Stack<Integer> pos = new Stack<>();
			Stack<Node> nod = new Stack<>();
			nod.push(doc);
			pos.push(0);
			Node node = null;
			NodeList list = null;
			while(nod.size()>0) {
				node = nod.pop();
				list = node.getChildNodes();
				for(int i=0;i<list.getLength();i++) {
					Node n1 = list.item(i);
					if(n1.getNodeName().equals("service")) {
						ret.push(createSSDPServiceInfo(location, n1));
					} else {
						nod.push(n1);
					}
				}
			}
			return ret;
		} catch(Throwable t) {
			t.printStackTrace();
			throw new IOException("");
		}
	}



}
