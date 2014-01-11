package net.hetimatan.net.ssdp;

import java.io.IOException;

import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.net.ssdp.message.SSDPMessage;
import junit.framework.TestCase;

public class TestForSSDPMessage extends TestCase {

	public void testXxx() throws IOException {
		String exp = 
		"HTTP/1.1 200 OK\r\n"+
		"CACHE-CONTROL: max-age=120\r\n"+
		"Location: http://192.168.0.1:2869/upnp/rootdevice.xml\r\n"+
		"SERVER: IGD-HTTP/1.1 UPnP/1.0 UPnP-Device-Host/1.0\r\n"+
		"ST: upnp:rootdevice\r\n"+
		"EXT:\r\n"+
		"USN: uuid:79f0447f-860fbb81::upnp:rootdevice\r\n"+
		"\r\n";

		MarkableFileReader reader = new MarkableFileReader(exp.getBytes());
		SSDPMessage message = SSDPMessage.decode(reader);
	}

	public void testYyy() throws IOException {
		String exp = 
				"HTTP/1.1 200 OK\r\n"+
				"CACHE-CONTROL: max-age=120\r\n"+
				"Location: http://192.168.0.1:2869/upnp/rootdevice.xml\r\n"+
				"SERVER: IGD-HTTP/1.1 UPnP/1.0 UPnP-Device-Host/1.0\r\n"+
				"ST: urn:schemas-upnp-org:device:InternetGatewayDevice:1\r\n"+
				"EXT:\r\n"+
				"USN: uuid:79f0447f-860fbb81::urn:schemas-upnp-org:device:InternetGatewayDevice:1\r\n"+
				"\r\n";
		MarkableFileReader reader = new MarkableFileReader(exp.getBytes());
		SSDPMessage message = SSDPMessage.decode(reader);
	}


}
