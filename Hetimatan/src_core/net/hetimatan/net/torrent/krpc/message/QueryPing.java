package net.hetimatan.net.torrent.krpc.message;

import java.io.IOException;
import java.io.OutputStream;

import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.net.torrent.util.bencode.BenDiction;
import net.hetimatan.net.torrent.util.bencode.BenObject;
import net.hetimatan.net.torrent.util.bencode.BenString;

public class QueryPing extends KrpcQuery {

	/*
	 * todo mod id is byte array
	 */
	public QueryPing(BenString transactionId, BenString id) {
		super("ping", transactionId);
		getArgs().put("id", id);
	} 

	public QueryPing(BenString transactionId, BenDiction diction, BenString id) {
		super("ping", transactionId, diction);
		getArgs().put("id", id);
	}

	public BenString getId() {
		return (BenString)getArgs().getBenValue("id");
	}

	public static QueryPing decode(BenDiction diction) throws IOException {
		if(!QueryPing.check(diction)){
			throw new IOException();
		}
		return new QueryPing(
				(BenString)diction.getBenValue("t"),
				(BenDiction)diction.getBenValue("a"),
				(BenString)diction.getBenValue("a").getBenValue("id"));

	}
	public static QueryPing decode(MarkableReader reader) throws IOException {
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
		BenObject id = args.getBenValue("id");
		if(id.getType() != BenObject.TYPE_STRI) {
			return false;
		}
		if(!diction.getBenValue("q").toString().equals("ping")) {
			return false;
		}
		return true;
	}

}
