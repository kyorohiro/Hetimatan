package net.hetimatan.net.ssdp;

import java.io.IOException;

import junit.framework.TestCase;
import net.hetimatan.net.ssdp.portmapping._task.UPNPGetExternalIpAddress;

public class TestForExternalIPAddress extends TestCase {
	String data = 
	"<?xml version=\"1.0\"?>\r\n"+
	"<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\r\n"+
	"	<SOAP-ENV:Body>\r\n"+
	"		<m:GetExternalIPAddressResponse xmlns:m=\"urn:schemas-upnp-org:service:WANIPConnection:1\">\r\n"+
	"			<NewExternalIPAddress>127.0.0.1</NewExternalIPAddress>\r\n"+
	"		</m:GetExternalIPAddressResponse>\r\n"+
	"	</SOAP-ENV:Body>\r\n"+
	"</SOAP-ENV:Envelope>\r\n";

	public void testExtract() throws IOException {
		String address = UPNPGetExternalIpAddress.extractIpAddress(data.getBytes());
		assertEquals("127.0.0.1", address);
	}
}
