package net.hetimatan.net.ssdp;

import java.io.IOException;
import java.io.OutputStream;

public abstract class SSDPMessage {
	public abstract void encode(OutputStream output) throws IOException;
}

