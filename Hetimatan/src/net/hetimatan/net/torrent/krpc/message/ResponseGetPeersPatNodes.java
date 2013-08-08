package net.hetimatan.net.torrent.krpc.message;

import java.io.IOException;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.net.torrent.util.bencode.BenDiction;
import net.hetimatan.net.torrent.util.bencode.BenObject;
import net.hetimatan.net.torrent.util.bencode.BenString;

public class ResponseGetPeersPatNodes extends KrpcResponse {

	public ResponseGetPeersPatNodes(BenString transactionId, BenString id, BenString token, BenString nodes) {
		super(transactionId.toString() );
		getArgs().put("id", id);
		getArgs().put("nodes", nodes);
		getArgs().put("token", token);
	} 

	public BenString getId() {
		return (BenString)getArgs().getBenValue("id");
	}

	public BenString getToken() {
		return (BenString)getArgs().getBenValue("token");
	}

	public BenString getNodes() {
		return (BenString)getArgs().getBenValue("nodes");
	}


	public static boolean check(BenDiction diction) { 
		if(!KrpcResponse.check(diction)){
			return false;
		}
		if(diction.getBenValue("r").getBenValue("id").getType() != BenObject.TYPE_STRI) {
			return false;
		}
		if(diction.getBenValue("r").getBenValue("nodes").getType() != BenObject.TYPE_STRI) {
			return false;
		}	
		if(diction.getBenValue("r").getBenValue("token").getType() != BenObject.TYPE_STRI) {
			return false;
		}	
		return true;
	}

	public static ResponseGetPeersPatNodes decode(MarkableReader reader) throws IOException {
		reader.popMark();
		try {
			BenDiction diction = BenDiction.decodeDiction(reader);
			if(!check(diction)) {
				reader.backToMark();
				throw new IOException();
			}
			
			return new ResponseGetPeersPatNodes((BenString)diction.getBenValue("t"), 
					(BenString)diction.getBenValue("r").getBenValue("id"),
					(BenString)diction.getBenValue("r").getBenValue("token"),				
					(BenString)diction.getBenValue("r").getBenValue("nodes")
					);
		} finally {
			reader.pushMark();
		}
	}

}
