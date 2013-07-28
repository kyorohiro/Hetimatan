package net.hetimatan.net.torrent.krpc;

import java.io.IOException;
import java.io.OutputStream;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.net.torrent.util.bencode.BenDiction;
import net.hetimatan.net.torrent.util.bencode.BenObject;
import net.hetimatan.net.torrent.util.bencode.BenString;

public class RequestPing {

	private String mId;
	private String mTransactionId = "xx";

	public RequestPing(String id, String transactionId) {
		mId = id;
		mTransactionId = transactionId;
	} 

	public String getTransactionId() {
		return mTransactionId;
	}

	public String getId() {
		return mId;
	}

	public static RequestPing decode(MarkableReader reader) throws IOException {
		reader.popMark();
		String transactionid = "xx";
		try {
			BenDiction diction = BenDiction.decodeDiction(reader);
			{
				BenObject v = diction.getBenValue("y");
				if(v.getType() == BenObject.TYPE_STRI && "q".equals(v.toString())) {
				} else {
					reader.backToMark();
					throw new IOException();
				}
			}
			{
				BenObject v = diction.getBenValue("q");
				if(v.getType() == BenObject.TYPE_STRI && "ping".equals(v.toString())) {
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
			BenObject a = diction.getBenValue("a");
			if(a.getType() != BenObject.TYPE_DICT) {
				reader.backToMark();
				throw new IOException();
			}
			BenObject id = a.getBenValue("id");
			if(id.getType() != BenObject.TYPE_STRI) {
				reader.backToMark();
				throw new IOException(""+a.getType());
			}			
			return new RequestPing(id.toString(), transactionid);
		} finally {
			reader.pushMark();
		}
	}

	public void encode(OutputStream output) throws IOException {
		BenDiction diction = new BenDiction();
		diction.append("t", new BenString(mTransactionId));
		diction.append("y", new BenString("q"));
		diction.append("q", new BenString("ping"));
		BenDiction a = new BenDiction();
		a.append("id", new BenString(mId));
		diction.append("a", a);
		diction.encode(output);
	}
	
}
