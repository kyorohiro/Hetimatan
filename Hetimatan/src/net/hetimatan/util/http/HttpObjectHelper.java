package net.hetimatan.util.http;

public class HttpObjectHelper {
	private static long[] sdef = {0, Long.MAX_VALUE};
	public static long[] getRange(String range) {
		range = range.replaceAll("\\r\\n\\s\\t", "");
		if(range == null|| range.length() == 0){return sdef;}
		String[] r = range.split(",");
		if(range == null|| range.length() == 0){return sdef;}
		if(r.length == 0) {return sdef;}
		long[] ret = new long[r.length*2];
		String vs[];
		try {
			for(int i=0;i<ret.length;i+=2) {
				vs = r[i/2].split("-");
				if(vs.length ==0) {
					return sdef;
				}
				ret[i] = Long.parseLong(vs[0]);
				if(vs.length>1) {
					ret[i+1] = Long.parseLong(vs[1]);
				} else {
					ret[i+1] = Long.MAX_VALUE;
				}
			}
		} catch(NumberFormatException e) {
			return sdef;
		}
		return ret;
	}
}
