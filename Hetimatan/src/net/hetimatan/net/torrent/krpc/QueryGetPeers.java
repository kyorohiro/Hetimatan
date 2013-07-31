package net.hetimatan.net.torrent.krpc;

import java.io.IOException;
import java.io.OutputStream;

import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.net.torrent.util.bencode.BenDiction;
import net.hetimatan.net.torrent.util.bencode.BenObject;
import net.hetimatan.net.torrent.util.bencode.BenString;
import net.hetimatan.net.torrent.util.bencode.BenInteger;

public class QueryGetPeers extends KrpcQuery {
	
	/*
	 * todo mod id and targetArray is byte array
	 */
	public QueryGetPeers(String transactionId, String id, String infoHash) {
		super("get_peers", transactionId);
		getArgs().put("id", new BenString(id));
		getArgs().put("info_hash", new BenString(infoHash));
	}

	public QueryGetPeers(String transactionId, BenDiction diction, String id, String infoHash) {
		super("get_peers", transactionId, diction);
		getArgs().put("id", new BenString(id));
		getArgs().put("info_hash", new BenString(infoHash));
	}

	public String getId() {
		return getArgs().getBenValue("id").toString();
	}

	public String getInfoHash() {
		return getArgs().getBenValue("info_hash").toString();
	}

	public static QueryGetPeers decode(MarkableReader reader) throws IOException {
		reader.popMark();
		try {
			BenDiction diction = BenDiction.decodeDiction(reader);
			if(!QueryGetPeers.check(diction)){
				throw new IOException();
			}
			return new QueryGetPeers(diction.getBenValue("t").toString(), (BenDiction)diction.getBenValue("a"),
					diction.getBenValue("a").getBenValue("id").toString(), 
					diction.getBenValue("a").getBenValue("info_hash").toString()
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
		if(args.getBenValue("info_hash").getType() != BenObject.TYPE_STRI) {
			return false;
		}
		if(!diction.getBenValue("q").toString().equals("get_peers")) {
			return false;
		}
		return true;
	}



}
