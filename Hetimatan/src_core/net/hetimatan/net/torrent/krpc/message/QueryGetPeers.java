package net.hetimatan.net.torrent.krpc.message;

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
	public QueryGetPeers(BenString transactionId, BenString id, BenString infoHash) {
		super("get_peers", transactionId);
		getArgs().put("id", id);
		getArgs().put("info_hash", infoHash);
	}

	public QueryGetPeers(BenString transactionId, BenDiction diction, BenString id, BenString infoHash) {
		super("get_peers", transactionId, diction);
		getArgs().put("id", id);
		getArgs().put("info_hash", infoHash);
	}

	public BenString getId() {
		return (BenString)getArgs().getBenValue("id");
	}

	public BenString getInfoHash() {
		return (BenString)getArgs().getBenValue("info_hash");
	}

	public static QueryGetPeers decode(MarkableReader reader) throws IOException {
		reader.popMark();
		try {
			BenDiction diction = BenDiction.decodeDiction(reader);
			if(!QueryGetPeers.check(diction)){
				throw new IOException();
			}
			return new QueryGetPeers(
					(BenString)diction.getBenValue("t"), (BenDiction)diction.getBenValue("a"),
					(BenString)diction.getBenValue("a").getBenValue("id"),
					(BenString)diction.getBenValue("a").getBenValue("info_hash")
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
