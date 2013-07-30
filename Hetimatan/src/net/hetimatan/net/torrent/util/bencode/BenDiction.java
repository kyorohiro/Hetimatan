package net.hetimatan.net.torrent.util.bencode;


import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import net.hetimatan.io.file.MarkableReader;

public class BenDiction extends BenObject {
	private Map<String, BenObject> mDict = new LinkedHashMap<String, BenObject>();

	public BenDiction() {
		super(TYPE_DICT);
	}

	public void append(String key, BenObject value) {
		mDict.put(key, value);
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		boolean first = true;
		Set<String> keys = mDict.keySet();
		for(String key: keys) {
			if(first) {
				builder.append(", ");
				first = false;
			}
			builder.append(""+key+":"+mDict.get(key));			
		}
		builder.append("}");
		return mDict.toString();
	}

	public byte[] toByte() {
		return toString().getBytes();
	}

	@Override
	public BenObject getBenValue(String key) {
		if(!mDict.containsKey(key)){
			return new BenObject.NullObject();
		}
		return mDict.get(key);
	}

	public Set<String> getKeys() {
		return mDict.keySet();
	}

	public BenObject getBenValue(String key, int type) throws IOException {
		BenObject tmp = getBenValue(key);
		if(tmp == null || tmp.getType() != type) {
			throw new IOException();
		}
		return tmp;
	}

	@Override
	public int size() {
		return mDict.size();
	}

	@Override
	public void encode(OutputStream output) throws IOException {
		output.write('d');
		Set<String> keys = mDict.keySet();
		for(String key : keys) {
			byte[] buffer = key.getBytes("utf8");
			BenString bkey = new BenString(buffer, 0, buffer.length, "utf8");
			bkey.encode(output);
			mDict.get(key).encode(output);
		}
		output.write('e');
	}

	//
	// bendiction   : "d" dictelements "e" 
	// dictelements : benstring benobject (benstring benobject)*
	//
	public static BenDiction decodeDiction(MarkableReader input) throws IOException {
		log("decodeDiction:"+input.getFilePointer()+",");
		try {
			input.pushMark();

			if (!checkHead(input, (byte)'d')) {
				throw new IOException("");
			} else {
				input.read();
			}

			BenDiction list = new BenDiction();
			do {
				if(input.peek() == 'e') {
					input.read();
					break;					
				} else if(-1 == input.peek()) {
					input.backToMark();
					throw new IOException();
				} else {
					BenObject key = BenString.decodeString(input);
					list.append(key.toString(), decodeValue(input));
				}
			} while(true);
			return list;
		} finally {
			input.popMark();
		}
	}
}
