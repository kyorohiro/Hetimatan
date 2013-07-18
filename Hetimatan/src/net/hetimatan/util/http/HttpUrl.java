package net.hetimatan.util.http;


import java.io.IOException;
import java.io.OutputStream;


public class HttpUrl extends HttpObject {

	private String mHost = "127.0.0.1";
	private int mPort = 80;
	private String mMethod = "";

	public HttpUrl(String host, String method, int port) {
		mHost = host;
		mPort = port;
		mMethod = method;
	}

	public String getHost() {
		return mHost;
	}

	public int getPort() {
		return mPort;
	}

	public String getMethod() {
		return mMethod;
	}

	@Override
	public void encode(OutputStream output) throws IOException {
		if(mMethod == null || mMethod.length() == 0) {
			output.write(("http://"+mHost+":"+mPort).getBytes());
		} else {
			output.write(("http://"+mHost+":"+mPort+"/"+mMethod).getBytes());			
		}
	}

	public static HttpUrl decode(String location) {
		int startPort = -1;
		int endPort = -1;
		int startValue = location.length();
		if(location.startsWith("http://")) {
			location = location.substring("http://".length());
		}

		startPort = location.indexOf(":");
		startValue = location.indexOf("?");
		String host = location;
		String method = "";
		if(startValue== -1) {startValue = location.length();}

		int port = 80;
		if(startPort>=0 && startPort<=startValue) {
			endPort = location.indexOf("/",startPort);
			if(endPort<0) {endPort=startValue;}
			port = port(location.substring(startPort,endPort));
			host = location.substring(0,startPort) + location.substring(endPort,startValue); 
		}
		int startMethod = host.indexOf("/");
		if(startMethod != -1) {
			method = host.substring(startMethod);
			host = host.substring(0, startMethod);
		}		

		return new HttpUrl(host, method, port);
	}

	private static int port(String port) {
		try  {
			if(port.startsWith(":")) {
				port = port.substring(1);
			}
			return Integer.parseInt(port);
		} catch(NumberFormatException e) {
		}
		return 80;
	}
}

