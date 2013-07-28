package net.hetimatan.net.torrent.krpc;

import java.io.IOException;
import java.io.OutputStream;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.net.torrent.util.bencode.BenDiction;
import net.hetimatan.net.torrent.util.bencode.BenObject;
import net.hetimatan.net.torrent.util.bencode.BenString;

public class RequestPing {

	private String mId;
	public RequestPing(String id) {
		mId = id;
	} 
	public RequestPing decode(MarkableReader reader) throws IOException {
		reader.popMark();
		try {
			BenDiction diction = BenDiction.decodeDiction(reader);
			BenObject v = diction.getBenValue("q");
			if(v.getType() == BenObject.TYPE_STRI && "id".equals(v.toString())) {
			} else {
				reader.backToMark();
				throw new IOException();
			}
			BenObject a = v.getBenValue("a");
			if(a.getType() != BenObject.TYPE_DICT) {
				reader.backToMark();
				throw new IOException();
			}
			BenObject id = a.getBenValue("id");
			if(a.getType() != BenObject.TYPE_STRI) {
				reader.backToMark();
				throw new IOException();
			}			
			return new RequestPing(id.toString());
		} finally {
			reader.pushMark();
		}
	}

	public void encode(OutputStream output) throws IOException {
		BenDiction diction = new BenDiction();
		diction.append("y", new BenString("q"));
		diction.append("q", new BenString("ping"));
		BenDiction a = new BenDiction();
		a.append("id", new BenString(mId));
		diction.append("a", a);
		diction.encode(output);
	}
	
}
