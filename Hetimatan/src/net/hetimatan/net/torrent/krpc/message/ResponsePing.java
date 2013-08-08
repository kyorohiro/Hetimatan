package net.hetimatan.net.torrent.krpc.message;

import java.io.IOException;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.net.torrent.util.bencode.BenDiction;
import net.hetimatan.net.torrent.util.bencode.BenObject;
import net.hetimatan.net.torrent.util.bencode.BenString;

public class ResponsePing extends KrpcResponse {

	public ResponsePing(BenString transactionId, BenString id) {
		super(transactionId.toString());
		getArgs().put("id", id);
	} 

	public BenString getId() {
		return (BenString)getArgs().getBenValue("id");
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
			
			return new ResponsePing(
					(BenString)diction.getBenValue("t"), 
					(BenString)diction.getBenValue("r").getBenValue("id"));
		} finally {
			reader.pushMark();
		}
	}

}
