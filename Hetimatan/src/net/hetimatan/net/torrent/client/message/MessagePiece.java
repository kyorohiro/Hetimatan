package net.hetimatan.net.torrent.client.message;


import java.io.IOException;
import java.io.OutputStream;

import net.hetimatan.io.file.KyoroFile;
import net.hetimatan.io.file.MarkableFileReader;
import net.hetimatan.io.file.MarkableReader;
import net.hetimatan.io.filen.KFNextHelper;
import net.hetimatan.io.filen.RACashFile;
import net.hetimatan.net.torrent.util.bencode.BenString;
import net.hetimatan.net.torrent.util.metafile.MetaFile;
import net.hetimatan.util.io.ByteArrayBuilder;
import net.hetimatan.util.url.PercentEncoder;

public class MessagePiece extends TorrentMessage {

	public static final String TAG = "piece";
	private int mIndex = 0;
	private int mBegin = 0;
	private KyoroFile mContent = null;

	
	public MessagePiece(int index, int begin, KyoroFile content) {
		super(TorrentMessage.SIGN_PIECE);
		try {
			System.out.println("new msp:"+index+","+begin+","+content.length());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mIndex = index;
		mBegin = begin;
		mContent = content;
	}


	public String toString() {
		int len = -1;
		try {
			len = (int)mContent.length();
		} catch (IOException e) {}
		return TAG+":"+mIndex+","+mBegin+","+len;
	}

	public int getIndex() {
		return mIndex;
	}

	public int getBegin() {
		return mBegin;
	}

	public KyoroFile getCotent() {
		return mContent;
	}

	@Override
	public void encode(OutputStream output) throws IOException {
		int length = 1+4*2+(int)mContent.length();
		RACashFile vf = new RACashFile(500, 2);
		vf.write(ByteArrayBuilder.parseInt(length, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN));
		vf.write(TorrentMessage.SIGN_PIECE);
		vf.write(ByteArrayBuilder.parseInt(mIndex, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN));
		vf.write(ByteArrayBuilder.parseInt(mBegin, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN));
		write(vf.getLastOutput(), mContent, 0, (int)mContent.length());
//		PercentEncoder en = new PercentEncoder();
//		System.out.println(""+length+","+KFNextHelper.newBinary(vf).length+","+mContent.length());
//		System.out.println(en.encode(KFNextHelper.newBinary(vf)));
		output.write(KFNextHelper.newBinary(vf));
//		output.write(ByteArrayBuilder.parseInt(length, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN));
//		output.write(TorrentMessage.SIGN_PIECE);
//		output.write(ByteArrayBuilder.parseInt(mIndex, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN));
//		output.write(ByteArrayBuilder.parseInt(mBegin, ByteArrayBuilder.BYTEORDER_BIG_ENDIAN));
//		System.out.println("ec ##"+mContent.length());
//		int len =write(output, mContent, 0, (int)mContent.length());
//		System.out.println("ec ::"+len);		
	}

	public boolean sha1check(MetaFile file) throws IOException {
		byte[] pieces = file.getPieces().toByte();
		int index = mIndex;
		int start = index*MetaFile.SHA1_LENGTH;
		int end = (index+1)*MetaFile.SHA1_LENGTH;
	
		MarkableFileReader reader = new MarkableFileReader(mContent, 512);
		BenString sha1 = MetaFile.createInfoSHA1(reader);
		byte[] shaBi = sha1.toByte();
		for(int i =0;i<MetaFile.SHA1_LENGTH;i++) {
			if(shaBi[i] != pieces[i+start]) {
				return false;
			}
		}
		return true;
	}

	public static MessagePiece decode(MarkableReader reader) throws IOException {
		int len = _length(reader);
		_signed(reader, TorrentMessage.SIGN_PIECE);
		int index = _decodeInt(reader);
		int begin = _decodeInt(reader);
		KyoroFile content = new RACashFile(len-9, 2);
		read(reader, content, begin, len-9);		
		return new MessagePiece(index, begin, content);
	}

}
