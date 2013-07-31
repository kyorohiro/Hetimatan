package net.hetimatan.net.torrent.krpc;

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
	public QueryAnnouncePeer(String transactionId, String id, String infoHash, int port, String token) {
		super("announce_peer", transactionId);
		getArgs().put("id", new BenString(id));
		getArgs().put("info_hash", new BenString(infoHash));
		getArgs().put("port", new BenInteger(port));
		getArgs().put("token", new BenString(infoHash));

	} 
	public QueryAnnouncePeer(String transactionId, BenDiction diction, String id, String infoHash, int port, String token) {
		super("announce_peer", transactionId, diction);
		getArgs().put("id", new BenString(id));
		getArgs().put("info_hash", new BenString(infoHash));
		getArgs().put("port", new BenInteger(port));
		getArgs().put("token", new BenString(infoHash));
	}

	public String getId() {
		return getArgs().getBenValue("id").toString();
	}

	public String getInfoHash() {
		return getArgs().getBenValue("info_hash").toString();
	}

	public int getPort() {
		return getArgs().getBenValue("port").toInteger();
	}

	public String getToken() {
		return getArgs().getBenValue("token").toString();
	}

	public static QueryAnnouncePeer decode(MarkableReader reader) throws IOException {
		reader.popMark();
		try {
			BenDiction diction = BenDiction.decodeDiction(reader);
			if(!QueryAnnouncePeer.check(diction)){
				throw new IOException();
			}
			return new QueryAnnouncePeer(diction.getBenValue("t").toString(), (BenDiction)diction.getBenValue("a"),
					diction.getBenValue("a").getBenValue("id").toString(), 
					diction.getBenValue("a").getBenValue("info_hash").toString(),
					diction.getBenValue("a").getBenValue("port").toInteger(),
					diction.getBenValue("a").getBenValue("token").toString()
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
