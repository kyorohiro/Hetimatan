package net.hetimatan.net.torrent.krpc.message;

import java.io.IOException;
import java.io.OutputStream;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.net.torrent.util.bencode.BenDiction;
import net.hetimatan.net.torrent.util.bencode.BenObject;
import net.hetimatan.net.torrent.util.bencode.BenString;

public class KrpcQuery {
	private BenString mTransactionId = null;
	private BenDiction mArgs = null;
	private String mMethodName = "xx";

	public KrpcQuery(String methodName, BenString transactionId) {
		mMethodName = methodName;
		mTransactionId = transactionId;
		mArgs = new BenDiction();
	} 

	public KrpcQuery(String methodName, BenString transactionId,BenDiction diction) {
		mMethodName = methodName;
		mTransactionId = transactionId;
		mArgs = diction;
	} 

	public BenDiction getArgs() {
		return mArgs;
	}

	public BenString getTransactionId() {
		return mTransactionId;
	}

	public BenDiction createDiction() {
		BenDiction diction = new BenDiction();
		diction.put("a", mArgs);
		diction.put("q", new BenString(mMethodName));
		diction.put("t", mTransactionId);
		diction.put("y", new BenString("q"));
		return diction;
	}

	public void encode(OutputStream output) throws IOException {
		BenDiction diction = createDiction();
		diction.encode(output);
	}

	public static KrpcQuery decode(MarkableReader reader) throws IOException {
		reader.pushMark();
		try {
			BenDiction diction = BenDiction.decodeDiction(reader);
			check(diction);
			return new KrpcQuery(diction.getBenValue("q").toString(), (BenString)diction.getBenValue("t"), (BenDiction)diction.getBenValue("a"));
		} catch(IOException e){
			reader.backToMark();
			throw e;
		} finally {
			reader.popMark();
		}
	}

	public static String getQueryName(BenDiction diction) {
		return diction.getBenValue("q").toString();
	}
	
	public static boolean checkQueryName(BenDiction diction) {
		if(diction.getBenValue("q").getType() == BenObject.TYPE_STRI) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean checkQueryTransactionId(BenDiction diction) {
		if(diction.getBenValue("t").getType() == BenObject.TYPE_STRI) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean checkActionIsQuery(BenDiction diction) {
		BenObject v = diction.getBenValue("y");
		if(v.getType() == BenObject.TYPE_STRI && "q".equals(v.toString())) {
			return true;
		} else {
			return false;
		}		
	}

	public static boolean checkArg(BenDiction diction) {
		BenObject args = diction.getBenValue("a");
		if(args.getType() == BenObject.TYPE_DICT) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean check(BenDiction diction) {
		if(!checkActionIsQuery(diction)) {
			return false;
		}
		if(!checkQueryName(diction)) {
			return false;
		}
		if(!checkQueryTransactionId(diction)) {
			return false;
		}
		if(!checkArg(diction)) {
			return false;
		}
		return true;
	}

}
