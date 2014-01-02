package net.hetimatan.net.ssdp.portmapping._task;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;



public class RootDeviceXml2ServiceInfo {

	public SSDPServiceInfo createSSDPServiceInfo(Node serviceNode) {
		SSDPServiceInfo ret = new SSDPServiceInfo();

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

	public LinkedList<SSDPServiceInfo> createServiceList(byte[] buffer) throws IOException {
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
						ret.push(createSSDPServiceInfo(n1));
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

	public static class SSDPServiceInfo {
		public static final String SERVICE_TYPE = "servicetype";
		public static final String SERVICE_ID =  "serviceid";
		public static final String CONTROL_URL = "controlurl";
		public static final String EVENT_SUB_URL = "eventsuburl";
		public static final String SCPDURL = "scpdurl";
		
		private HashMap<String, String> mMsap = new HashMap<>();
		public void add(String key, String value) {
			mMsap.put(key, value);
		}

		public String toString() {
			Set<String> keys = mMsap.keySet();
			StringBuilder builder = new StringBuilder();
			for(String key:keys) {
				builder.append("#"+key+":"+mMsap.get(key)+"#");
			}
			return builder.toString();
		}

		public String get(String key) {
			String ret = mMsap.get(key);
			if(ret == null) {return "";}
			else {return ret;}
		}
	}

}
