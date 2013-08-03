package net.hetimatan.net.torrent.krpc.message;

import java.io.IOException;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.net.torrent.util.bencode.BenDiction;
import net.hetimatan.net.torrent.util.bencode.BenObject;
import net.hetimatan.net.torrent.util.bencode.BenString;

public class ResponseFindNode extends KrpcResponse {

	public ResponseFindNode(String transactionId, String id, String nodes) {
		super(transactionId);
		getArgs().put("id", new BenString(id));
		getArgs().put("nodes", new BenString(nodes));
	} 

	public String getId() {
		return getArgs().getBenValue("id").toString();
	}

	public String getNodes() {
		return getArgs().getBenValue("nodes").toString();
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
		return true;
	}

	public static ResponseFindNode decode(MarkableReader reader) throws IOException {
		reader.popMark();
		try {
			BenDiction diction = BenDiction.decodeDiction(reader);
			if(!check(diction)) {
				reader.backToMark();
				throw new IOException();
			}
			
			return new ResponseFindNode(diction.getBenValue("t").toString(), 
					diction.getBenValue("r").getBenValue("id").toString(),
					diction.getBenValue("r").getBenValue("nodes").toString()				
					);
		} finally {
			reader.pushMark();
		}
	}

}