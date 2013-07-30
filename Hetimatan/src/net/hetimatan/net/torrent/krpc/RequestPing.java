package net.hetimatan.net.torrent.krpc;

import java.io.IOException;
import java.io.OutputStream;

import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.net.torrent.util.bencode.BenDiction;
import net.hetimatan.net.torrent.util.bencode.BenObject;
import net.hetimatan.net.torrent.util.bencode.BenString;

public class RequestPing extends KrpcRequest {

	private String mId;
	public RequestPing(String transactionId, String id) {
		super(transactionId);
		mId = id;
	} 

	public RequestPing(String transactionId, BenDiction diction, String id) {
		super(transactionId, diction);
		mId = id;
	}

	@Override
	public String getTransactionId() {
		return super.getTransactionId();
	}

	public String getId() {
		return mId;
	}

	public void encode(OutputStream output) throws IOException {
		BenDiction diction = createDiction();
		diction.encode(output);
	}

	public static RequestPing decode(MarkableReader reader) throws IOException {
		reader.popMark();
		try {
			BenDiction diction = BenDiction.decodeDiction(reader);
			if(!RequestPing.check(diction)){
				throw new IOException();
			}
			return new RequestPing(diction.getBenValue("t").toString(), (BenDiction)diction.getBenValue("a"),
					diction.getBenValue("a").getBenValue("id").toString());
		} catch(IOException e) {
			throw e;
		} finally {
			reader.pushMark();
		}
	}
	
	@Override
	public BenDiction createDiction() {
		BenDiction diction = super.createDiction();
		BenDiction a = (BenDiction)diction.getBenValue("a");
		a.append("id", new BenString(mId));
		return diction;
	}


	public static boolean check(BenDiction diction) {
		if(!KrpcRequest.check(diction)) {
			return false;
		}
		BenDiction args = (BenDiction)diction.getBenValue("a");
		BenObject id = args.getBenValue("id");
		if(id.getType() != BenObject.TYPE_STRI) {
			return false;
		}
		return true;
	}



}
