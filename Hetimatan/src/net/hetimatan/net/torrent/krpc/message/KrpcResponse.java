package net.hetimatan.net.torrent.krpc.message;

import java.io.IOException;
import java.io.OutputStream;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.net.torrent.util.bencode.BenDiction;
import net.hetimatan.net.torrent.util.bencode.BenObject;
import net.hetimatan.net.torrent.util.bencode.BenString;

public class KrpcResponse {
	private String mTransactionId = "xx";
	private BenDiction mArgs = null;

	public KrpcResponse(String transactionId) {
		mTransactionId = transactionId;
		mArgs = new BenDiction();
	} 

	public KrpcResponse(String transactionId,BenDiction diction) {
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
		diction.put("r", mArgs);
		diction.put("t", new BenString(mTransactionId));
		diction.put("y", new BenString("r"));
		return diction;
	}

	public void encode(OutputStream output) throws IOException {
		BenDiction diction = createDiction();
		diction.encode(output);
	}

	public static KrpcResponse decode(MarkableReader reader) throws IOException {
		reader.pushMark();
		try {
			BenDiction diction = BenDiction.decodeDiction(reader);
			check(diction);
			return new KrpcResponse(diction.getBenValue("t").toString(), (BenDiction)diction.getBenValue("r"));
		} catch(IOException e){
			reader.backToMark();
			throw e;
		} finally {
			reader.popMark();
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
		if(v.getType() == BenObject.TYPE_STRI && "r".equals(v.toString())) {
			return true;
		} else {
			return false;
		}		
	}

	public static boolean checkArg(BenDiction diction) {
		BenObject response = diction.getBenValue("r");
		if(response.getType() == BenObject.TYPE_DICT) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean check(BenDiction diction) {
		if(!checkActionIsQuery(diction)) {
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
