package net.hetimatan.net.torrent.krpc.message;

import java.io.IOException;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.net.torrent.util.bencode.BenDiction;
import net.hetimatan.net.torrent.util.bencode.BenObject;
import net.hetimatan.net.torrent.util.bencode.BenString;
import net.hetimatan.util.http.HttpObject;
import net.hetimatan.util.io.ByteArrayBuilder;

public class ResponseFindNode extends KrpcResponse {

	public ResponseFindNode(BenString transactionId, BenString id, BenString nodes) {
		super(transactionId);
		getArgs().put("id", id);
		getArgs().put("nodes", nodes);
	}

	public BenString getId() {
		return (BenString)getArgs().getBenValue("id");
	}

	public BenString getNodes() {
		return (BenString)getArgs().getBenValue("nodes");
	}

	public int numOfNode() {
		return getNodes().byteLength()/26;
	}

	public byte[] getNodeId(int index) throws IOException {
		byte[] ret = new byte[20];
		int start = index*26;
		byte[] buffer = getNodes().toByte();
		if(start+20>buffer.length) {
			throw new IOException();
		}
		System.arraycopy(buffer, start, ret, 0, 20);
		return ret;		
	}

	public byte[] getNodeIP(int index) throws IOException {
		byte[] ret = new byte[4];
		int start = index*26 + 20;
		byte[] buffer = getNodes().toByte();
		if(start+4>buffer.length) {
			throw new IOException();
		}
		System.arraycopy(buffer, start, ret, 0, 4);
		return ret;
	}

	public int getNodePort(int index) throws IOException {
		byte[] ret = new byte[2];
		int start = index*26 + 20 + 4;
		byte[] buffer = getNodes().toByte();
		if(start+2>buffer.length) {
			throw new IOException();
		}
		System.arraycopy(buffer, start, ret, 0, 2);
		return ByteArrayBuilder.parseShort(ret, 0, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN);
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
			
			return new ResponseFindNode(
					(BenString)diction.getBenValue("t"), 
					(BenString)diction.getBenValue("r").getBenValue("id"),
					(BenString)diction.getBenValue("r").getBenValue("nodes")				
					);
		} finally {
			reader.pushMark();
		}
	}

}
