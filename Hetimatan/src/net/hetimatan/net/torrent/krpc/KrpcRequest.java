package net.hetimatan.net.torrent.krpc;

import java.io.IOException;
import java.io.OutputStream;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.net.torrent.util.bencode.BenDiction;
import net.hetimatan.net.torrent.util.bencode.BenObject;
import net.hetimatan.net.torrent.util.bencode.BenString;

public class KrpcRequest {
	private String mTransactionId = "xx";
	private BenDiction mArgs = null;

	public KrpcRequest(String transactionId) {
		mTransactionId = transactionId;
		mArgs = new BenDiction();
	} 

	public KrpcRequest(String transactionId, BenDiction diction) {
		mTransactionId = transactionId;
		mArgs = diction;
	} 

	public BenDiction getArgs() {
		return mArgs;
	}

	public String getTransactionId() {
		return mTransactionId;
	}

	public BenDiction createDiction() {
		BenDiction diction = new BenDiction();
		diction.append("a", mArgs);
		diction.append("q", new BenString("ping"));
		diction.append("t", new BenString(mTransactionId));
		diction.append("y", new BenString("q"));
		return diction;
	}
	public void encode(OutputStream output) throws IOException {
		BenDiction diction = createDiction();
		diction.encode(output);
	}

	public static KrpcRequest decode(MarkableReader reader) throws IOException {
		reader.pushMark();
		try {
			BenDiction diction = BenDiction.decodeDiction(reader);
			check(diction);
			return new KrpcRequest(diction.getBenValue("t").toString(), (BenDiction)diction.getBenValue("a"));
		} catch(IOException e){
			reader.backToMark();
			throw e;
		} finally {
			reader.popMark();
		}
	}
	public static boolean checkQueryName(BenDiction diction) {
		BenObject v = diction.getBenValue("q");
		if(v.getType() == BenObject.TYPE_STRI && "ping".equals(v.toString())) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean checkQueryTransactionId(BenDiction diction) {
		BenObject v = diction.getBenValue("t");
		if(v.getType() == BenObject.TYPE_STRI) {
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
