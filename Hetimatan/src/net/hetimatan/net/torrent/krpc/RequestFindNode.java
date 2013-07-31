package net.hetimatan.net.torrent.krpc;

import java.io.IOException;
import java.io.OutputStream;

import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.net.torrent.util.bencode.BenDiction;
import net.hetimatan.net.torrent.util.bencode.BenObject;
import net.hetimatan.net.torrent.util.bencode.BenString;

public class RequestFindNode extends KrpcRequest {
	
	/*
	 * todo mod id and targetArray is byte array
	 */
	public RequestFindNode(String transactionId, String id, String targetId) {
		super(transactionId);
		getArgs().put("id", new BenString(id));
		getArgs().put("target", new BenString(targetId));
	} 

	public RequestFindNode(String transactionId, BenDiction diction, String id, String targetId) {
		super(transactionId, diction);
		getArgs().put("id", new BenString(id));
		getArgs().put("target", new BenString(targetId));
	}

	public String getId() {
		return getArgs().getBenValue("id").toString();
	}

	public String getTargetId() {
		return getArgs().getBenValue("target").toString();
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
