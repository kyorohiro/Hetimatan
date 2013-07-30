package net.hetimatan.net.torrent.util.bencode;


import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;

import net.hetimatan.io.file.MarkableReader;

public class BenList extends BenObject {

	private LinkedList<BenObject> mList = null;

	public BenList() {
		super(TYPE_LIST);
		mList = new LinkedList<BenObject>();
	}

	public void push(BenObject value) {
		mList.addFirst(value);		
	}

	public void append(BenObject value) {
		mList.add(value);
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		boolean first = true;
		for(BenObject value : mList) {
			if(first) {
				builder.append(", ");
				first = false;
			}
			builder.append(value.toString());			
		}
		builder.append("]");
		return mList.toString();
	}

	public byte[] toByte() {
		return toString().getBytes();
	}

	public BenObject getBenValue(int location) {
		if(location > mList.size()){
			return new BenObject.NullObject();
		}
		return mList.get(location);
	}

	public BenObject getBenValue(int location, int type) throws IOException {
		BenObject tmp = getBenValue(location);
		if(tmp.getType() != type) {
			throw new IOException();
		}
		return tmp;
	}

	@Override
	public int size() {
		return mList.size();
	}

	@Override
	public void encode(OutputStream output) throws IOException {
		output.write('l');
		for(BenObject v : mList) {
			v.encode(output);
		}
		output.write('e');
	}


	//
	// benlist      : "l" listelements "e"
	// listelements : benobject (benobject)*
	//
	public static BenList decodeList(MarkableReader input) throws IOException {
		log("decodeList:"+input.getFilePointer()+",");
		try {
			input.pushMark();
			if(!checkHead(input, (byte)'l')) {
				throw new IOException("");
			} else {
				input.read();
			}

			BenList list = new BenList();
			do {
				if (input.peek() == 'e') {
					input.read();
					break;					
				} else if(-1 == input.peek()) {
					input.backToMark();
					throw new IOException();
				} else {
					list.append(decodeValue(input));
				}
			} while(true);
			return list;
		} finally {
			input.popMark();
		}
	}
}
