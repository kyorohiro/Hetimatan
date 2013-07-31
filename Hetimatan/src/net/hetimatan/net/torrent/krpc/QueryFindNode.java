package net.hetimatan.net.torrent.krpc;

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
	public QueryFindNode(String transactionId, String id, String targetId) {
		super("find_node", transactionId);
		getArgs().put("id", new BenString(id));
		getArgs().put("target", new BenString(targetId));
	} 

	public QueryFindNode(String transactionId, BenDiction diction, String id, String targetId) {
		super("find_node", transactionId, diction);
		getArgs().put("id", new BenString(id));
		getArgs().put("target", new BenString(targetId));
	}

	public String getId() {
		return getArgs().getBenValue("id").toString();
	}

	public String getTarget() {
		return getArgs().getBenValue("target").toString();
	}

	public static QueryFindNode decode(MarkableReader reader) throws IOException {
		reader.popMark();
		try {
			BenDiction diction = BenDiction.decodeDiction(reader);
			if(!QueryFindNode.check(diction)){
				throw new IOException();
			}
			return new QueryFindNode(diction.getBenValue("t").toString(), (BenDiction)diction.getBenValue("a"),
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
