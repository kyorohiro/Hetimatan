package net.hetimatan.net.torrent.krpc.message;

import java.io.IOException;
import java.io.OutputStream;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.net.torrent.util.bencode.BenDiction;
import net.hetimatan.net.torrent.util.bencode.BenList;
import net.hetimatan.net.torrent.util.bencode.BenObject;
import net.hetimatan.net.torrent.util.bencode.BenInteger;
import net.hetimatan.net.torrent.util.bencode.BenString;

public class KrpcError {
	public static final Integer CODE_201 = 201;
	public static final String DESCRIPTION_201 = "Genetic Error";

	public static final Integer CODE_202 = 202;
	public static final String DESCRIPTION_202 = "Server Error";

	public static final Integer CODE_203 = 203;
	public static final String DESCRIPTION_203 = "Protocol Error, such as a malformed packet, invalid arguments, or bad token";

	public static final Integer CODE_204 = 204;
	public static final String DESCRIPTION_204 = "Method Unknown";

	private String mTransactionId = "xx";
	private BenList mArgs = null;

	public KrpcError(String transactionId, int errCode, String errDescription) {
		mTransactionId = transactionId;
		mArgs = new BenList();
		mArgs.append(new BenInteger(errCode));
		mArgs.append(new BenString(errDescription));
	} 

	public KrpcError(String transactionId,BenList diction) {
		mTransactionId = transactionId;
		mArgs = diction;
	} 

	public BenList getArgs() {
		return mArgs;
	}

	public String getTransactionId() {
		return mTransactionId;
	}

	public BenDiction createDiction() {
		BenDiction diction = new BenDiction();
		diction.put("e", mArgs);
		diction.put("t", new BenString(mTransactionId));
		diction.put("y", new BenString("e"));
		return diction;
	}

	public void encode(OutputStream output) throws IOException {
		BenDiction diction = createDiction();
		diction.encode(output);
	}

	public static KrpcError decode(MarkableReader reader) throws IOException {
		reader.pushMark();
		try {
			BenDiction diction = BenDiction.decodeDiction(reader);
			check(diction);
			return new KrpcError(diction.getBenValue("t").toString(), (BenList)diction.getBenValue("e"));
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
		if(v.getType() == BenObject.TYPE_STRI && "e".equals(v.toString())) {
			return true;
		} else {
			return false;
		}		
	}

	public static boolean checkArg(BenDiction diction) {
		BenObject response = diction.getBenValue("e");
		if(response.getType() == BenObject.TYPE_LIST&& response.size() >=2
		&& response.getBenValue(0).getType() == BenObject.TYPE_INTE &&
		response.getBenValue(1).getType() == BenObject.TYPE_STRI) {
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
