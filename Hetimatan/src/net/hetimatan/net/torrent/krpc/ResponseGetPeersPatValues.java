package net.hetimatan.net.torrent.krpc;

import java.io.IOException;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.net.torrent.util.bencode.BenDiction;
import net.hetimatan.net.torrent.util.bencode.BenList;
import net.hetimatan.net.torrent.util.bencode.BenObject;
import net.hetimatan.net.torrent.util.bencode.BenString;

public class ResponseGetPeersPatValues extends KrpcResponse {

	public ResponseGetPeersPatValues(String transactionId, String id, String token, BenList values) {
		super(transactionId);
		getArgs().put("id", new BenString(id));
		getArgs().put("token", new BenString(token));
		getArgs().put("values", values);
	} 

	public String getId() {
		return getArgs().getBenValue("id").toString();
	}

	public String getToken() {
		return getArgs().getBenValue("token").toString();
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
			
			return new ResponseGetPeersPatValues(diction.getBenValue("t").toString(), 
					diction.getBenValue("r").getBenValue("id").toString(),
					diction.getBenValue("r").getBenValue("token").toString(),				
					(BenList)diction.getBenValue("r").getBenValue("values")
					);
		} finally {
			reader.pushMark();
		}
	}

}
