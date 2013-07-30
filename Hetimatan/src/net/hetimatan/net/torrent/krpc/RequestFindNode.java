package net.hetimatan.net.torrent.krpc;

import java.io.IOException;
import java.io.OutputStream;

import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.net.torrent.util.bencode.BenDiction;
import net.hetimatan.net.torrent.util.bencode.BenObject;
import net.hetimatan.net.torrent.util.bencode.BenString;

public class RequestFindNode extends KrpcRequest {

	private String mId;
	private String mTargetId;
	
	/*
	 * todo mod id is byte array
	 */
	public RequestFindNode(String transactionId, String id, String targetId) {
		super(transactionId);
		mId = id;
		mTargetId = targetId;
	} 

	public RequestFindNode(String transactionId, BenDiction diction, String id, String targetId) {
		super(transactionId, diction);
		mId = id;
		mTargetId = targetId;
	}

	@Override
	public String getTransactionId() {
		return super.getTransactionId();
	}

	public String getId() {
		return mId;
	}

	public String getTargetId() {
		return mTargetId;
	}

	public void encode(OutputStream output) throws IOException {
		BenDiction diction = createDiction();
		diction.encode(output);
	}

	public static RequestFindNode decode(MarkableReader reader) throws IOException {
		reader.popMark();
		try {
			BenDiction diction = BenDiction.decodeDiction(reader);
			if(!RequestFindNode.check(diction)){
				throw new IOException();
			}
			return new RequestFindNode(diction.getBenValue("t").toString(), (BenDiction)diction.getBenValue("a"),
					diction.getBenValue("a").getBenValue("id").toString(), 
					diction.getBenValue("a").getBenValue("target").toString()
					);
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
		a.append("target", new BenString(mTargetId));
		return diction;
	}


	public static boolean check(BenDiction diction) {
		if(!KrpcRequest.check(diction)) {
			return false;
		}
		BenDiction args = (BenDiction)diction.getBenValue("a");
		if(args.getBenValue("id").getType() != BenObject.TYPE_STRI) {
			return false;
		}
		if(args.getBenValue("target").getType() != BenObject.TYPE_STRI) {
			return false;
		}
		return true;
	}



}
