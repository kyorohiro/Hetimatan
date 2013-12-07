package net.hetimatan.net.torrent.krpc.message;

import java.io.IOException;
import java.io.OutputStream;

import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.net.torrent.util.bencode.BenDiction;
import net.hetimatan.net.torrent.util.bencode.BenInteger;
import net.hetimatan.net.torrent.util.bencode.BenObject;
import net.hetimatan.net.torrent.util.bencode.BenString;

public class QueryAnnouncePeer extends KrpcQuery {
	
	/*
	 * todo mod id and targetArray is byte array
	 */
	public QueryAnnouncePeer(BenString transactionId, BenString id, BenString infoHash, int port, BenString token) {
		super("announce_peer", transactionId);
		getArgs().put("id", id);
		getArgs().put("info_hash", infoHash);
		getArgs().put("port", new BenInteger(port));
		getArgs().put("token", token);
	} 

	public QueryAnnouncePeer(BenString transactionId, BenDiction diction, BenString id, BenString infoHash, int port, BenString token) {
		super("announce_peer", transactionId, diction);
		getArgs().put("id", id);
		getArgs().put("info_hash", infoHash);
		getArgs().put("port", new BenInteger(port));
		getArgs().put("token", token);
	}

	public BenString getId() {
		return (BenString)getArgs().getBenValue("id");
	}

	public BenString getInfoHash() {
		return (BenString)getArgs().getBenValue("info_hash");
	}

	public int getPort() {
		return getArgs().getBenValue("port").toInteger();
	}

	public BenString getToken() {
		return (BenString)getArgs().getBenValue("token");
	}

	public static QueryAnnouncePeer decode(MarkableReader reader) throws IOException {
		reader.popMark();
		try {
			BenDiction diction = BenDiction.decodeDiction(reader);
			if(!QueryAnnouncePeer.check(diction)){
				throw new IOException();
			}
			return new QueryAnnouncePeer(
					(BenString)diction.getBenValue("t"), (BenDiction)diction.getBenValue("a"),
					(BenString)diction.getBenValue("a").getBenValue("id"), 
					(BenString)diction.getBenValue("a").getBenValue("info_hash"),
					diction.getBenValue("a").getBenValue("port").toInteger(),
 					(BenString)diction.getBenValue("a").getBenValue("token")
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
		if(args.getBenValue("port").getType() != BenObject.TYPE_INTE) {
			return false;
		}
		if(args.getBenValue("info_hash").getType() != BenObject.TYPE_STRI) {
			return false;
		}
		if(args.getBenValue("token").getType() != BenObject.TYPE_STRI) {
			return false;
		}

		if(!diction.getBenValue("q").toString().equals("announce_peer")) {
			return false;
		}
		return true;
	}



}
