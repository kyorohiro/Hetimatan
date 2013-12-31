package net.hetimatan.net.ssdp.sample;

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
	
	public String _data= 
	"<?xml version=\"1.0\"?>\r\n"+
	"<root xmlns=\"urn:schemas-upnp-org:device-1-0\">\r\n"+
	"  <specVersion>\r\n"+
	"    <major>1</major>\r\n"+
	"    <minor>0</minor>\r\n"+
	"  </specVersion>\r\n"+
	"  <device>\r\n"+
	"    <deviceType>urn:schemas-upnp-org:device:InternetGatewayDevice:1</deviceType>\r\n"+
	"    <friendlyName>Aterm Series</friendlyName>\r\n"+
	"    <manufacturer>NEC Corporation/NEC AccessTechnica,Ltd.</manufacturer>\r\n"+
	"    <modelDescription>Broadband Router and Wireless Access Point</modelDescription>\r\n"+
	"    <modelName>Aterm Series</modelName>\r\n"+
	"    <modelNumber></modelNumber>\r\n"+
	"    <serialNumber>0000001</serialNumber>\r\n"+
	"    <UDN>uuid:79f0447f-860fbb81</UDN>\r\n"+
	"    <serviceList>\r\n"+
	"      <service>\r\n"+
	"         <serviceType>urn:schemas-upnp-org:service:Layer3Forwarding:1</serviceType>\r\n"+
	"         <serviceId>urn:upnp-org:serviceId:L3Frwd1</serviceId>\r\n"+
	"         <controlURL>/upnp/control/L3Frwd1</controlURL>\r\n"+
	"         <eventSubURL>/upnp/event/L3Frwd1</eventSubURL>\r\n"+
	"         <SCPDURL>/upnp/L3Frwd1.xml</SCPDURL>\r\n"+
	"      </service>\r\n"+
	"    </serviceList>\r\n"+
	"    <deviceList>\r\n"+
	"      <device>\r\n"+
	"        <deviceType>urn:schemas-upnp-org:device:WANDevice:1</deviceType>\r\n"+
	"        <friendlyName>Aterm Series</friendlyName>\r\n"+
	"        <manufacturer>NEC Corporation/NEC AccessTechnica,Ltd.</manufacturer>\r\n"+
	"        <modelDescription>Broadband Router and Wireless Access Point</modelDescription>\r\n"+
	"        <modelName>Aterm Series</modelName>\r\n"+
	"        <modelNumber></modelNumber>\r\n"+
	"        <serialNumber>0000001</serialNumber>\r\n"+
	"        <UDN>uuid:79f0447f-860fbb83</UDN>\r\n"+
	"        <serviceList>\r\n"+
	"          <service>\r\n"+
	"            <serviceType>urn:schemas-upnp-org:service:WANCommonInterfaceConfig:1</serviceType>\r\n"+
	"            <serviceId>urn:upnp-org:serviceId:WANCommonIFC1</serviceId>\r\n"+
	"            <controlURL>/upnp/control/WANCommonIFC1</controlURL>\r\n"+
	"            <eventSubURL>/upnp/event/WANCommonIFC1</eventSubURL>\r\n"+
	"            <SCPDURL>/upnp/WANCommonIFC1.xml</SCPDURL>\r\n"+
	"          </service>\r\n"+
	"        </serviceList>\r\n"+
	"     <deviceList>\r\n"+
	"       <device>\r\n"+
	"         <deviceType>urn:schemas-upnp-org:device:WANConnectionDevice:1</deviceType>\r\n"+
	"         <friendlyName>Aterm Series</friendlyName>\r\n"+
	"         <manufacturer>NEC Corporation/NEC AccessTechnica,Ltd.</manufacturer>\r\n"+
	"         <modelDescription>Broadband Router and Wireless Access Point</modelDescription>\r\n"+
	"         <modelName>Aterm Series</modelName>\r\n"+
	"         <modelNumber></modelNumber>\r\n"+
	"         <serialNumber>0000001</serialNumber>\r\n"+
	"         <UDN>uuid:79f0447f-860fbb85</UDN>\r\n"+
	"         <serviceList>\r\n"+
	"           <service>\r\n"+
	"             <serviceType>urn:schemas-upnp-org:service:WANIPConnection:1</serviceType>\r\n"+
	"               <serviceId>urn:upnp-org:serviceId:WANIPConn1</serviceId>\r\n"+
	"               <controlURL>/upnp/control/WANIPConn1</controlURL>\r\n"+
	"               <eventSubURL>/upnp/event/WANIPConn1</eventSubURL>\r\n"+
	"               <SCPDURL>/upnp/WANIPConn1.xml</SCPDURL>\r\n"+
	"           </service>\r\n"+
	"          </serviceList>\r\n"+
	"         </device>\r\n"+
	"       </deviceList>\r\n"+
	"     </device>\r\n"+
	"  </deviceList>\r\n"+
	" <presentationURL>http://192.168.0.1/</presentationURL>\r\n"+
	" </device>\r\n"+
	"</root>\r\n\r\n";
	
	public static void main(String[] args) {
		RootDeviceXml2ServiceInfo parser = new RootDeviceXml2ServiceInfo();
		try {
			LinkedList<SSDPServiceInfo> serviceList= parser.createServiceList(parser._data2.getBytes());
			System.out.println(""+serviceList.toString());
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

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

	public static String _data2 = 
	"<?xml version=\"1.0\"?>\n"+
	"<root xmlns=\"urn:schemas-upnp-org:device-1-0\">\n"+
	"<specVersion>\n"+
	"<major>1</major>\n"+
	"<minor>0</minor>\n"+
	"</specVersion>\n"+
	"<device>\n"+
	"<deviceType>urn:schemas-upnp-org:device:InternetGatewayDevice:1</deviceType>\n"+
	"<friendlyName>Aterm Series</friendlyName>\n"+
	"<manufacturer>NEC Corporation/NEC AccessTechnica,Ltd.</manufacturer>\n"+
	"<modelDescription>Broadband Router and Wireless Access Point</modelDescription>\n"+
	"<modelName>Aterm Series</modelName>\n"+
	"<modelNumber></modelNumber>\n"+
	"<serialNumber>0000001</serialNumber>\n"+
	"<UDN>uuid:79f0447f-860fbb81</UDN>\n"+
	"<serviceList>\n"+
	"<service>\n"+
	"<serviceType>urn:schemas-upnp-org:service:Layer3Forwarding:1</serviceType>\n"+
	"<serviceId>urn:upnp-org:serviceId:L3Frwd1</serviceId>\n"+
	"<controlURL>/upnp/control/L3Frwd1</controlURL>\n"+
	"<eventSubURL>/upnp/event/L3Frwd1</eventSubURL>\n"+
	"<SCPDURL>/upnp/L3Frwd1.xml</SCPDURL>\n"+
	"</service>\n"+
	"</serviceList>\n"+
	"<deviceList>\n"+
	"<device>\n"+
	"<deviceType>urn:schemas-upnp-org:device:WANDevice:1</deviceType>\n"+
	"<friendlyName>Aterm Series</friendlyName>\n"+
	"<manufacturer>NEC Corporation/NEC AccessTechnica,Ltd.</manufacturer>\n"+
	"<modelDescription>Broadband Router and Wireless Access Point</modelDescription>\n"+
	"<modelName>Aterm Series</modelName>\n"+
	"<modelNumber></modelNumber>\n"+
	"<serialNumber>0000001</serialNumber>\n"+
	"<UDN>uuid:79f0447f-860fbb83</UDN>\n"+
	"<serviceList>\n"+
	"<service>\n"+
	"<serviceType>urn:schemas-upnp-org:service:WANCommonInterfaceConfig:1</serviceType>\n"+
	"<serviceId>urn:upnp-org:serviceId:WANCommonIFC1</serviceId>\n"+
	"<controlURL>/upnp/control/WANCommonIFC1</controlURL>\n"+
	"	<eventSubURL>/upnp/event/WANCommonIFC1</eventSubURL>\n"+
	"<SCPDURL>/upnp/WANCommonIFC1.xml</SCPDURL>\n"+
	"</service>\n"+
	"</serviceList>\n"+
	"<deviceList>\n"+
	"<device>\n"+
	"<deviceType>urn:schemas-upnp-org:device:WANConnectionDevice:1</deviceType>\n"+
	"<friendlyName>Aterm Series</friendlyName>\n"+
	"<manufacturer>NEC Corporation/NEC AccessTechnica,Ltd.</manufacturer>\n"+
	"<modelDescription>Broadband Router and Wireless Access Point</modelDescription>\n"+
	"<modelName>Aterm Series</modelName>\n"+
	"<modelNumber></modelNumber>\n"+
	"<serialNumber>0000001</serialNumber>\n"+
	"<UDN>uuid:79f0447f-860fbb85</UDN>\n"+
	"<serviceList>\n"+
	"<service>\n"+
	"<serviceType>urn:schemas-upnp-org:service:WANIPConnection:1</serviceType>\n"+
	"<serviceId>urn:upnp-org:serviceId:WANIPConn1</serviceId>\n"+
	"<controlURL>/upnp/control/WANIPConn1</controlURL>\n"+
	"<eventSubURL>/upnp/event/WANIPConn1</eventSubURL>\n"+
	"<SCPDURL>/upnp/WANIPConn1.xml</SCPDURL>\n"+
	"</service>\n"+
	"</serviceList>\n"+
	"</device>\n"+
	"</deviceList>\n"+
	"</device>\n"+
	"</deviceList>\n"+
	"<presentationURL>http://192.168.0.1/</presentationURL>\n"+
	"</device>\n"+
	"</root>\n";
	
}
