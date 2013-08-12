package net.hetimatan.net.torrent.krpc.message;

import java.io.IOException;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.net.torrent.util.bencode.BenDiction;
import net.hetimatan.net.torrent.util.bencode.BenList;
import net.hetimatan.net.torrent.util.bencode.BenObject;
import net.hetimatan.net.torrent.util.bencode.BenString;

public class ResponseGetPeersPatValues extends KrpcResponse {

	public ResponseGetPeersPatValues(BenString transactionId, BenString id, BenString token, BenList values) {
		super(transactionId);
		getArgs().put("id", id);
		getArgs().put("token", token);
		getArgs().put("values", values);
	} 

	public BenString getId() {
		return (BenString)getArgs().getBenValue("id");
	}

	public BenString getToken() {
		return (BenString)getArgs().getBenValue("token");
	}

	public BenList getValues() {
		return (BenList)getArgs().getBenValue("values");
	}


	public static boolean check(BenDiction diction) { 
		if(!KrpcResponse.check(diction)){
			return false;
		}
		if(diction.getBenValue("r").getBenValue("id").getType() != BenObject.TYPE_STRI) {
			return false;
		}
		if(diction.getBenValue("r").getBenValue("token").getType() != BenObject.TYPE_STRI) {
			return false;
		}	
		if(diction.getBenValue("r").getBenValue("values").getType() != BenObject.TYPE_LIST) {
			return false;
		}	
		return true;
	}

	public static ResponseGetPeersPatValues decode(MarkableReader reader) throws IOException {
		reader.popMark();
		try {
			BenDiction diction = BenDiction.decodeDiction(reader);
			if(!check(diction)) {
				reader.backToMark();
				throw new IOException();
			}
			
			return new ResponseGetPeersPatValues(
					(BenString)diction.getBenValue("t"), 
					(BenString)diction.getBenValue("r").getBenValue("id"),
					(BenString)diction.getBenValue("r").getBenValue("token"),				
					(BenList)diction.getBenValue("r").getBenValue("values")
					);
		} finally {
			reader.pushMark();
		}
	}

}
