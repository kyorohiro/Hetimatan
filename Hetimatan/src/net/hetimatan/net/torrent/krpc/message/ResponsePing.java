package net.hetimatan.net.torrent.krpc.message;

import java.io.IOException;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.net.torrent.util.bencode.BenDiction;
import net.hetimatan.net.torrent.util.bencode.BenObject;
import net.hetimatan.net.torrent.util.bencode.BenString;

public class ResponsePing extends KrpcResponse {

	public ResponsePing(String transactionId, String id) {
		super(transactionId);
		getArgs().put("id", new BenString(id));
	} 

	public String getId() {
		return getArgs().getBenValue("id").toString();
	}


	public static boolean check(BenDiction diction) { 
		if(!KrpcResponse.check(diction)){
			return false;
		}
		if(diction.getBenValue("r").getBenValue("id").getType() != BenObject.TYPE_STRI) {
			return false;
		}	
		return true;
	}

	public static ResponsePing decode(MarkableReader reader) throws IOException {
		reader.popMark();
		try {
			BenDiction diction = BenDiction.decodeDiction(reader);
			if(!check(diction)) {
				reader.backToMark();
				throw new IOException();
			}
			
			return new ResponsePing(diction.getBenValue("t").toString(), 
					diction.getBenValue("r").getBenValue("id").toString());
		} finally {
			reader.pushMark();
		}
	}

}
