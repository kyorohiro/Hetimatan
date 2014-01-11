package net.hetimatan.net.ssdp.portmapping;

public class PortMappingRequestTemplate {
	public static final String CONTENT_TYPE = "text/xml";
	public static final String SOAPACTION_TYPE = "SOAPACTION";
	public static final String SOAPACTION_VALUE_ADD_PORT_MAPPING = "\"urn:schemas-upnp-org:service:WANPPPConnection:1#AddPortMapping\"";
	public static final String SOAPACTION_VALUE_DELETE_PORT_MAPPING = "\"urn:schemas-upnp-org:service:WANPPPConnection:1#DeletePortMapping\"";
	public static final String SOAPACTION_VALUE_GET_GENERIC_PORT_MAPPING_ENTRY = "\"urn:schemas-upnp-org:service:WANPPPConnection:1#GetGenericPortMappingEntry\"";
	public static final String SOAPACTION_VALUE_GET_EXTERNAL_IP_ADDRESS = "\"urn:schemas-upnp-org:service:WANPPPConnection:1#GetExternalIPAddress\"";

	public static final String TEMPLATE_ADD = 
	"<?xml version=\"1.0\"?>\r\n"+
	"<SOAP-ENV:Envelope xmlns:SOAP-ENV:=\"http://schemas.xmlsoap.org/soap/envelope/\" SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\r\n"+
	"  <SOAP-ENV:Body>\r\n"+
	"    <m:AddPortMapping xmlns:m=\"urn:schemas-upnp-org:service:WANPPPConnection:1\">\r\n"+
	"      <NewRemoteHost></NewRemoteHost>\r\n"+
	"      <NewExternalPort>@NewExternalPort</NewExternalPort>\r\n"+
	"      <NewProtocol>@NewProtocol</NewProtocol>\r\n"+
	"      <NewInternalPort>@NewInternalPort</NewInternalPort>\r\n"+
	"      <NewInternalClient>@NewInternalClient</NewInternalClient>\r\n"+
	"      <NewEnabled>@NewEnabled</NewEnabled>\r\n"+
	"      <NewPortMappingDescription>@NewPortMappingDescription</NewPortMappingDescription>\r\n"+
	"      <NewLeaseDuration>@NewLeaseDuration</NewLeaseDuration>\r\n"+
	"    </m:AddPortMapping>\r\n"+
	"  </SOAP-ENV:Body>\r\n"+
	"</SOAP-ENV:Envelope>\r\n";

	public static String createBody_Add(int newExternalPort, int newInternalPort, 
			String newInternalClient, String newProtocol,
			int newEnabled, int newLeaseDuration,
			String newPortMappingDescription) {
		return TEMPLATE_ADD
				.replace("@NewExternalPort", "" + newExternalPort)
				.replace("@NewInternalPort", "" + newInternalPort)
				.replace("@NewInternalClient", "" + newInternalClient)
				.replace("@NewProtocol", "" + newProtocol)
				.replace("@NewLeaseDuration", "" + newLeaseDuration)
				.replace("@NewEnabled", "" + newEnabled)
				.replace("@NewPortMappingDescription", "" + newPortMappingDescription)
				;
	}

	public static final String TEMPLATE_DEL = 
	"<?xml version=\"1.0\"?>\r\n"+
	"<SOAP-ENV:Envelope xmlns:SOAP-ENV:=\"http://schemas.xmlsoap.org/soap/envelope/\" SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\r\n"+
	"  <SOAP-ENV:Body>\r\n"+
	"    <m:DeletePortMapping xmlns:m=\"urn:schemas-upnp-org:service:WANPPPConnection:1\">\r\n"+
	"      <NewRemoteHost></NewRemoteHost>\r\n"+
	"      <NewExternalPort>@NewExternalPort</NewExternalPort>\r\n"+
	"      <NewProtocol>@NewProtocol</NewProtocol>\r\n"+
	"    </m:DeletePortMapping>\r\n"+
	"  </SOAP-ENV:Body>\r\n"+
	"</SOAP-ENV:Envelope>\r\n";

	public static String createBody_Del(int newExternalPort, String newProtocol) {
		return TEMPLATE_DEL
				.replace("@NewExternalPort", "" + newExternalPort)
				.replace("@NewProtocol", "" + newProtocol)
				;
	}

	public static final String TEMPLATE_GET_GENERIC_PORT = 

	"<?xml version=\"1.0\"?>\r\n"+
	"<SOAP-ENV:Envelope xmlns:SOAP-ENV:=\"http://schemas.xmlsoap.org/soap/envelope/\" SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\r\n"+
	"  <SOAP-ENV:Body>\r\n"+
	"    <m:GetGenericPortMappingEntry xmlns:m=\"urn:schemas-upnp-org:service:WANPPPConnection:1\">\r\n"+
	"      <NewPortMappingIndex>@NewPortMappingIndex</NewPortMappingIndex>\r\n"+
	"    </m:GetGenericPortMappingEntry>\r\n"+
	"  </SOAP-ENV:Body>\r\n"+
	"</SOAP-ENV:Envelope>\r\n";

	public static String createBody_GetGebericPort(int newPortMappingIndex) {
		return TEMPLATE_GET_GENERIC_PORT
				.replace("@NewPortMappingIndex", "" + newPortMappingIndex)
				;
	}

	public static final String TEMPLATE_GET_EXTERNAL_IP_ADDRESS = 
	"<?xml version=\"1.0\"?>\r\n"+
	"<SOAP-ENV:Envelope xmlns:SOAP-ENV:=\"http://schemas.xmlsoap.org/soap/envelope/\" SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\r\n"+
	"  <SOAP-ENV:Body>\r\n"+
	"    <m:GetExternalIPAddress xmlns:m=\"urn:schemas-upnp-org:service:WANPPPConnection:1\"></m:GetExternalIPAddress>\r\n"+
	"  </SOAP-ENV:Body>\r\n"+
	"</SOAP-ENV:Envelope>\r\n";

	public static String createBody_GetExternalIpAddress() {
		return TEMPLATE_GET_EXTERNAL_IP_ADDRESS;
	}

}
