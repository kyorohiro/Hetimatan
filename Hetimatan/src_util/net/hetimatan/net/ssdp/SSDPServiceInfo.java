package net.hetimatan.net.ssdp;

import java.util.HashMap;
import java.util.Set;

import sun.util.logging.resources.logging;

public class SSDPServiceInfo {
	public static final String SERVICE_TYPE = "servicetype";
	public static final String SERVICE_ID =  "serviceid";
	public static final String CONTROL_URL = "controlurl";
	public static final String EVENT_SUB_URL = "eventsuburl";
	public static final String SCPDURL = "scpdurl";
	
	private String mLocation = "";
	public SSDPServiceInfo(String location) {
		mLocation = location;
	}

	public String getLocation() {
		return mLocation;
	}

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
