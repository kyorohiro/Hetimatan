package net.hetimatan.net.ssdp;

import java.io.IOException;
import java.io.OutputStream;

import net.hetimatan.net.ssdp.message.SSDPMessage;

public class SSDPSearchMessage extends SSDPMessage {
	public static final String SL_MSEARCH = "M-SEARCH * HTTP/1.1";
	public static final String UPNP_ROOT_DEVICE = "upnp:rootdevice";
	public static final String UPNP_INTERNET_GATEWAY = "urn:schemas-upnp-org:device:InternetGatewayDevice:1";
	private String mST = "";
	private int mMX= 3;

	public SSDPSearchMessage(String searchTarget, int responseSec) {
		super(null);
		mST = searchTarget;
		mMX = responseSec;
	}

	public void encode(OutputStream output) throws IOException {
		output.write((SL_MSEARCH+"\r\n").getBytes());
        output.write(("HOST:"+ SSDPClient.SSDP_ADDRESS +":"+SSDPClient.SSDP_PORT+"\r\n").getBytes());
        output.write(("MAN:\"ssdp:discover\""+"\r\n").getBytes());
        output.write(("ST:"+mST+"\r\n").getBytes());
        output.write(("MX:" +mMX+"\r\n").getBytes());
        output.write(("\r\n").getBytes());
	}
}
