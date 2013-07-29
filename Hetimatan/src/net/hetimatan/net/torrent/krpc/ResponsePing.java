package net.hetimatan.net.torrent.krpc;

import java.io.IOException;
import java.io.OutputStream;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.net.torrent.util.bencode.BenDiction;
import net.hetimatan.net.torrent.util.bencode.BenObject;
import net.hetimatan.net.torrent.util.bencode.BenString;

public class ResponsePing {

	private String mId;
	private String mTransactionId = "xx";

	public ResponsePing(String id, String transactionId) {
		mId = id;
		mTransactionId = transactionId;
	} 

	public String getId() {
		return mId;
	}

	public String getTransactionId() {
		return mTransactionId;
	}

	public static ResponsePing decode(MarkableReader reader) throws IOException {
		reader.popMark();
		String transactionid = "";
		try {
			BenDiction diction = BenDiction.decodeDiction(reader);
			{
				BenObject v = diction.getBenValue("y");
				if(v.getType() == BenObject.TYPE_STRI && "r".equals(v.toString())) {
				} else {
					reader.backToMark();
					throw new IOException();
				}
			}
			{
				BenObject v = diction.getBenValue("t");
				if(v.getType() == BenObject.TYPE_STRI) {
					transactionid = v.toString();
				} else {
					reader.backToMark();
					throw new IOException();
				}
			}

			BenObject r = diction.getBenValue("r");
			if(r.getType() != BenObject.TYPE_DICT) {
				reader.backToMark();
				throw new IOException();
			}
			BenObject id = r.getBenValue("id");
			if(id.getType() != BenObject.TYPE_STRI) {
				reader.backToMark();
				throw new IOException();
			}			
			return new ResponsePing(id.toString(), transactionid);
		} finally {
			reader.pushMark();
		}
	}

	public void encode(OutputStream output) throws IOException {
		BenDiction diction = new BenDiction();
		diction.append("y", new BenString("r"));
		diction.append("t", new BenString(mTransactionId));
		BenDiction r = new BenDiction();
		r.append("id", new BenString(mId));
		diction.append("r", r);
		diction.encode(output);
	}
	
}
