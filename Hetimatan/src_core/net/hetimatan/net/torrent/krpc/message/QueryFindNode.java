package net.hetimatan.net.torrent.krpc.message;

import java.io.IOException;
import java.io.OutputStream;

import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.net.torrent.util.bencode.BenDiction;
import net.hetimatan.net.torrent.util.bencode.BenObject;
import net.hetimatan.net.torrent.util.bencode.BenString;

public class QueryFindNode extends KrpcQuery {
	
	/*
	 * todo mod id and targetArray is byte array
	 */
	public QueryFindNode(BenString transactionId, BenString id, BenString targetId) {
		super("find_node", transactionId);
		getArgs().put("id", id);
		getArgs().put("target", targetId);
	} 

	public QueryFindNode(BenString transactionId, BenDiction diction, BenString id, BenString targetId) {
		super("find_node", transactionId, diction);
		getArgs().put("id", id);
		getArgs().put("target", targetId);
	}

	public BenString getId() {
		return (BenString)getArgs().getBenValue("id");
	}

	public BenString getTarget() {
		return (BenString)getArgs().getBenValue("target");
	}

	public static QueryFindNode decode(BenDiction diction) throws IOException {
		try {
			if(!QueryFindNode.check(diction)){
				throw new IOException();
			}
			return new QueryFindNode(
					(BenString)diction.getBenValue("t"), (BenDiction)diction.getBenValue("a"),
					(BenString)diction.getBenValue("a").getBenValue("id"), 
					(BenString)diction.getBenValue("a").getBenValue("target")
					);
		} catch(IOException e) {
			throw e;
		}
	}

	public static QueryFindNode decode(MarkableReader reader) throws IOException {
		reader.popMark();
		try {
			return decode(BenDiction.decodeDiction(reader));
		} catch(IOException e) {
			throw e;
		} finally {
			reader.pushMark();
		}
	}

	public static boolean check(BenDiction diction) {
		if(!KrpcQuery.check(diction)) {
			return false;
		}
		BenDiction args = (BenDiction)diction.getBenValue("a");
		if(args.getBenValue("id").getType() != BenObject.TYPE_STRI) {
			return false;
		}
		if(args.getBenValue("target").getType() != BenObject.TYPE_STRI) {
			return false;
		}
		if(!diction.getBenValue("q").toString().equals("find_node")) {
			return false;
		}
		return true;
	}



}
